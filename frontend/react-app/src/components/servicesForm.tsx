import * as React from 'react';
import Service from '../models/service';

import { Input, Button } from '../common/components/form';

interface Props {
    service: Service;
    onChange: (fieldName: string, value: string) => void;
    onSave: () => void;
}

export const ServiceForm: React.FunctionComponent<Props> = (props) => { 
    return (
        <form>
            <h1>Manage service</h1>

            <Input
                name="Name"
                label="Name"
                value={props.service.Name}
                onChange={props.onChange}
            />

            <Input
                name="Url"
                label="Url"
                value={props.service.Url}
                onChange={props.onChange}
            />

            <Button
                label="Save"
                className="btn btn-success mt-2"
                onClick={props.onSave}
            />
        </form>
    );
};
