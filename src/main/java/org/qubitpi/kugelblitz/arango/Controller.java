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

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

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
     * @param database  The name of the database
     * @param collection  Name of the collection in which the document is to be created.
     * @param document  An object that specifies all the fields of a document, the collection the document belongs
     * to, and the database that stores that collection
     *
     * @return the newly created document with a 200 status code
     */
    @Operation(
            tags = "Documents",
            summary = "Create a document",
            description = "Creates a new document from the document given in the body, unless there is already a " +
                    "document with the `_key` given. If no `_key` is given, a new unique `_key` is generated " +
                    "automatically. The `_id` is automatically set in both cases, derived from the collection name " +
                    "and `_key`. \n\n " +
                    "" +
                    "> **INFO:** An `_id` or `_rev` attribute specified in the body is ignored. \n\n\n" +
                    "" +
                    "If the document was created successfully, then the `Location` header contains the path to the " +
                    "newly created document. The `ETag` header field contains the revision of the document. Both " +
                    "are only set in the single document case. \n\n " +
                    "" +
                    "Unless `silent` is set to `true`, the body of the response contains a JSON object with the " +
                    "following attributes: \n " +
                    "" +
                    "- `_id`, containing the document identifier with the format `<collection-name>/<document-key>`. " +
                    "\n " +
                    "- `_key`, containing the document key that uniquely identifies a document within the " +
                    "collection. \n" +
                    "- `_rev`, containing the document revision. \n\n" +
                    "" +
                    "If the collection parameter `waitForSync` is `false`, then the call returns as soon as the " +
                    "document has been accepted. It does not wait until the documents have been synced to disk. \n\n" +
                    "" +
                    "Optionally, the query parameter `waitForSync` can be used to force synchronization of the " +
                    "document creation operation to disk even in case that the `waitForSync` flag had been disabled " +
                    "for the entire collection. Thus, the `waitForSync` query parameter can be used to force " +
                    "synchronization of just this specific operations. To use this, set the `waitForSync` parameter " +
                    "to `true`. If the `waitForSync` parameter is not specified or set to `false`, then the " +
                    "collection's default `waitForSync` behavior is applied. The `waitForSync` query parameter " +
                    "cannot be used to disable synchronization for collections that have a default `waitForSync` " +
                    "value of `true`. \n\n" +
                    "" +
                    "If the query parameter `returnNew` is `true`, then, for each generated document, the complete " +
                    "new document is returned under the `new` attribute in the result.\n"
    )
    @PostMapping(
            value = "/createDocument/{database}/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    JsonNode createDocument(
            @Parameter(
                    description = "ArangoDB database name. \n\n" +
                            "> **Note**: The database, if not exists, will be created automatically",
                    example = "mydatabase",
                    required = true
            )
            @PathVariable(value = "database", required = true) final String database,
            @Parameter(
                    description = "Name of the collection in which the document is to be created. \n\n" +
                            "> **Note**: The collection, if not exists, will be created automatically",
                    example = "mycollection",
                    required = true
            )
            @PathVariable(value = "collection", required = true) final String collection,
            @RequestBody(required = true) final Map<String, Object> document
    ) {
        if (!databaseService.databaseExists(database)) {
            databaseService.createDatabase(database);
        }

        if (!collectionService.collectionExists(database, collection)) {
            collectionService.createCollection(database, collection);
        }

        return documentService.createDocument(database, collection, document);
    }
}
