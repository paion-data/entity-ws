/*
 * Copyright 2025 Jiaqi Liu. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qubitpi.kugelblitz;

import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

import java.io.File;
import java.time.Duration;

/**
 * Integration tests for Docker Compose.
 */
@Testcontainers
public class DockerComposeIT {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Container
    private static final DockerComposeContainer COMPOOSE =
            new DockerComposeContainer(new File("docker-compose.yaml"))
                    .withExposedService("kugelblitz", 8080)
                    .withEnv("KUGELBLITZ_ARANGO_HOSTS", "http://arango-db:8529")
                    .withEnv("KUGELBLITZ_ARANGO_USERNAME", "root")
                    .withEnv("KUGELBLITZ_ARANGO_PASSWORD", "root")
                    .withEnv("ARANGO_ROOT_PASSWORD", "root")
                    .withStartupTimeout(Duration.ofSeconds(600));

    /**
     * Make sure new document can be created.
     *
     * @throws Exception if any error occurs in tests
     */
    @Test
    public void testCreateDocument() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .build()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        final ObjectNode payload = JSON_MAPPER.createObjectNode();
        payload.put("myfield", "myvalue");

        RestAssured.given()
                .body(payload)
                .when()
                .post("/arango/createDocument/mydatabase/mycollection")
                .then()
                .statusCode(200)
                .body("_id", notNullValue())
                .body("_key", notNullValue())
                .body("_rev", notNullValue());
    }
}
