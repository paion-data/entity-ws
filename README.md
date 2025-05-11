Entity Webservice
=================

![Java Version Badge][Java Version Badge]
[![API Doc Badge]][API Doc URL]
[![Docker Hub][Docker Pulls Badge]][Docker Hub URL]
[![Apache License Badge]][Apache License, Version 2.0]

__Entity Webservice__ is a Spring Boot application template that lets us set up model driven JSON API web service with
minimal effort. Its goal is to swiftly productionize a persistence webservice with modern CI/CD support.

Entity Webservice is designed for:

- non-business persistence capabilities
- performance-wise caching

It __is not for__:

- security aspect, such as Authentication or Authorization
- any business layer logics

For this reason, Entity Webservice is suitable for a microservice architecture.

Quick Start
-----------

### Getting Source Code

```console
git clone git@github.com:QubitPi/entity-ws.git
cd entity-ws
```

### Setting Environment Variables

> [!TIP]
>
> One change the values of `ENTITY_WS_ARANGO_USERNAME` and `ENTITY_WS_ARANGO_PASSWORD` to anything else. The values here
> are examples only.

```console
export ENTITY_WS_ARANGO_HOSTS=http://arango-db:8529 && \
export ENTITY_WS_ARANGO_USERNAME=root && \
export ENTITY_WS_ARANGO_PASSWORD=root && \
export ARANGO_ROOT_PASSWORD=$ENTITY_WS_ARANGO_PASSWORD
```

### Spinning Up Services

There is a reference [docker-compose.yml](./docker-compose.yml) where we could use for production. Simply execute

```console
docker compose up
```

When we see the output like the following

```text
arango-db-1  | 2025-05-11T15:26:29Z [1] INFO [cf3f4] {general} ArangoDB (version 3.11.13 [linux]) is ready for business. Have fun!
entity-ws-1  |
entity-ws-1  |   .   ____          _            __ _ _
entity-ws-1  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
entity-ws-1  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
entity-ws-1  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
entity-ws-1  |   '  |____| .__|_| |_|_| |_\__, | / / / /
entity-ws-1  |  =========|_|==============|___/=/_/_/_/
entity-ws-1  |
entity-ws-1  |  :: Spring Boot ::                (v3.4.4)
entity-ws-1  |
...
entity-ws-1  | 2025-05-11T15:27:29.539Z  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
```

it means we are ready to play with Entity WS. Let's create a book entity first

```console
curl -X 'POST' \
  'http://localhost:8080/arango/createDocument/library/books' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "Pride and Prejudice",
  "author": "Jane Austen"
}'
```

Next we will verify the data has been saved into database. The quick start uses [ArangoDB](https://arango.qubitpi.org/)
as the persistence database. We can first hit the Arando web console at __http://localhost:8529/__ which should take us to a
page like the following:

![ArangoDB web console login](./docs/arango-login.png "Error loading docs/arango-login.png").

Use __root__ and __root__ as username and password, respectively. Then navigate to the data page by __Login__ ->
selecting __library__ in database dropdown menu -> click __Select DB: library__ -> click __books__
[collection](https://arango.qubitpi.org/stable/concepts/data-structure/collections/) -> click __Content__ in the top
navigation bar. As we can see the data already exists as an
[Arango document](https://arango.qubitpi.org/stable/concepts/data-structure/documents/) in the database:

![Example Data in ArangoDB](./docs/example-data.png "Error loading docs/example-data.png")

> [!TIP]
>
> One could find more endpoints at http://localhost:8080/swagger-ui/index.html

### Troubleshooting

#### Healthcheck is Failing

[Inspect healthcheck logs](https://stackoverflow.com/a/42738182) with
`docker inspect --format "{{json .State.Health }}" <container name> | jq`

#### ArangoDB Healthcheck is Failing with "ArangoError: not connected"

This is mostly due to the wrong password during the "connection" via `arangosh` used in healthcheck. Wrong password
could be

- [Container is still using the old password from undeleted volume](https://github.com/arangodb/arangodb/issues/2712#issuecomment-314949756)

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
  curl --location 'localhost:8080/arango/createDocument/mydatabase/mycollection' --header 'Content-Type: application/json' --data '{
      "myfield": "myvalue"
  }' -v
  ```

Development
-----------

### Prerequisite

- JDK 17
- Maven
- Docker

### Running tests

```console
mvn clean verify
```

License
-------

The use and distribution terms for [entity-ws]() are covered by the [Apache License, Version 2.0].

[Apache License Badge]: https://img.shields.io/badge/Apache%202.0-F25910.svg?style=for-the-badge&logo=Apache&logoColor=white
[Apache License, Version 2.0]: https://www.apache.org/licenses/LICENSE-2.0
[API Doc Badge]: https://img.shields.io/badge/Open%20API-Swagger-85EA2D.svg?style=for-the-badge&logo=openapiinitiative&logoColor=white&labelColor=6BA539
[API Doc URL]: https://springdoc.org/

[Docker Pulls Badge]: https://img.shields.io/docker/pulls/jack20191124/entity-ws?style=for-the-badge&logo=docker&color=2596EC
[Docker Hub URL]: https://hub.docker.com/r/jack20191124/entity-ws

[Java Version Badge]: https://img.shields.io/badge/Java-17-brightgreen?style=for-the-badge&logo=OpenJDK&logoColor=white
