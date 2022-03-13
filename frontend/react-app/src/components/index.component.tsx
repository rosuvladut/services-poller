import * as React from "react";
import TableRow from "./TableRow";
import Service from "../models/service";
import BaseService from "../service/base.service";
import * as toastr from "toastr";
interface IProps {}
interface IState {
  listServices: Array<Service>;
  isReady: Boolean;
  hasError: Boolean;
}

class Index extends React.Component<IProps, IState> {
  public state: IState = {
    listServices: new Array<Service>(),
    isReady: false,
    hasError: false,
  };
  constructor(props: IProps) {
    super(props);
    this.state = {
      isReady: false,
      listServices: Array<Service>(),
      hasError: false,
    };
  }

  public async componentDidMount() {
    this.setState({isReady: false});

    const response = await fetch('http://localhost:3000/services');
    const data = await response.json();
    const listServices = new Array<Service>();

    (data || []).forEach((p: any) => {
      listServices.push(new Service(p.id, p.name, p.url, p.createdOn, p.status));
    });

    this.setState({ listServices: listServices }); 
    this.setState({ isReady: true });

    const socket = new WebSocket('ws://localhost:3000/ws/services'); 
    socket.addEventListener('message', async (event: any) => { 
      const service = JSON.parse(event.data);
      this.state.listServices.push(new Service(service.id, service.name, service.url, service.createdOn, service.status));
      this.setState({listServices: this.state.listServices});
      this.setState({ isReady: true });
    });
    // BaseService.getAll<Service>("/services").then((rp) => {
    //   if (rp.Status) {
    //     const data = rp.Data;
    //     const listServices = new Array<Service>();

    //     (data || []).forEach((p: any) => {
    //       listServices.push(new Service(p._id, p.name, p.url));
    //     });

    //     this.setState({ listServices: listServices }); 
    //     this.setState({ isReady: true });
    //   } else {
    //     this.setState({ isReady: true });
    //     this.setState({ hasError: true });
    //     console.log("Messages: " + rp.Messages);
    //     console.log("Exception: " + rp.Exception);
    //   }
    // });

    setTimeout(() => {
      if (!this.state.isReady) {
        toastr.info(
          "It is possible that the service is being restarted, please wait more ...",
          "",
          { timeOut: 8000 }
        );
      }

      if (this.state.hasError) {
        toastr.error(
          "An error occurred!",
          "",
          { timeOut: 8000 }
        );
      }
    }, 2000);
  }

  public tabRow = () => {
    if (!this.state.isReady) {
      return (
        <tr>
          <td colSpan={6} className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </td>
        </tr>
      );
    }
    if (this.state.hasError) {
      return (
        <tr>
          <td colSpan={6} className="text-center">
            <div className="alert alert-danger" role="alert">
              An error occurred!
            </div>
          </td>
        </tr>
      );
    }
    

    return this.state.listServices.map(function (object, i) {
      return <TableRow key={i} index={i + 1} service={object} />;
    });
  };

  public render(): React.ReactNode {
    return (
      <div>
        <h3 className="text-center">Services List</h3>
        <table className="table table-striped" style={{ marginTop: 20 }}>
          <thead>
            <tr>
              <th>Index</th>
              <th>Name</th>
              <th>Url</th>
              <th>Added On</th>
              <th>Status</th>
              <th className="text-center" colSpan={2}>
                Action
              </th>
            </tr>
          </thead>
          <tbody>{this.tabRow()}</tbody>
        </table>
      </div>
    );
  }
}
export default Index;
