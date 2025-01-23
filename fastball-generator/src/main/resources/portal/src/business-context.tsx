import { Select } from 'antd';
import React, { useEffect, useState } from 'react';
import { useFastballContext } from 'fastball-frontend-common';
import { buildJsonRequestInfo } from './utils'

type BusinessContextItem = {
    id: string;
    title: string;
}

export const BusinessContextSelector: React.FC = () => {
    const { getCurrentBusinessContextId, setCurrentBusinessContextId } = useFastballContext();
    const [businessContextItems, setBusinessContextItems] = useState<BusinessContextItem[]>([]);

    const onChange = (value: string) => {
        setCurrentBusinessContextId(value);
        location.reload();
    }

    const fetchData = async () => {
        try {
            const request = buildJsonRequestInfo()
            const response = await fetch('/api/portal/businessContextItems', request);
            const result = await response.json();
            const items: BusinessContextItem[] = result.data;
            if (!getCurrentBusinessContextId()?.length) {
                onChange(items[0].id);
            }
            setBusinessContextItems(items);
        } catch (error) {
            console.error('Error fetching business context data:', error);
        }
    };

    useEffect(() => {
        fetchData()
    }, []);

    const items = businessContextItems.map(item => ({ value: item.id, label: item.title }));

    return <Select
        style={{ width: 120 }}
        value={getCurrentBusinessContextId()}
        onChange={onChange}
        options={items}
    />
};