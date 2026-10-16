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

import java.util.List;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.HTTPVerb;
import org.hl7.fhir.r5.model.Enumerations.ObservationStatus;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;
import net.atos.ari.health.khealthinair.cdr.api.repository.RepositoryConnector;
import net.atos.ari.health.khealthinair.model.enums.CodeSystem;

@Component
public class ObservationService {

	private static final Logger logger = LoggerFactory.getLogger(ObservationService.class);

	@Autowired
	private RepositoryConnector repositoryConnector;

	public ObservationService(@Autowired FhirConfigProperties fhirConfigProperties) {
		logger.debug("New instance of ObservationService with SYSTEM_KHEALTHINAIR:[{}].",
				CodeSystem.SYSTEM_KHEALTHINAIR);
	}

	/**
	 *
	 * @param observationReferences
	 * @return
	 */
	public String updateObservation(Observation observation) {

		// Narrative
		observation.getText().setStatus(org.hl7.fhir.r5.model.Narrative.NarrativeStatus.GENERATED);
		observation.getText().setDivAsString(
				"<div>Observation created for patient [Patient/" + observation.getSubject().getId() + "]</div>");
		observation.setStatus(ObservationStatus.FINAL);
		observation.addPerformer(new Reference(observation.getSubject().getReference()));

		// @formatter:off
	    MethodOutcome outcome = repositoryConnector
	      .client()
	      .create()
	      .resource(observation)
	      .cacheControl(CacheControlDirective.noCache())
	      .execute();
	    // @formatter:on

		if (outcome.getCreated() != null && outcome.getCreated()) {
			return outcome.getId().getIdPart();
		} else if (outcome.getResponseStatusCode() == 200) {
			return outcome.getId().getIdPart();
		} else {
			logger.error("Patient [{}] creation/update failed. Error: {}", observation.getIdElement().getIdPart());
			return null;
		}

	}

	/**
	 * Generates a Bundle from an Encounter and their associated Procedures and
	 * Observations
	 *
	 * @param observations
	 * @return
	 */
	public Bundle createObservationsBundle(List<Observation> observations) {

		Bundle bundle = new Bundle();
		bundle.setType(Bundle.BundleType.TRANSACTION);

		observations.stream()
				.forEach(observation -> bundle.addEntry().setFullUrlElement(IdType.newRandomUuid())
						.setResource(observation).getRequest().setUrl("/Observation/" + observation.getId())
						.setMethod(HTTPVerb.PUT));

		return bundle;
	}

}
