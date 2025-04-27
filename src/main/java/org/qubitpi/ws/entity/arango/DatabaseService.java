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
package org.qubitpi.ws.entity.arango;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A standard Spring service layer responsible for the database-related interaction between entity-ws and ArangoDB.
 */
@Service
class DatabaseService extends AbstractRestService {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseService.class);

    /**
     * Returns whether or not a database with a specified name already exists.
     *
     * @param database  The name of the database to check for
     *
     * @return {@code true} if the databases exists or {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    protected boolean databaseExists(final String database) {
        final JsonNode allDatabases = get(host + "/_db/_system/_api/database").get("result");

        try {
            return ((List<String>) JSON_MAPPER
                    .readerForListOf(String.class)
                    .readValue(allDatabases)).contains(database);
        } catch (final IOException exception) {
            LOG.error(String.format("Error deserializing %s", allDatabases), exception);
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE, exception);
        }
    }

    /**
     * Creates a new database.
     *
     * @param database  The name of the database to be created.
     */
    protected void createDatabase(final String database) {
        final ObjectNode newDatabase = JSON_MAPPER.createObjectNode();
        newDatabase.put("name", database);
        post(
                host + "/_db/_system/_api/database",
                newDatabase,
                Map.of("Content-Type", "application/json", "Authorization", "bearer " + getJwtToken())
        );
    }
}
