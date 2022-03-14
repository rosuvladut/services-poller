# Services Poller
Spring application for checking status of Web services provided by users

Technologies:
- Spring Boot Reactive
- Java 11
- Lombok
- MongoDB
- Sockets with ApplicationEvents
- Maven

On Frontend:
- React with Typescript
- build using 'npm install' and start using 'npm start'


Applications allows the user to:
- view a list of existing services
- add a new service using name and URL
- edit an existing service
- delete a service

Using Websockets the changes are received on the UI once a change is made on an existing service or a new service is added.

