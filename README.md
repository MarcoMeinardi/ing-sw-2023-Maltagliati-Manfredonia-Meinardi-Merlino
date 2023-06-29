# Prova Finale di Ingegneria del Software - AA 2022-2023

## Members
 - Ludovico Maltagliati
 - Riccardo Manfredonia
 - Marco Meinardi
 - Lorenzo Merlino

## Functionalities

| Requirements          |    Implemented     |
|-----------------------|:------------------:|
| Complete Rules        | :white_check_mark: |
| Socket Connection     | :white_check_mark: |
| RMI Connection        | :white_check_mark: |
| CLI                   | :white_check_mark: |
| GUI                   | :white_check_mark: |
| **Advanced features** |  **Implemented**   |
| Concurrent games      | :white_check_mark: |
| Persistency           | :white_check_mark: |
| Disconnection-proof   | :white_check_mark: |
| Chat                  | :white_check_mark: |

## Compilation
Before building the application, you must place the copyrighted images regarding the game UI in the `src/main/resources/img`.
After that, you can build the project running the following commands:
```
mvn clean test  # Optional, to launch tests
mvn clean
mvn compile assembly:single -Pserver
mvn compile assembly:single -Pclient
```
The created jars are:
 - `target/MyShelfie.jar` for client
 - `target/MyShelfie-server.jar` for server

Precompiled jars can be found [here](deliverables/final/jar)

## Execution
The project has been tested on Java19.

To start the game, run the following commands:
```
/path/to/java -jar MyShelfie-server.jar  # server
/path/to/java -jar MyShelfie.jar [-cli]  # client
```
Add the `-cli` flag to start the CLI; by default, the GUI is launched.

Socket connection uses the 8000 port, while 8001 port and 10000 port and above are used for RMI connection and its services.

## JavaDoc
[JavaDoc](docs)

## UML
[UML](delivarables/final/uml)
