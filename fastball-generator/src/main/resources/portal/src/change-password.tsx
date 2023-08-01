import {
    ProForm,
    ProFormInstance,
    ProFormText,
} from '@ant-design/pro-components';
import { message } from 'antd';
import { useRef } from 'react';

const TOKEN_LOCAL_KEY = 'fastball_token';

const waitTime = (time: number = 100) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(true);
        }, time);
    });
};

const ChangePasswordForm = () => {
    const formRef = useRef<ProFormInstance>();
    return <ProForm<{
        password: string;
        newPassword?: string;
        confirmPassword?: string;
    }>
        formRef={formRef}
        onFinish={async () => {
            const values = await formRef.current?.validateFieldsReturnFormatValue?.();
            if (values.newPassword != values.confirmPassword) {
                message.error('新密码与确认密码不一致, 请确认');
                return;
            }
            const resp = await fetch('/api/portal/changePassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(values)
            })
            const json = await resp.text();
            const result = JSON.parse(json);
            if (result.status === 200) {
                message.success("修改成功, 请使用新密码重新登录");
                localStorage.removeItem(TOKEN_LOCAL_KEY)
                waitTime(1000)
                location.href = '/#/login'
            } else {
                message.error(result.message);
            }
        }}
        autoFocusFirstInput
    >
        <ProForm.Group>
            <ProFormText.Password
                width="md"
                name="password"
                required
                label="当前密码"
                placeholder="请输入当前密码"
                rules={[{ required: true, message: '请输入当前密码' }]}
            />
            <ProFormText.Password
                width="md"
                name="newPassword"
                required
                label="新密码"
                placeholder="请输入新密码"
                rules={[{ required: true, message: '请输入新密码' }]}
            />
            <ProFormText.Password
                width="md"
                name="confirmPassword"
                required
                label="确认密码"
                placeholder="请输入确认密码"
                rules={[{ required: true, message: '请输入确认密码' }]}
            />
        </ProForm.Group>
    </ProForm>
}

export default ChangePasswordForm;