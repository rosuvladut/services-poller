export default class Services {
    Id?: string;
    Name: string;
    Url: string;
    CreatedOn: string;
    Status: string;

    constructor(id: string,name: string, url: string, createdOn: string, status:string) {
        this.Id = id;
        this.Name = name;
        this.Url = url;
        this.CreatedOn = createdOn;
        this.Status = status;
    } 
}