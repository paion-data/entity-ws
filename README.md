Entity Webservice
=================

__Entity Webservice__ is a Spring Boot application template that lets us set up model driven JSON API web service with
minimal effort.

Documentation
-------------

```console
git clone git@github.com:QubitPi/entity-ws.git
cd entity-ws
mvn clean package
java -jar target/entity-ws-0.0.1-SNAPSHOT.jar
```

- Healthcheck: http://localhost:8080/actuator/health

Development
-----------

- Starting locally:

  ```console
  mvn clean package
  java -jar target/entity-ws-0.0.1-SNAPSHOT.jar
  ```

- Running tests:

  ```console
  mvn clean verify
  ```
