import React from 'react';
import * as toastr from 'toastr';
import Service from '../models/service';
import BaseService from '../service/base.service';
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


export default class Create extends  React.Component<IProps, IState> {
    constructor(props:IProps) {
        super(props);
         
        this.state = {
            service: {
                Name: '',
                Url: '',
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
    private onSave = () => { 
        BaseService.create<Service>("/services", this.state.service).then(
            (rp) => {
                console.log(rp);
                if (rp.Status) {
                    toastr.success('New Service saved.'); 


                    this.setState({
                        service: {
                            Name: '',
                            Url: '',
                            Id: '',
                            CreatedOn: '',
                            Status: ''
                        }
                    });
                     
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