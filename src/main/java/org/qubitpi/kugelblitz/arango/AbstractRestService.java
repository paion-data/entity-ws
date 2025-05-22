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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link AbstractRestService} abstracts away the ArangoDB REST API request implementation.
 * <p>
 * See https://arango.qubitpi.org/stable/develop/http-api/ for more information.
 */
abstract class AbstractRestService {

    protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    protected static final String FAILED_REQUEST_MESSAGE = "There is a backend error. Please contact backend team";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestService.class);

    @Value("${entity-ws.arango.hosts}")
    protected String host;

    @Value("${entity-ws.arango.username}")
    protected String username;

    @Value("${entity-ws.arango.password}")
    protected String password;

    /**
     * Sends an GET request to ArangoDB REST API endpoint.
     * <p>
     * If the API endpoint reports an error, this method will immediately abort the client request with an error
     * message.
     *
     * @param uri  The URL of the GET endpoint
     *
     * @return the data of a successful API request
     */
    protected JsonNode get(final String uri) {
        HttpResponse<String> response = null;

        try {
            response = HTTP_CLIENT.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(uri))
                            .GET()
                            .header("Authorization", "bearer " + getJwtToken())
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (final IOException exception) {
            LOG.error(String.format("I/O error on ArangoDB GET API request: %s", exception.getMessage()), exception);
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE, exception);
        } catch (final InterruptedException exception) {
            LOG.error(
                    String.format("Unexpected abort on ArangoDB GET API request: %s", exception.getMessage()),
                    exception
            );
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE, exception);
        }

        if (response.statusCode() != 200) {
            LOG.error(String.format("ArangoDB GET API error: %s", response.body()));
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE);
        }

        try {
            return JSON_MAPPER.readTree(response.body());
        } catch (final JsonProcessingException exception) {
            LOG.error(
                    String.format("Jackson deserialization error on GET reponse: %s", exception.getMessage()),
                    exception
            );
            throw new RuntimeException(FAILED_REQUEST_MESSAGE, exception);
        }
    }

    /**
     * Send a POST with the specified API URL, payload, and headers.
     * <p>
     * Note that the POST request does not necessarily have to be sent against an ArangoDB.
     *
     * @param uri  The provided URL to send the request to
     * @param payload  The provided payload in {@link ObjectNode} representation. The JSON payload field name
     * corresponds to the {@link ObjectNode} field name and JSON field value to {@link ObjectNode} field value
     * @param headers  A map of headers with key representing the header key and value being the header value
     *
     * @return The POST response body represented by a {@link JsonNode}
     */
    protected static JsonNode post(final String uri, final ObjectNode payload, final Map<String, String> headers) {
        HttpResponse<String> response = null;

        try {
            response = HTTP_CLIENT.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(uri))
                            .POST(HttpRequest.BodyPublishers.ofString(JSON_MAPPER.writeValueAsString(payload)))
                            .headers(
                                    headers
                                            .entrySet()
                                            .stream()
                                            .map(entry -> Arrays.asList(entry.getKey(), entry.getValue()))
                                            .flatMap(List::stream)
                                            .toList()
                                            .toArray(String[]::new)
                            )
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (final IOException exception) {
            final String errorMessage = String.format("I/O error: %s", exception.getMessage());
            LOG.error(errorMessage, exception);
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE, exception);
        } catch (final InterruptedException exception) {
            final String errorMessage = String.format(
                    "Unexpected abort on ArangoDB POST API request: %s",
                    exception.getMessage()
            );
            LOG.error(errorMessage, exception);
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE, exception);
        }

        if (response.statusCode() >= 400) {
            LOG.error(String.format("ArangoDB POST API error: %s", response.body()));
            throw new IllegalStateException(FAILED_REQUEST_MESSAGE);
        }

        try {
            return JSON_MAPPER.readTree(response.body());
        } catch (final JsonProcessingException exception) {
            LOG.error(
                    String.format("Jackson deserialization error on POST response: %s", exception.getMessage()),
                    exception
            );
            throw new RuntimeException(FAILED_REQUEST_MESSAGE, exception);
        }
    }

    /**
     * Sends an authentication request to Arango auth endpoint and returns a JWT token.
     * <p>
     * See https://arango.qubitpi.org/stable/develop/http-api/authentication/ for more info
     *
     * @return a string representing the JWT token
     */
    protected String getJwtToken() {
        final ObjectNode credential = JSON_MAPPER.createObjectNode();
        credential.put("username", username);
        credential.put("password", password);

        return post(host + "/_open/auth", credential, Map.of("Content-Type", "application/json"))
                .get("jwt").asText();
    }
}
