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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Component
public class ApiKeyRequestFilter extends GenericFilterBean {

	private static final Logger logger = LoggerFactory.getLogger(ApiKeyRequestFilter.class);

	@Value("${spring.security.api-key-admin}")
	String API_KEY_ADMIN;
	@Value("${spring.security.api-key-pilot}")
	String API_KEY_PILOT;
	@Value("${server.port}")
	String CDR_API_PORT;
	@Value("${server.servlet.context-path}")
	String CDR_API_CONTEXT_PATH;

	private OpenAPI openAPI;

	Map<String, PathMethodDetails> pathsMethodDetails = null;

	@EventListener(ApplicationReadyEvent.class)
	public void fetchOpenApi() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		String openApiJson = restTemplate
				.getForObject("http://localhost:" + CDR_API_PORT + CDR_API_CONTEXT_PATH + "/v3/api-docs", String.class);
		ObjectMapper mapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).build();
		this.openAPI = mapper.readValue(openApiJson, OpenAPI.class);
	}

//	@PostConstruct
//	public void inspectOpenApi() {
//		openAPI.getPaths().forEach((path, pathItem) -> {
//			System.out.println("Path: " + path);
//			pathItem.readOperations().forEach(operation -> {
//				System.out.println("  Method: " + operation.getOperationId());
//			});
//		});
//	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		RequestMethod requestMethod = RequestMethod.valueOf(req.getMethod());
		String path = req.getRequestURI().replaceFirst(req.getContextPath(), "");

		if (path.equals("/") || path.startsWith("/error") || path.startsWith("/static") || path.startsWith("/v3")
				|| path.startsWith("/swagger-ui") || path.startsWith("/actuator/")) {
			chain.doFilter(request, response);
			return;

		} else {

			if (null == this.pathsMethodDetails) {
				this.pathsMethodDetails =
						openAPIToAllPathsDetails(openAPI);
			}

			List<String> pathSecurity = getSecurityByPathAndMethod(path, requestMethod);

			if (null == pathSecurity || pathSecurity.size() == 0) {
				logger.trace("No security API-Key needed when requesting URI [{}]", req.getRequestURI());
				chain.doFilter(request, response);
			} else {
				logger.trace("Security API-Key needed when requesting URI [{}]: {} ", req.getRequestURI(),
						pathSecurity.toString());
				String apikey = req.getHeader("X-API-Key") == null ? "" : req.getHeader("X-API-Key");
				if (!apikey.isBlank()) {
					for (String securityRequirement : pathSecurity) {
						switch (securityRequirement) {
						case "apikeyForAdmin":
							if (!API_KEY_ADMIN.isBlank() && apikey.equals(API_KEY_ADMIN)) {
								chain.doFilter(request, response);
								return;
							}
							break;
						case "apikeyForPilot":
							if (!API_KEY_PILOT.isBlank() && apikey.equals(API_KEY_PILOT)) {
								chain.doFilter(request, response);
								return;
							}
							break;
						default:
							logger.trace("No API-Key found for securityRequirement '" + securityRequirement + "'");
						}
					}
					logger.trace("API-Key didn't match any authorized securityScheme {} when requesting URI [{}].",
							pathSecurity, req.getRequestURI());
				} else {
					logger.trace("API-Key (X-API-Key header) not provided in reques URI [{}]: .", req.getRequestURI(),
							pathSecurity.toString());
				}
				logger.trace("Unauthorized attempt for request URI [{}]", req.getRequestURI());
				HttpServletResponse resp = (HttpServletResponse) response;
				resp.reset();
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				// throw new ResponseStatusException(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
				// "Request URI [" + req.getRequestURI() + "] not expected or unauthorized",
				// null);
			}
		}
	}

	/**
	 * Transforms the OpenAPI object to an structure easier to handle for later
	 * requests security checks
	 *
	 * @param openAPI
	 * @return
	 */
	private Map<String, PathMethodDetails> openAPIToAllPathsDetails(OpenAPI openAPI) {
		io.swagger.v3.oas.models.Paths paths = openAPI.getPaths();
		PathItem pathItem = null;

		if (null != paths && paths.size() > 0) {
			this.pathsMethodDetails = new TreeMap<>(); // in case openAPI was not ready
		}

		for (String key : paths.keySet()) {
			pathItem = paths.get(key);
			int pathSections = key.split("/").length;
			if (null != pathItem.getHead()) {
				pathsMethodDetails.put(key, new PathMethodDetails(RequestMethod.HEAD,
						(null == pathItem.getHead().getSecurity()) ? null : pathItem.getHead().getSecurity(),
						(null == pathItem.getHead().getParameters()) ? 0 : pathItem.getHead().getParameters().size(),
						pathSections));
			} else if (null != pathItem.getGet()) {
				pathsMethodDetails.put(key, new PathMethodDetails(RequestMethod.GET,
						(null == pathItem.getGet().getSecurity()) ? null : pathItem.getGet().getSecurity(),
						(null == pathItem.getGet().getParameters()) ? 0 : pathItem.getGet().getParameters().size(),
						pathSections));
			} else if (null != pathItem.getPost()) {
				pathsMethodDetails.put(key, new PathMethodDetails(RequestMethod.POST,
						(null == pathItem.getPost().getSecurity()) ? null : pathItem.getPost().getSecurity(),
						(null == pathItem.getPost().getParameters()) ? 0 : pathItem.getPost().getParameters().size(),
						pathSections));
			} else if (null != pathItem.getPatch()) {
				pathsMethodDetails.put(key, new PathMethodDetails(RequestMethod.PATCH,
						(null == pathItem.getPatch().getSecurity()) ? null : pathItem.getPatch().getSecurity(),
						(null == pathItem.getPatch().getParameters()) ? 0 : pathItem.getPatch().getParameters().size(),
						pathSections));
			} else if (null != pathItem.getDelete()) {
				pathsMethodDetails.put(key,
						new PathMethodDetails(RequestMethod.DELETE,
								(null == pathItem.getDelete().getSecurity()) ? null
										: pathItem.getDelete().getSecurity(),
								(null == pathItem.getDelete().getParameters()) ? 0
										: pathItem.getDelete().getParameters().size(),
								pathSections));
			} else {
				// throw new Exception("No expected method (head, get post, patch, delete) in
				// path [" + key
				// + "]");
				logger.trace("No expected method (head, get post, patch, delete) in path " + key);
			}
		}

		for (Entry<String, PathMethodDetails> pathMethodDetails : this.pathsMethodDetails.entrySet()) {
			logger.trace(pathMethodDetails.getKey() + ": " + pathMethodDetails.getValue().getRequestMethod()
					+ " - Sections:" + pathMethodDetails.getValue().getPathSections() + ", parameters: "
					+ pathMethodDetails.getValue().getParameters());
		}
		return this.pathsMethodDetails;
	}

	/**
	 * PathsMethodDetails maintains the sorted TreeMap<String, PathMethodDetails>
	 * structure, alongside the methods to check the security of the path to check
	 */

	public Map<String, PathMethodDetails> getPathsMethodDetailsByMethod(RequestMethod requestMethod) {
		Map<String, PathMethodDetails> filtered = new HashMap<>();
		filtered.entrySet().addAll(pathsMethodDetails.entrySet().stream()
				.filter(p -> p.getValue().getRequestMethod().equals(requestMethod)).collect(Collectors.toSet()));
		return filtered;
	}

	/**
	 * From the potential matchings, the chosen is the one checking from the left
	 * the parts matching fulfilling from the left till number of parameters
	 * matching
	 *
	 * @param path
	 * @param requestMethod
	 * @return
	 */
	List<String> getSecurityByPathAndMethod(String path, RequestMethod requestMethod) {
		Map<String, PathMethodDetails> filteredPathsDetails = new TreeMap<>();

		filteredPathsDetails = pathsMethodDetails.entrySet().stream()
				.filter(p -> new AntPathMatcher().match(p.getKey(), path))
				.sorted(Map.Entry.<String, PathMethodDetails>comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		// filteredPathsDetails.forEach((key, val) -> {
		// logger.trace("Potential filtered for matching : " + key + " = " +
		// val.toString());
		// });

		if (filteredPathsDetails.size() > 0) {
			if (filteredPathsDetails.containsKey(path)) { // Direct match when there is no parameters
				logger.trace("Security filter API-Key match for {} : {}", path,
						filteredPathsDetails.get(path).getSecurity());
				return filteredPathsDetails.get(path).getSecurity();
			} else {
				String basePath = path;
				for (Map.Entry<String, PathMethodDetails> entry : filteredPathsDetails.entrySet()) {
					String basePathEntry = entry.getKey();
					for (int i = path.split("/").length - 1; i >= entry.getValue().parameters; i--) {
						basePath = basePath.substring(0, basePath.lastIndexOf('/'));
						basePathEntry = String.join("/", Arrays.asList(entry.getKey().split("/")).subList(0, i));
						if (basePath.equals(basePathEntry)) {
							logger.trace("Security filter API-Key match for {} : {}", path,
									filteredPathsDetails.get(entry.getKey()).getSecurity());
							return filteredPathsDetails.get(entry.getKey()).getSecurity();
						}
					}
				}
			}
		}
		return null;
	}

	Boolean isSecurityByPathAndMethod(String path, RequestMethod method, String securityRequirement) {
		return getSecurityByPathAndMethod(path, method).contains(securityRequirement);
	}
}

