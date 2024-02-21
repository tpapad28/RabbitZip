# RabbitZip

## Summary

A proof-of-concept project, demonstrating compression of RabbitMQ messages with minimal changes to existing
implementation.

## Usage

You need a running RabbitMQ server (as configured in `application.properties`).
A simple solution would be:

```bash
docker run -p 5672:5672  rabbitmq
```

Then build with:

```bash
mvn install
```

And finally run:

```bash
java -jar target/RabbitZip-1.0-SNAPSHOT.jar
```