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

package net.atos.ari.health.khealthinair.cdr.api.service;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r5.model.Binary;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.DeleteCascadeModeEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SummaryEnum;
import net.atos.ari.health.khealthinair.cdr.api.config.ClinicalDataRepositoryApiApplicationConfig;
import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;
import net.atos.ari.health.khealthinair.cdr.api.repository.RepositoryConnector;
import net.atos.ari.health.khealthinair.model.enums.CodeSystem;
import net.atos.ari.health.khealthinair.model.enums.Pilot;

@Component
public class BinaryService {

	private static final Logger logger = LoggerFactory.getLogger(BinaryService.class);

	@Autowired
	private RepositoryConnector repositoryConnector;

	@Autowired
	ObjectMapper objectMapper;

	public BinaryService(@Autowired FhirConfigProperties fhirConfigProperties,
			@Autowired ClinicalDataRepositoryApiApplicationConfig clinicalDataRepositoryApiApplicationConfig) {

		logger.debug("New instance of BinaryService with SYSTEM_KHEALTHINAIR:[{}].", CodeSystem.SYSTEM_KHEALTHINAIR);
	}

	/**
	 * Create K-HEALTHinAIR Binary
	 *
	 * @param binary
	 * @return
	 */
	public String uploadBinary(Reader reader, String name, Pilot pilot, String description) {
		logger.info("Creating Binary file for the pilot [{}] the CDR...", pilot.getName());

		org.hl7.fhir.r5.model.Binary binary = new org.hl7.fhir.r5.model.Binary();

		if (name.length() > 64) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Filename too long", null);
		}
		String fileIdBasedOnName = name.replace(".csv", "").replace("_", "");
		if (existBinaryByBinaryId(fileIdBasedOnName)) {
			logger.info("Binary with name [{}] already exists in the CDR, creating a subsequent version.", name);
		}
		binary.setId(new IdType("Binary", fileIdBasedOnName));

		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[1024];
		int len;
		try {
			while ((len = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] fileBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

		binary.setContentType("application/csv");
		binary.getMeta().addTag(CodeSystem.SYSTEM_KHEALTHINAIR, "Pilot", pilot.getName());
		binary.getMeta().addTag(CodeSystem.SYSTEM_KHEALTHINAIR, "OriginalFileName", name);

		// Add uploaded-date as extension
		Extension dateExtension = new Extension();
		dateExtension.setUrl(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/StructureDefinition/uploaded-date");
		dateExtension.setValue(new DateTimeType(OffsetDateTime.now().toString()));
		binary.getMeta().addExtension(dateExtension);
		binary.setContent(fileBytes);

		// @formatter:off
	    MethodOutcome outcome = repositoryConnector
	      .client()
	      .update()
	      .resource(binary)
	      .cacheControl(CacheControlDirective.noCache())
	      .execute();
	    // @formatter:on

		if (outcome.getCreated() != null && outcome.getCreated()) {
			logger.info("Binary [{}] created.", name);
			return outcome.getId().getIdPart();
		} else if (outcome.getResponseStatusCode() == 200) {
			logger.info("Binary [{}] created.", name);
			return outcome.getId().getIdPart();
		} else {
			logger.error("Binary [{}] creation/update failed. Error: {}", binary.getMeta().getTagFirstRep().getCode());
			return null;
		}
	}

	/**
	 * Request the ids of all the K-HEALTHinAIR Binaries
	 *
	 * @return
	 */
	public List<String> getBinariesIds() {
		logger.debug("Retrieve all binary ids from the CDR");

		try {
	// @formatter:off
      Bundle results = repositoryConnector.client()
          .search()
          .forResource(Binary.class)
          .summaryMode(null)
          .returnBundle(Bundle.class)
          .elementsSubset("Binary.identifier", "Binary.meta")
          .cacheControl(CacheControlDirective.noCache())
          .execute();
      // @formatter:on

			List<String> binaryIds = new ArrayList<>();

			if (results.getEntry().size() > 0) {
				do {
					for (BundleEntryComponent entry : results.getEntry()) {
						if (entry.getResource() instanceof Binary) {
							binaryIds.add(entry.getResource().getIdElement().getIdPart());
						}
					}
					if (results.getLink(IBaseBundle.LINK_NEXT) != null) {
						results = repositoryConnector.client().loadPage().next(results).execute();
					} else {
						return binaryIds;
					}
				} while (true);
			} else {
				logger.debug("No Binaries");
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Retrieve specific Binary by identifier
	 *
	 * @param binaryId
	 * @return
	 */
	Binary getBinaryByBinaryId(String binaryId) {
		logger.debug("Retrieve Binary [{}] from the CDR", binaryId);

		try {
		// @formatter:off
      Bundle results = repositoryConnector.client()
          .search()
          .forResource(Binary.class)
          .where(IAnyResource.RES_ID.exactly().code(binaryId))
          .sort().ascending(IAnyResource.RES_ID)
          .returnBundle(Bundle.class)
          .cacheControl(CacheControlDirective.noCache())
          .execute();
      // @formatter:on

			if (results.getEntry().size() == 1) {
				return (Binary) results.getEntry().get(0).getResource();
			} else {
				logger.debug("No Binary with identifier [{}]", binaryId);
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Check K-HEALTHinAIR Binary existence by the binaryId
	 *
	 * @param patientId
	 * @return
	 */
	public Boolean existBinaryByBinaryId(String binaryId) {
		logger.debug("Checking existence of Binary [{}] in the CDR...", binaryId);
		binaryId.replace(" ", "-");
		if (null == binaryId || binaryId.isEmpty()) {
			logger.debug("Provide a valid binaryId field.");
			return false;
		}

		try {
		// @formatter:off
            Bundle results = repositoryConnector.client()
                .search()
                .forResource(Binary.class)
	            .where(IAnyResource.RES_ID.exactly().code(binaryId))
	            .returnBundle(Bundle.class)
	            .summaryMode(SummaryEnum.COUNT) // Search only: just return a count of the matching resources, without returning the actual matches
	            .cacheControl(CacheControlDirective.noCache())
	            .execute();
        // @formatter:on

			if (results.getTotal() > 0) {
				return true;
			} else {
				logger.debug("No BinaryId with id [{}].", binaryId);
				return false;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 *
	 * @param binaryId
	 * @return
	 */
	public OffsetDateTime getBinaryLastModification(String binaryId) {
		logger.debug("Getting lastUpdated for the Binary [Binary/{}] in the CDR...", binaryId);

		try {
		// @formatter:off
	      Bundle results = repositoryConnector.client()
	          .search()
	          .forResource(Binary.class)
	          .where(IAnyResource.RES_ID.exactly().code(binaryId))
	          .elementsSubset("Binary.meta.lastUpdated")
	          .returnBundle(Bundle.class)
	          .cacheControl(CacheControlDirective.noCache())
	          .execute();
	      // @formatter:on

			if (results.getEntry().size() > 0) {
				return results.getEntry().get(0).getResource().getMeta().getLastUpdated().toInstant()
						.atOffset(ZoneOffset.UTC);
			} else {
				logger.debug("No Patient with id [Binary/{}].", binaryId);
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Delete Binary for the binaryId
	 *
	 * @param binaryId
	 * @return
	 */
	public MethodOutcome deleteBinaryId(String binaryId) {
		try {
			// @formatter:off
		     MethodOutcome methodOutcome =  repositoryConnector.client()
		       .delete()
		       .resourceById("Binary", binaryId)
		       .cascade(DeleteCascadeModeEnum.DELETE)
		       .cacheControl(CacheControlDirective.noCache())
		       .execute();
	        // @formatter:on

			logger.debug("Binary [{}] successfully deleted. {}", binaryId,
					((OperationOutcome) methodOutcome.getOperationOutcome()).getIssue().get(1).getDiagnostics());

			return methodOutcome;
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

}
