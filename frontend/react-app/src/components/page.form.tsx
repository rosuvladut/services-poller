import * as React from 'react';
import Service from '../models/service';
import { ServiceForm } from './servicesForm';

interface IProps {
    service: Service;
    onChange: (fieldName: string, value: string) => void;
    onSave: () => void;
}

export const PersonPage: React.FunctionComponent<IProps> = (props: IProps) => {  
    return (
        <ServiceForm
            service={props.service}
            onChange={props.onChange}
            onSave={props.onSave}
        />
    );
}
