# SOPRA group 23

### Introduction
**Wevent** is an interactive Event planning tool.  Thereby, Wevent enables its customers to create and engage in live Event planning sessions with their friends or colleagues. In these sessions invited Users can decide what task each individual has to perform. Additionally, what makes Wevent special is that it has the dimension of public and private events. For Private Events only invited Users can take part in the event planning session. However, for Public events, every registered user can take part in the event planning session and unregistered Users can see the event details. Other special features that we added is a Google Maps interface of all the location of public events as well as your specific private event and an Email Notification system.

### Technologies
For the backend we used Java, Spring Boot and Gradle. For the testing we used Mokito and JUnit. Additionally, we used postman to test the endpoints manually. Additionally, to generate the E-Mails, we used a yahoo account and its corresponding API.

#### Important links
-  View the API documentation of the application: https://sopra-fs22-group-23-server.herokuapp.com//swagger-ui/index.html#

### High-level Components
We have identified four main components in our system. 
- The first is the [User entity](https://github.com/sopra-fs22-group-23/server/tree/master/src/main/java/ch/uzh/ifi/sopra22/entity/User.java). The User entity is used to store a User. As the user is one of the main parts, and the client also wants to gather different information about the user, we had to implement a [User Service](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/service/UserService.java) and a [User Controller](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/controller/UserController.java). Thereby, the User Service updates and interprets the User request. The User Controller is the access point that receives this request and sends the respective requests to the User Service.
- The Second important high-level Component is the Event. Thereby, we also have the same three dimensions as explained by the User ([Entity](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/entity/Event.java), [Service](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/service/EventService.java), and [Controller](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/controller/EventController.java)). This store, modify and interpret, and receive the request from the client, in respective order. Therefore the event Entity has all the information to describe an Event. Furthermore, the main dimension of the Event is that it has the Dimension of a public or private event. Thereby, the main logic is also implemented around this attribute. Additionally, to add a User to the Event, we create an [EventUser](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/entity/EventUser.java). This Event User represents a specific user, that is then mapped to an Event. The Event User has the Special Categories of Role and Status. 
- The third main component is the [Event Task](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/entity/EventTask.java). The Event Task enables us to add tasks to an event. Those tasks can then be added to a specific user. To do this, one has to have special [endpoints](https://github.com/sopra-fs22-group-23/server/blob/master/src/main/java/ch/uzh/ifi/sopra22/controller/EventTaskController.java) to modify the specific task entities.
- The final important high-level component is the [WebSockets](https://github.com/sopra-fs22-group-23/server/tree/master/src/main/java/ch/uzh/ifi/sopra22/websockets) to enable a live and collaborative feature. Thereby, we need to use Websockets for this functionality, as we want to change from a pull to push endpoint implementation. This is important so that users can move the tasks in real-time for a specific event.

### Launch and Deployment

This repository can be downloaded and run via Gradle. Thereby, Gradle will install all needed dependencies and automatically run tests if the project is being built. The implementation of new features will trigger the JUnit tests automatically when the project is rebuild.

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
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Roadmap
The three main features that could be implemented in the backend are the following:
1. Save guests. We want to save unregistered users, this gives us the opportunity to send update statuses as well as reminders.
2. Save the Pictures in an external API. At the moment we save the pictures on the server, however, this is a suboptimal solution. Therefore it should be refactored to save the pictures on an external Database.
3. Use a persistent Database. At the moment we use a non-persistent Database, therefore from time to time the Data is deleted. However, to ramp this up, a persistent database should be used.

### Authors and acknowledgment
The authors of this Application are the UZH SoPra Group 23:
- Adam Bauer
- Mark Düring
- Wesley Müri
- Paolo Tykko
- Kai Zinnhardt

Further aknowledgements go to:
- Samuel Brügger (TA)
- Tomas Fritz, Prof. Dr.

### [License](https://github.com/sopra-fs22-group-23/server/blob/master/LICENSE) &copy;


# Old README, to be Deleted!!!
### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

# Links from before

## Getting started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/

## Setup this Template with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions will help you to run it more easily:
-   `pivotal.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`
-   `richardwillis.vscode-gradle`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs22` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle

You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).



### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

## Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing

Have a look here: https://www.baeldung.com/spring-boot-testing
