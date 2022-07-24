## Error reporting

### Running the project
First start up Docker Compose (from the root of the project):
```
docker compose up
```

From the root of the project, start up the application:
```
mvn spring-boot:run
```

You will now start to see messages being produced and pushed to Kafka. These messages are then in turn picked up from
the Kafka queue and persisted in Cassandra. 
