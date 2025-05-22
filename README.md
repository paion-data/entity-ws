Kugelblitz
==========

![Java Version Badge][Java Version Badge]
[![API Doc Badge]][API Doc URL]
[![Docker Hub][Docker Pulls Badge]][Docker Hub URL]
[![Apache License Badge]][Apache License, Version 2.0]

__Kugelblitz__ is a Spring Boot application template that lets us set up REST JSON API webservice with minimal
effort. Its goal is to swiftly productionize a persistence webservice with all the elements of modern microservices such
as CI/CD support.

Kugelblitz is designed for:

- non-business persistence capabilities that maximizes functions while minimizes the conceptual complexities (everything
  is built on top of exiting best practices; no new concept invented)
- performance-wise caching

It __is not for__:

- security aspect, such as Authentication or Authorization
- any business layer logics

For this reason, Kugelblitz is suitable for a microservice architecture.

✨ Features
-----------

- __Explicit interface__: Kugelblitz specifically serves impersonation-free CRUD operations
- __Tech agnostic API__: In a world of constant and rapid changes, Kugelblitz does not require integration technology
  that dictates what technology stack to use for interactions
- __Simple__: Kugelblitz allows clients full freedom in technology choice by [![API Doc Badge]][API Doc URL]

🚀 Quick Start
--------------

Start a production quality service at [kugelblitz.qubitpi.org](https://kugelblitz.qubitpi.org/docs/intro).

License
-------

The use and distribution terms for [Kugelblitz]() are covered by the [Apache License, Version 2.0].

[Apache License Badge]: https://img.shields.io/badge/Apache%202.0-F25910.svg?style=for-the-badge&logo=Apache&logoColor=white
[Apache License, Version 2.0]: https://www.apache.org/licenses/LICENSE-2.0
[API Doc Badge]: https://img.shields.io/badge/Open%20API-Swagger-85EA2D.svg?style=for-the-badge&logo=openapiinitiative&logoColor=white&labelColor=6BA539
[API Doc URL]: https://springdoc.org/

[Docker Pulls Badge]: https://img.shields.io/docker/pulls/jack20191124/kugelblitz?style=for-the-badge&logo=docker&color=2596EC
[Docker Hub URL]: https://hub.docker.com/r/jack20191124/kugelblitz

[Java Version Badge]: https://img.shields.io/badge/Java-17-brightgreen?style=for-the-badge&logo=OpenJDK&logoColor=white
