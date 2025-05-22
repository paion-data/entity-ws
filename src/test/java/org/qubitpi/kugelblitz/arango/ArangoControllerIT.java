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
package org.qubitpi.kugelblitz.arango;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * {@link Controller ArandoDB-backed entity controller} integration tests.
 */
@Testcontainers
@WebMvcTest(Controller.class)
@Import({DatabaseService.class, CollectionService.class, DocumentService.class})
public class ArangoControllerIT {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Container
    private static final GenericContainer ARANGO_DB = new GenericContainer(DockerImageName.parse("arangodb:3.11.13"))
            .withEnv("ARANGO_ROOT_PASSWORD", "root")
            .withExposedPorts(8529);

    @Autowired
    private MockMvc mockMvc;

    /**
     * Dynamically set ArangoDB container connection info.
     *
     * @param registry  {@code application.properties} mutator at runtime
     */
    @DynamicPropertySource
    static void registerPgProperties(final DynamicPropertyRegistry registry) {
        registry.add(
                "kugelblitz.arango.hosts",
                () -> String.format("http://%s:%s", ARANGO_DB.getHost(), ARANGO_DB.getMappedPort(8529))
        );
    }

    /**
     * Make sure new document can be created.
     *
     * @throws Exception if any error occurs in tests
     */
    @Test
    public void testCreateDocument() throws Exception {
        final ObjectNode payload = JSON_MAPPER.createObjectNode();
        payload.put("myfield", "myvalue");

        mockMvc.perform(
                post("/arango/createDocument/mydatabase/mycollection")
                        .content(JSON_MAPPER.writeValueAsBytes(payload))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._id").exists())
                .andExpect(jsonPath("$._key").exists())
                .andExpect(jsonPath("$._rev").exists());
    }
}
