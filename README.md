# ClubOk
**[Backend](./clubok_be) _([API Documentation](https://clubok.docs.apiary.io/#))_ | [Frontend](./clubok_fe)**

Social Network for University Clubs

## Development
### Prerequisites
- [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- IDE ([IntelliJ](), [Eclipse Java]())
- [Maven](https://maven.apache.org/)
- Create **development.properties** and **test.properties** files in [config](./clubok_be/src/main/java/dc/clubok/config)
directory with _port_, _mongodb_uri_, and _secret_ properties
- [Lombok plugin](https://projectlombok.org/)

## How to run?
### Getting started
In order to run the project you have to have the following prerequisites:
- [MongoDB](https://www.mongodb.com/) and tools to work with Mongo ([Robo3T](https://robomongo.org/))
- Run MongoDB Server

There are several ways to run the server, using IDE, or using compiled `.jar` file

#### Using IDE
- `mvn clean install`
- Run [ClubOKService](./clubok_be/src/main/java/dc/clubok/ClubOKService.java) class

#### Using JAR file
- `java -jar clubok_be/target/clubok_be-1.0-SNAPSHOT.jar`

### Project Team:
- Anuar Otynshin
- Alexey Muryshkin
- Rustam Shumenov
- Madiyar Aitzhanov
- Aibek Ziyashev
