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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * The entity endpoint backed by ArangoDB.
 */
@RestController
@RequestMapping("/arango")
class Controller {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private DocumentService documentService;

    /**
     * Creates a document.
     *
     * @param requestBody  An object that specifies all the fields of a document, the collection the document belongs
     * to, and the database that stores that collection
     *
     * @return the newly created document with a 200 status code
     */
    @PostMapping(value = "/createDocument", produces = "application/json")
    JsonNode createDocument(@RequestBody final DocumentRequestBody requestBody) {
        final String database = requestBody.getDatabase();
        final String collection = requestBody.getCollection();
        final Map<String, Object> document = requestBody.getDocument();

        if (!databaseService.databaseExists(database)) {
            databaseService.createDatabase(database);
        }

        if (!collectionService.collectionExists(database, collection)) {
            collectionService.createCollection(database, collection);
        }

        return documentService.createDocument(database, collection, document);
    }
}
