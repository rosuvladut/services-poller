import React from 'react';
import * as toastr from 'toastr';
import Service from '../models/service';
import BaseService from '../service/base.service';
import { History } from 'history';
import { PersonPage } from './page.form'; 


interface IProps { 
    history: History;
    //Map properties match
    match:{ 
        isExact: boolean
        params: {
            id:string
        },
        path: string,
        url: string,
    }
}
interface IState {
    service: Service
}


export default class Edit extends React.Component<IProps, IState> {

    constructor(props: IProps) {

        super(props);

        this.state = {
            service: {
                Name: '',
                Url: '',
                Id: '',
                CreatedOn: '',
                Status: ''
            }
        }
        this.onFieldValueChange = this.onFieldValueChange.bind(this);

    }




    private onFieldValueChange(fieldName: string, value: string) { 
        const nextState = {
            ...this.state,
            service: {
                ...this.state.service,
                [fieldName]: value,
            }
        };

        this.setState(nextState);
    }

    public componentDidMount() { 
        BaseService.get<Service>('/services/', this.props.match.params.id).then(
            (rp) => {
                if (rp.Status) {
                    const service = rp.Data;
                    this.setState({ service: new Service(service.id, service.name,service.url, service.createdOn, service.status)});
                } else {
                    toastr.error(rp.Messages);
                    console.log("Messages: " + rp.Messages);
                    console.log("Exception: " + rp.Exception);
                }
            }

        );
    }


    private onSave = () => {

        console.log(this.state.service);
        BaseService.update<Service>("/service", this.props.match.params.id,this.state.service).then(
            (rp) => {
                if (rp.Status) {
                    toastr.success('Service saved.');
                    this.props.history.goBack();
                } else {
                    toastr.error(rp.Messages);
                    console.log("Messages: " + rp.Messages);
                    console.log("Exception: " + rp.Exception);
                }
            }
        );

    }
 
    render() {
        return (
            <PersonPage
                service={this.state.service}
                onChange={this.onFieldValueChange}
                onSave={this.onSave}
            />
        );
    }
}