/**
 * Private class to maintain a faster structure to perform security check for
 * the path
 */
@Data
class PathMethodDetails implements Comparable<PathMethodDetails> {
	RequestMethod requestMethod = null;
	List<String> security = null;
	int parameters = 0;
	int pathSections = 0;

	public PathMethodDetails(RequestMethod requestMethod, List<SecurityRequirement> securityRequirements,
			int parameters, int pathSections) {
		super();
		this.requestMethod = requestMethod;
		this.security = new ArrayList<>();
		if (null != securityRequirements) {
			for (SecurityRequirement securityRequirement : securityRequirements) {
				this.security.addAll(securityRequirement.keySet());
			}
		}
		this.parameters = parameters;
		this.pathSections = pathSections;
	}

	@Override
	public int compareTo(PathMethodDetails p) {
		if (this.getPathSections() == p.getPathSections()) {
			// Same pathSections, first the one with less parameters
			return (this.getParameters() < p.getParameters() ? 1 : 0);
		} else {
			// Different pathSections, first the one with more fixed part
			// /v1/view/exacerbation-states/xxA/xxB/xxC/{a}/{b} --> 9-2=7
			// /v1/view/exacerbation-states/xxA/xxB/{a} --> 7-1=6
			// /v1/view/exacerbation-states/xxA/{a}/{b}/{c} --> 8-3=5
			// /v1/view/exacerbation-states/{a}/{b}/{c} --> 7-3=4
			return (this.getPathSections() - this.getParameters() < p.getPathSections() - p.getParameters() ? 1 : 0);
		}
	}
}
