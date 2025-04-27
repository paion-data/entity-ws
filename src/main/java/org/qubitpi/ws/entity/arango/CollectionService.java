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
 * A standard Spring service layer responsible for the collection-related interaction between entity-ws and ArangoDB.
 */
@Service
class CollectionService extends AbstractRestService {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionService.class);

    /**
     * Returns whether or not a database contains a collection with the specified name.
     *
     * @param database  The name of the containing database
     * @param collection  The name of the collection to check for
     *
     * @return {@code true} if the collection exists or {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    protected boolean collectionExists(final String database, final String collection) {
        final JsonNode allCollections = get(
                String.format(host + "/_db/%s/_api/collection?excludeSystem=true", database)
        ).get("result");

        try {
            return ((List<JsonNode>) JSON_MAPPER
                    .readerForListOf(JsonNode.class)
                    .readValue(allCollections))
                    .stream()
                    .anyMatch(jsonNode -> collection.equals(jsonNode.get("name").asText()));
        } catch (final IOException exception) {
            LOG.error(String.format("Error deserializing %s", allCollections), exception);
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE, exception);
        }
    }

    /**
     * Creates a new collection.
     *
     * @param database  The name of the containing database
     * @param collection  The name of the collection to be created
     */
    protected void createCollection(final String database, final String collection) {
        final ObjectNode newCollection = JSON_MAPPER.createObjectNode();
        newCollection.put("name", collection);
        post(
                String.format(host + "/_db/%s/_api/collection", database),
                newCollection,
                Map.of("Content-Type", "application/json", "Authorization", "bearer " + getJwtToken())
        );
    }
}
