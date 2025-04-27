Entity Webservice
=================

__Entity Webservice__ is a Spring Boot application template that lets us set up model driven JSON API web service with
minimal effort. Its goal is to swiftly productionize a persistence webservice with modern CI/CD support.

Entity Webservice __is for__:

- non-business persistence capabilities
- performance-wise caching

It __is not for__:

- security aspect, such as Authentication or Authorization
- any business layer logics

For this reason, Entity Webservice is suitable for a microservice architecture.

Documentation
-------------

Spin up an Arango database

```console
docker run -d -p 8529:8529 \
       -e ARANGO_ROOT_PASSWORD=root \
       -v arango-data:/var/lib/arangodb3 \
       -v arango-app:/var/lib/arangodb3-apps \
       --name arangodb --platform linux/arm64/v8 arangodb
```

The ArangoDB Web console should be accessible at http://localhost:8529 with the username and password being `root` and
`root`, respectively.

> [!TIP]
>
> For more documentation on navigating the Arango Web UI, please refer to
> [ArangoDB documentation](https://arango.qubitpi.org/stable/components/web-interface/)

```console
git clone git@github.com:QubitPi/entity-ws.git
cd entity-ws
mvn clean package

export ENTITY_WS_ARANGO_HOSTS=http://localhost:8529
export ENTITY_WS_ARANGO_USERNAME=root
export ENTITY_WS_ARANGO_PASSWORD=root

java -jar target/entity-ws-0.0.1-SNAPSHOT.jar
```

Note that the `ENTITY_WS_ARANGO_USERNAME` and the `ENTITY_WS_ARANGO_PASSWORD` is in accordance with the ArandoDB Docker
configuration above. In addition, `ENTITY_WS_ARANGO_HOSTS` must start with either "__http://__" or "__https://__"

The default port is 8080.

- Healthcheck: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Creating an entity:

  ```console
  curl --location 'localhost:8080/arango/createDocument' --header 'Content-Type: application/json' --data '{
      "database": "myvalue",
      "collection": "myvalue",
      "document": {
          "myfield": "myvalue"
      }
  }' -v
  ```

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
