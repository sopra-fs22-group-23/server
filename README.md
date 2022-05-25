# SOPRA group 23

### Introduction
**Wevent** is an interactive Event planning tool. Thereby, Wevent enables its customers to create and engage in live Event planning sessions with their friends or colleagues. In these sessions, invited Users can decide what task each individual has to perform. Additionally, what makes Wevent unique is its dimension of public and private events. For Private Events, only invited users can participate in the event planning session. However, every registered user can participate in the event planning session for public events, and unregistered Users can see the event planning details. Other notable features that we added are a Google Maps interface of all the locations of public events and for your specific private event (Google Maps API) and an Email Notification system.

### Technologies
For the backend, we used Java, Spring Boot, and Gradle. For the testing, we used Mokito and JUnit. Additionally, we used postman to test the endpoints manually. Furthermore, to generate the E-Mails, we used a yahoo account and its corresponding API.

#### Important links
View the API documentation of the application to get an overview of our Endpoints. The link is the following: https://sopra-fs22-group-23-server.herokuapp.com//swagger-ui/index.html#

### High-level Components
We have identified four main components in our system. 
- The first is the [User entity](https://github.com/sopra-fs22-group-23/server/tree/master/src/main/java/ch/uzh/ifi/sopra22/entity/User.java). The User entity is used to store a User. As the User is one of the main parts, and the client wants to access and modify various information about the User, we implemented a [User Service](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/service/UserService.java) and a [User Controller](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/controller/UserController.java). Thereby, the User Service updates and interprets the User request. Additionally, the User Controller is a collection of endpoints that receives the request from the client and, after evaluation from the User Service, sends the responses back to the client.
- The Second crucial high-level Component is the Event. Thereby, we also have the same three dimensions as explained to the User. We have an Event ([Entity](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/entity/Event.java), [Service](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/service/EventService.java), and [Controller](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/controller/EventController.java)). These store, modify and interpret, and receive the request from the client, respectively. Therefore the event Entity has all the information to describe an Event. Furthermore, the central dimension of the Event is that it has the dimension of public and private events. Thereby, the main logic is implemented around this attribute. Additionally, to add a User to the Event, we create an [EventUser](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/entity/EventUser.java). This Event User represents a specific user that is then mapped to an Event. The Event User has the Special parameter such as Role and Status. 
- The third main component is the [Event Task](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/entity/EventTask.java). The Event Task enables us to add tasks to an event; those tasks can then be added to a specific user. One has to have special [endpoints](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/controller/EventTaskController.java) to modify the specific task entities.
- The final important high-level component is the [WebSockets](https://github.com/sopra-fs22-group-23/server/tree/master/src/main/java/ch/uzh/ifi/sopra22/websockets). The Websockets enable our live collaborative feature to work efficiently. We need to use Websockets for this functionality, as we want to change from a pull to push endpoint implementation. This is important so that users can move the tasks in real-time for a specific event, and the others get informed by the server that this move happened.

### Launch and Deployment

This repository can be downloaded and run via Gradle. Gradle will install all needed dependencies and automatically run tests if the project is being built. In addition, the implementation of new features will trigger the JUnit tests automatically when the project is rebuilt.

#### Build
```
./gradlew build
```
#### Run
```
./gradlew bootRun
```
#### Test
```
./gradlew test
```
#### Development Mode
You can start the backend in development mode; this will automatically trigger a new build and reload the application
once the file's content has been changed, you save the file.

To do this, start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

Additionally, if you want to avoid to trigger an automatic rebuild of the application you can use the following command instead:

`./gradlew build`

### Roadmap
The three main features that could be implemented in future backend development are:
1. Save guests. We want to save unregistered users, which allows us to send update statuses and reminders.
2. Save the images in an external API. At the moment, we save the images on the server; however, this is a suboptimal solution. Therefore it should be refactored to save the pictures on an external Database.
3. Use a persistent Database. At the moment, we use a non-persistent Database; therefore, from time to time, the Data is deleted. However, to ramp this up, a persistent database should be used.

### Authors and acknowledgment
The authors of this Application are the UZH SoPra Group 23:
- Adam Bauer
- Mark Düring
- Wesley Müri
- Paolo Tykko
- Kai Zinnhardt

Further aknowledgements go to:
- Samuel Brügger (TA)
- Thomas Fritz, Prof. Dr.

### [License](https://github.com/sopra-fs22-group-23/server/blob/master/LICENSE) &copy;
