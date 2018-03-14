# ClubOk
Social Network for University Clubs

# Prerequisites
## Editing
- [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- IDE ([IntelliJ](), [Eclipse Java]())
- [Maven](https://maven.apache.org/)
- Create **development.properties** and **test.properties** files in [config](./clubok_be/src/main/java/dc/clubok/config)
directory with _port_, _mongodb_uri_, and _secret_ properties
- [Lombok plugin](https://projectlombok.org/)

## Running
- [MongoDB](https://www.mongodb.com/) and tools to work with Mongo ([Robo3T](https://robomongo.org/))
- Run MongoDB Server


# Running Tutorial:
## Using IDE
- `mvn clean install`
- Run [ClubOKService](./clubok_be/src/main/java/dc/clubok/ClubOKService.java) class

## Using JAR file
- `java -jar clubok_be/target/clubok_be-1.0-SNAPSHOT.jar`