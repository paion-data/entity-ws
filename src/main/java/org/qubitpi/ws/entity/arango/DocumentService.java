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

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A standard Spring service layer responsible for the document-related interaction between entity-ws and ArangoDB.
 */
@Service
class DocumentService extends AbstractRestService {

    /**
     * Creates a document.
     * <p>
     * The method assumes the database and collection already exist, otherwise an error occurs.
     *
     * @param database  The database name that stores the document
     * @param collection  The collection name that contains the document
     * @param entity  An object that contains all fields of the document to be created
     *
     * @return the newly created document
     */
    JsonNode createDocument(final String database, final String collection, final Map<String, Object> entity) {
        final ObjectNode document = JSON_MAPPER.createObjectNode();
        entity.forEach((key, value) -> document.put(key, value.toString()));

        return post(
                String.format(host + "/_db/%s/_api/document/%s", database, collection),
                document,
                Map.of("Content-Type", "application/json", "Authorization", "bearer " + getJwtToken())
        );
    }
}
