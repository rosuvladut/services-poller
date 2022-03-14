import React from "react";
import { Link } from "react-router-dom";
import Service from "../models/service";
import BaseService from "../service/base.service";
import * as toastr from "toastr";

function Del(Id?: string) {
  BaseService.delete("/services/", Id).then((rp) => {
    if (rp.Status) {
      toastr.success("Service deleted.");
      window.location.reload();
    } else {
      toastr.error(rp.Messages);
      console.log("Messages: " + rp.Messages);
      console.log("Exception: " + rp.Exception);
    }
  });
}

interface IProps {
  service: Service;
  index: Number;
}

const TableRow: React.FunctionComponent<IProps> = (props) => { 
  return (
    <tr>
      <td>{props.index}</td>
      <td>{props.service.Name}</td>
      <td>{props.service.Url}</td>
      <td>{props.service.CreatedOn}</td>
      <td>{props.service.Status === "UP" &&
              <button type="button" className="btn btn-success">UP</button>
      }
      {props.service.Status === "DOWN" &&
              <button type="button" className="btn btn-danger">DOWN</button>
      }
      {props.service.Status === "UNKNOWN" &&
              <button type="button" className="btn btn-light">UNKNOWN</button>
      }
      </td>
      <td>
        <Link to={"/edit/" + props.service.Id} className="btn btn-primary">
          Edit
        </Link>
      </td>
      <td>
        <button onClick={() => Del(props.service.Id)} className="btn btn-danger">
          Delete
        </button>
      </td>
    </tr>
  );
};
export default TableRow;
