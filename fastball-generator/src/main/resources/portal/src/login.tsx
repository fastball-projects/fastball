import React, { useState } from 'react';
import {
    LockOutlined,
    MobileOutlined,
    UserOutlined
} from '@ant-design/icons';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Alert, message, Tabs } from 'antd';
import {
    LoginForm,
    ProFormCaptcha,
    ProFormCheckbox,
    ProFormText,
    DefaultFooter
} from '@ant-design/pro-components';
import './login.scss'

const TOKEN_LOCAL_KEY = 'fastball_token';
const REDIRECT_URL_QUERY_KEY = 'redirectUrl';


const LoginMessage: React.FC<{
    content: string;
}> = ({ content }) => {
    return (
        <Alert
            style={{
                marginBottom: 24,
            }}
            message={content}
            type="error"
            showIcon
        />
    );
};

const Footer: React.FC = () => {
    const currentYear = new Date().getFullYear();

    return (
        <DefaultFooter
            style={{
                background: 'none',
            }}
            copyright={`${currentYear} 杭州范数科技有限公司`}
            links={[
                {
                    key: 'Fastball',
                    title: 'Fastball',
                    href: 'https://fastball.dev',
                    blankTarget: true,
                }]
            }
        />
    );
};

const Login: React.FC = () => {
    const [loginFailMessage, setLoginFailMessage] = useState<string>('');
    const [type, setType] = useState<string>('account');

    const containerClassName = useEmotionCss(() => {
        return {
            display: 'flex',
            flexDirection: 'column',
            height: '100vh',
            overflow: 'auto',
            backgroundImage:
                "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
            backgroundSize: '100% 100%',
        };
    });

    const handleSubmit = async (values: any) => {
        try {
            const resp = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(values)
            })
            const json = await resp.text();
            const result = JSON.parse(json);
            if (result.status === 200) {
                message.success(result.message);
                localStorage.setItem(TOKEN_LOCAL_KEY, JSON.stringify(result.data));
                const urlParams = new URL(window.location.href).searchParams;
                location.href = urlParams.get(REDIRECT_URL_QUERY_KEY) || '/'
                return;
            } else {
                setLoginFailMessage(result.message);
                // message.error(`Login error ${result.status}: ${result.message}`);
            }
        } catch (error) {
            console.log(error);
            message.error('登录失败，请重试！');
        }
    };

    return (
        <div className={containerClassName}>
            <div
                style={{
                    flex: '1',
                    padding: '32px 0',
                }}
            >
                <LoginForm
                    contentStyle={{
                        minWidth: 280,
                        maxWidth: '75vw',
                    }}
                    logo={<img alt="logo" src="/logo.svg" />}
                    title="Fastball"
                    subTitle="一款面向 Java 开发人员的界面开发框架"
                    initialValues={{
                        autoLogin: true,
                    }}
                    onFinish={async (values: any) => {
                        await handleSubmit(values);
                    }}
                >
                    <Tabs
                        activeKey={type}
                        onChange={setType}
                        centered
                        items={[
                            {
                                key: 'account',
                                label: '账户密码登录',
                            },
                            {
                                key: 'mobile',
                                label: '手机号登录',
                            },
                        ]}
                    />

                    {loginFailMessage && (
                        <LoginMessage
                            content={loginFailMessage}
                        />
                    )}

                    {type === 'account' && (
                        <>
                            <ProFormText
                                name="username"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <UserOutlined />,
                                }}
                                placeholder='用户名:'
                                rules={[
                                    {
                                        required: true,
                                        message: "请输入用户名!",
                                    },
                                ]}
                            />
                            <ProFormText.Password
                                name="password"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined />,
                                }}
                                placeholder='密码: '
                                rules={[
                                    {
                                        required: true,
                                        message: "请输入密码！",
                                    },
                                ]}
                            />
                        </>
                    )}
                    {type === 'mobile' && (
                        <>
                            <ProFormText
                                fieldProps={{
                                    size: 'large',
                                    prefix: <MobileOutlined />,
                                }}
                                name="mobile"
                                placeholder='手机号'
                                rules={[
                                    {
                                        required: true,
                                        message: "请输入手机号！",
                                    },
                                    {
                                        pattern: /^1\d{10}$/,
                                        message: "手机号格式错误！",
                                    },
                                ]}
                            />
                            <ProFormCaptcha
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined />,
                                }}
                                captchaProps={{
                                    size: 'large',
                                }}
                                placeholder='请输入验证码'
                                captchaTextRender={(timing, count) => {
                                    if (timing) {
                                        return `${count} '获取验证码'`;
                                    }
                                    return '获取验证码';
                                }}
                                name="captcha"
                                rules={[
                                    {
                                        required: true,
                                        message: "请输入验证码！",
                                    },
                                ]}
                                onGetCaptcha={async (phone) => {
                                    // const result = await getFakeCaptcha({
                                    //     phone,
                                    // });
                                    // if (!result) {
                                    //     return;
                                    // }
                                    message.success('获取验证码成功！验证码为：1234');
                                }}
                            />
                        </>
                    )}
                    <div
                        style={{
                            marginBottom: 24,
                        }}
                    >
                        <ProFormCheckbox noStyle name="autoLogin">
                            自动登录
                        </ProFormCheckbox>
                        <a
                            style={{
                                float: 'right',
                            }}
                        >
                            忘记密码
                        </a>
                    </div>
                </LoginForm>
            </div>
            <Footer/>
        </div>
    );
};

export default Login;