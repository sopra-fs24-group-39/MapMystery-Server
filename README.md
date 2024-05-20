# MapMystery

## Introduction
**MapMystery** is a Geoguessr-like game designed to test your geographical knowledge and observational skills. The goal of this project is to create an engaging and educational experience that allows users to guess locations based on images provided by the Google API. This project is developed using Java with Springboot for the backend and React for the frontend.

## Technologies Used
- **Java** with **Springboot**: Backend development
- **Websockets**: Real-time communication
- **Spring Boot Starter Mail**, **JavaMailAPI**: Email service
- **JPA (Java Persistence API)**: Database management
- **Google API**: Image gathering for the game
- **JUnit**: Testing
- **Spring Security**: Password Management
- **SonarQube**: Static code analysis

## High-level Components

1. **UserController**: [link](https://github.com/sopra-fs24-group-39/MapMystery-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java): Manages endpoints for creating users, managing friend request and settings
2. **LobbyController** [link](https://github.com/sopra-fs24-group-39/MapMystery-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/LobbyController.java): Main game logic for initalising games both public and private, tracking scoring
3. **LobbyService** [link](https://github.com/sopra-fs24-group-39/MapMystery-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/LobbyService.java):
    - "putToSomeLobby" - Logic for a public player joining any lobby
    - "joinLobby" - Logic for joining a specific Lobby can be private just requires authentication parameter
    - "kickOutInactivePlayers" - Logic for removing players who have left the lobby
    - "createAndSendLeaderBoard" - tallying points and returing to the client side interface.
4. **StreetViewService**: [link](https://github.com/sopra-fs24-group-39/MapMystery-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/StreetViewService.java): Managing API calls to google, has **exposed** API_KEY


## Launch & Deployment
To get started with **MapMystery**, follow these steps:

1. **Clone the Repository**:
    ```sh
    git clone <repository_url>
    ```

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing with Postman

We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.


## Roadmap
Future enhancements and features for **MapMystery**:

1. Better management of exposed API_KEYS and EMAIL information
2. Addition of different rounds for main gamemode **Globe Guesser**
       - A countries based point system (in the same country = max points)
       - Head to head battle with depleting score
       - Custom maps with only certain locations
3. Improvement of **chat** functionality to allow individual chats between players
4. Implementation of tip system for developers

## Authors and Acknowledgment
- **Sam Wallace**: backend developer
- **David Sanchez** backend developer
- **Nils Jacobi**: frontend developer
- **Tim**: frontend developer
- **Joshua Winterflood** frontend/backend developer

Special thanks to all contributors and testers.

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details.
