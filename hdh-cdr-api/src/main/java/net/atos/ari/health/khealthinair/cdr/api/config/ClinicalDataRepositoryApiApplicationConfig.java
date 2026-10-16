/*
 * Copyright © 2021-2026 Bull SAS
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of work done in the K-HEALTHinAIR EU funded project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.atos.ari.health.khealthinair.cdr.api.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import net.atos.ari.health.khealthinair.model.enums.CodeSystem;

@Configuration
@EnableWebSecurity
public class ClinicalDataRepositoryApiApplicationConfig {

	@Autowired
	FhirConfigProperties fhirConfigProperties;

	@Value("${server.servlet.context-path:/}")
	private String contextPath;

	/**
	 * SecurityWebFilterChain should disable the default behaviour of autoconfig
	 * (Needed for the actuator)
	 * https://github.com/gregwhitaker/springboot-webflux-apikey-example/blob/master/src/main/java/example/security/KeyAuthenticationConverter.java
	 *
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	// @formatter:off
	    http
	        .securityMatchers((matchers) -> matchers.anyRequest())
	        .authorizeHttpRequests((authz) -> authz.anyRequest().permitAll())
	        .formLogin((form) -> form.disable())
	        .httpBasic((basic) -> basic.disable())
	        .logout((logout) -> logout.disable())
	        .csrf((csrf) -> csrf.disable())
	        .cors((cors) -> cors.disable());
        return http.build();
	}

	@Bean
	public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
	// @formatter:off
    return openApi -> openApi
        .info(new Info()
            .title("HDH Clinical Data Repository API")
            .description("<style>@font-face {font-family: 'Quicksand';src:"
        	    + "url('"
        	    + contextPath
        	    + "/static/fonts/Quicksand.woff2') format('woff2')}</style>"
        	    + "<p style='font-family:Quicksand,sans-serif; font-weight:normal;"
        	    + "font-size:0.9em;text-align:center; margin-left:auto;margin-right:auto'>"
        	    + "<img src='"
        	    + contextPath
        	    + "/static/K-HEALTHinAIR.png' alt='K-HEALTHinAIR' "
        	    + "height='50px'><img src='"
        	    + contextPath
        	    + "/static/EUFunded.png' alt='EU' height='50px'>"
        	    + "<br><span style='font-weight: bold; font-stretch: extra-expanded;'>"
        	    + "Knowledge for improving indoor air quality and health</span><br>"
        	    + "<span>Platform for collecting and sharing the information "
        	    + "and knowledge generated about chemical and biological indoor air "
        	    + "pollutants affecting human health.</span></p>"
        	    + "<p style='font-family:Quicksand,sans-serif;font-size:0.9em;'>"
        	    + "This is the OpenAPI documentation of the CDR-API, part of the Health Data Hub, for the K-HEALTHinAIR project."
        	    + "Exposed schemas are based on K-HEALTHinAIR data model.")
            .contact(new Contact()
                .name("AppliedAI (former ARI Health)")
                .url("https://k-healthinair.eu")
                )
            .version(fhirConfigProperties.getVersion())
            .license(new License()
                .name("GPL-3.0-or-later")
                .url("https://www.gnu.org/licenses/gpl-3.0.html"))
            )
        .externalDocs(new ExternalDocumentation()
            .description("K-HEALTHinAIR Project")
            .url(CodeSystem.SYSTEM_KHEALTHINAIR))
        .getComponents()
        .addSecuritySchemes("apikeyForAdmin", new SecurityScheme()
            .name("X-API-Key")
            .type(Type.APIKEY)
            .in(In.HEADER)
            .description("API-key for Admin/Import/Export endpoints"))
        .addSecuritySchemes("apikeyForPilot", new SecurityScheme()
            .name("X-API-Key")
            .type(Type.APIKEY)
            .in(In.HEADER)
            .description("API-key for Import/Export endpoints"));
    // @formatter:on
	}
}
