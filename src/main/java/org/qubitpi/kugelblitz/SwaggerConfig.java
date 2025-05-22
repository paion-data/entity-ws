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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Swagger UI metadata configuration.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures the Swagger UI metadata.
     *
     * @return the complete configuration to be injected into the Spring context
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Entity Webservice")
                                .version("0.0.1")
                                .description("Entity Webservice REST API Interface")
                                .contact(
                                        new Contact()
                                                .name("Jiaqi Liu")
                                                .url("https://qubitpi.org")
                                                .email("jack20220723@gmail.com")
                                )
                                .license(
                                        new License()
                                                .name("Apache License, Version 2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                                )
                );
    }
}
