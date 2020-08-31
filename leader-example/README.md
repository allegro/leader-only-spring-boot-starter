# Example

This is an example application. It has single endpoint `/greetings/{name}`.
By default, it starts at port `8181`. With applications starts also a Zookeeper instance.
The application connects to it and checks, if can be a new leader.

# How to run?

Start Zookeeper. You can use a preconfigured [Docker Compose file](./docker-compose.yaml). 
It will start Zookeeper at port `22181`.

```shell script
docker-compose up
```

Next, start first Spring Boot application.

```shell script
./gradlew bootRun --args="--curator-leadership.connection-string=localhost:22181"
```

Start next instances of this application 
to check if selecting leader works properly. All you need to do
is to start it second time with different port than `8181`.

```shell script
./gradlew bootRun --args="--curator-leadership.connection-string=localhost:22181 --server.port=8282"
```

# Check in action

Run following command. It should respond with ```Hello Jenny```, if `8181` is the leader.

```shell script
curl localhost:8181/greetings/Jenny
```

See, if other instances work properly. They should respond with empty body.

```shell script
curl localhost:8282/greetings/Jenny
```