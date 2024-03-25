import { BellOutlined } from '@ant-design/icons';
import { ProList } from '@ant-design/pro-components';
import { Badge, Space, Tag, Drawer } from 'antd';
import React, { useEffect, useState } from 'react';
import ComponentMapper from './component-mapper'

type Message = {
    id: string;
    alreadyRead: boolean;
    title: string;
    content: string;
    data?: any;
    component?: string;
}

type MessageViewProps = {
    title: string;
    component: string;
    data?: any;
    open: boolean;
    onClose?: () => void;
}

export const MessageView: React.FC<MessageViewProps> = ({ title, data, component, open, onClose }) => {
    const [actions, setActions] = React.useState<React.ReactNode>([]);
    const TargetComponent = ComponentMapper[component]
    return <Drawer title={title} width="75%" placement="right" closable={false} onClose={onClose} open={open} footer={<Space>{actions}</Space>} >
        <TargetComponent setActions={setActions} input={data} />
    </Drawer>
}

export const MessageList: React.FC = () => {
    const [openedMessage, setOpenedMessage] = useState<Message | null>(null);
    return <>
        <ProList<Message>
            rowKey="id"
            headerTitle="消息中心"
            showActions="hover"
            request={async (
                params
            ) => {
                const response = await fetch(`/api/portal/loadMessage?current=${params.current || 0}`);
                const result = await response.json();
                return {
                    data: result.data.data,
                    success: result.status === 200,
                    total: result.data.total,
                };
            }}
            onItem={(item) => {
                return {
                    onClick: (event) => {
                        fetch(`/api/portal/readMessage/${item.id}`, { method: 'POST' });
                        if (item.component) {
                            setOpenedMessage(item);
                        }
                    },
                }
            }}
            metas={{
                title: {
                    dataIndex: 'title',
                },
                description: {
                    dataIndex: 'content',
                },
                subTitle: {
                    render: (_, record) => {
                        return record.alreadyRead ? [] : (
                            <Space size={0}>
                                <Tag color="red">未读</Tag>
                            </Space>
                        );
                    },
                },
            }}
        />
        {openedMessage && openedMessage.component && <MessageView title={openedMessage.title} component={openedMessage.component} data={openedMessage.data} open={openedMessage != null} onClose={() => setOpenedMessage(null)} />}
    </>
}

export const MessageIcon: React.FC = () => {
    const [hasUnreadMessage, setHasUnreadMessage] = useState<boolean>(false);

    const fetchData = async () => {
        try {
            const response = await fetch('/api/portal/hasUnreadMessage');
            const result = await response.json();
            setHasUnreadMessage(result.data);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    useEffect(() => {
        const intervalId = setInterval(fetchData, 60000);
        fetchData()
        return () => clearInterval(intervalId);
    }, []);
    return <Badge dot={hasUnreadMessage}>
        <BellOutlined onClick={() => {
            location.href = '/#/message';
        }} />
    </Badge>
};