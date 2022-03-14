import * as React from 'react';
import { Link } from "react-router-dom";
interface IProps {
}
interface IState {
}

class Home extends React.Component<IProps, IState> {


    public componentDidMount() {

    }

    public render(): React.ReactNode {
        return (
            <div>
                <div className="px-4 py-5 my-5 text-center">
                        <h1 className="display-5 fw-bold">Services Poller Application</h1>
                        <div className="col-lg-6 mx-auto">
                            <p className="lead mb-4">App created using React</p>
                            <div className="d-grid gap-2 d-sm-flex justify-content-sm-center">
                                <a type="button" className="btn btn-primary btn-lg px-4 gap-3"
                                 href="https://github.com/rosuvladut">Go to GitHub</a>

                                  <Link to={'/index'} className="btn btn-outline-secondary btn-lg px-4">Services List</Link> 
                            </div>
                        </div>
                </div>

            </div>
        );
    }
}
export default Home;