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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r5.model.Address;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Patient;
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

@Component
public class PatientService {

	private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

	@Autowired
	private RepositoryConnector repositoryConnector;

	@Autowired
	ObjectMapper objectMapper;

	public PatientService(@Autowired FhirConfigProperties fhirConfigProperties,
			@Autowired ClinicalDataRepositoryApiApplicationConfig clinicalDataRepositoryApiApplicationConfig) {

		logger.debug("New instance of PatientService with SYSTEM_KHEALTHINAIR:[{}].", CodeSystem.SYSTEM_KHEALTHINAIR);
	}

	/**
	 * Create K-HEALTHinAIR Patient
	 *
	 * @param khealthinairPatient
	 * @return
	 */
	public String updatePatient(net.atos.ari.health.khealthinair.model.Patient khealthinairPatient) {
		logger.info("Creating/Updating Patient with patientId [{}] in the CDR...", khealthinairPatient.getPatientId());

		org.hl7.fhir.r5.model.Patient patient = new org.hl7.fhir.r5.model.Patient();
		if (null != khealthinairPatient.getPatientId() && !khealthinairPatient.getPatientId().isEmpty()) {
			patient.setId(new IdType("Patient", khealthinairPatient.getPatientId()));
			patient.addIdentifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
					.setValue(khealthinairPatient.getPatientId());
		}

		// Narrative
		patient.getText().setStatus(org.hl7.fhir.r5.model.Narrative.NarrativeStatus.GENERATED);
		patient.getText().setDivAsString(
				"<div>Patient created from HIS patient id [" + khealthinairPatient.getPatientId() + "].</div>");

		// Country
		List<Address> addresses = new ArrayList<>();
		if (khealthinairPatient.getCountryOfBirth() != null && khealthinairPatient.getCountryOfBirth().equals("0")) {
			addresses.add(new Address().setCountry("Spain"));
		} else {
			addresses.add(new Address().setCountry(khealthinairPatient.getCountryOfBirth2()));
		}
		patient.setAddress(addresses);

		// Gender
		if (khealthinairPatient.getSex() != null && !khealthinairPatient.getSex().isBlank()) {
			switch (khealthinairPatient.getSex()) {
			case "1":
				patient.setGender(AdministrativeGender.MALE);
				break;
			case "2":
				patient.setGender(AdministrativeGender.FEMALE);
				break;
			case "3":
				patient.setGender(AdministrativeGender.OTHER);
				break;
			case "999":
				patient.setGender(AdministrativeGender.UNKNOWN);
				break;
			default:
				logger.error("Not recogniced value for sex [{}].", khealthinairPatient.getSex());
			}
		}

		if (khealthinairPatient.getRelationshipstatus() != null
				&& !khealthinairPatient.getRelationshipstatus().isBlank()) {
			switch (khealthinairPatient.getRelationshipstatus()) {
			case "0":
				patient.setMaritalStatus(
						new CodeableConcept()
								.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
										.setCode(khealthinairPatient.getRelationshipstatus())
										.setDisplay("Not married/partnered"))
								.addCoding(
										new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
												.setCode("U").setDisplay("Unmarried")));
				break;
			case "1":
				patient.setMaritalStatus(new CodeableConcept()
						.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
								.setCode(khealthinairPatient.getRelationshipstatus()).setDisplay("Married/ Partnered"))
						.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
								.setCode("M").setDisplay("Married")));
				break;
			case "2":
				patient.setMaritalStatus(new CodeableConcept()
						.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
								.setCode(khealthinairPatient.getRelationshipstatus()).setDisplay("Divorced/Separated"))
						.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
								.setCode("D").setDisplay("Divorced")));
				break;
			case "3":
				patient.setMaritalStatus(
						new CodeableConcept()
								.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
										.setCode(khealthinairPatient.getRelationshipstatus()).setDisplay("Widowed"))
								.addCoding(
										new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
												.setCode("W").setDisplay("Widowed")));
				break;
			case "4":
				patient.setMaritalStatus(
						new CodeableConcept()
								.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
										.setCode(khealthinairPatient.getRelationshipstatus()).setDisplay("Single"))
								.addCoding(
										new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
												.setCode("S").setDisplay("Never married")));
				break;
			case "5":
				patient.setMaritalStatus(
						new CodeableConcept()
								.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
										.setCode(khealthinairPatient.getRelationshipstatus()).setDisplay("Unknown"))
								.addCoding(
										new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
												.setCode("UNK").setDisplay("unknown")));
				break;
			default:
				logger.error("Not recogniced value for Relationshipstatus [{}].",
						khealthinairPatient.getRelationshipstatus());
			}
		}
		// BirthDate
		if (khealthinairPatient.getYearOfBirth() != null) {
			patient.getBirthDateElement().setTimeZone(TimeZone.getTimeZone("UTC")).setValue(
					Date.from(khealthinairPatient.getYearOfBirth().atStartOfDay(ZoneId.of("UTC")).toInstant()));
		}
		// @formatter:off
	    MethodOutcome outcome = repositoryConnector
	      .client()
	      .update() // So is created with the provided ID
	      .resource(patient) //FIXME Confirm update is not deleting gender+burthDate, etc
	      //.withId(khealthinairPatient.getPatientId())
	      .cacheControl(CacheControlDirective.noCache())
	      .execute();
	    // @formatter:on

		if (outcome.getCreated() != null && outcome.getCreated()) {
			return outcome.getId().getIdPart();
		} else if (outcome.getResponseStatusCode() == 200) {
			return outcome.getId().getIdPart();
		} else {
			logger.error("Patient [{}] creation/update failed. Error: {}", patient.getIdElement().getIdPart());
			return null;
		}

	}

	/**
	 * Request the ids of all the K-HEALTHinAIR Patients
	 *
	 * @return
	 */
	public List<String> getPatientsPatientIds() {
		logger.debug("Retrieve all patients ids from the CDR");

		try {
	// @formatter:off
      Bundle results = repositoryConnector.client()
          .search()
          .forResource(Patient.class)
          .summaryMode(null)
          .returnBundle(Bundle.class)
          .elementsSubset("Patient.identifier", "Patient.name")
          .cacheControl(CacheControlDirective.noCache())
          .execute();
      // @formatter:on

			List<String> patientsIds = new ArrayList<>();

			if (results.getEntry().size() > 0) {
				do {
					for (BundleEntryComponent entry : results.getEntry()) {
						if (entry.getResource() instanceof Patient) {
							patientsIds.add(entry.getResource().getIdElement().getIdPart());
						}
					}
					if (results.getLink(IBaseBundle.LINK_NEXT) != null) {
						results = repositoryConnector.client().loadPage().next(results).execute();
					} else {
						return patientsIds;
					}
				} while (true);
			} else {
				logger.debug("No Patients");
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	net.atos.ari.health.khealthinair.model.Patient getKhealthinairPatientByPatientId(String patientId) {
		return fhirPatientToKhealthinairPatient(getPatientByPatientId(patientId));
	}

	/**
	 * Retrieve specific K-HEALTHinAIR Patient by identifier
	 *
	 * @param patientId
	 * @return
	 */
	Patient getPatientByPatientId(String patientId) {
		logger.debug("Retrieve patient [{}] from the CDR", patientId);

		try {
		// @formatter:off
	    Bundle results = repositoryConnector.client()
	        .search()
	        .forResource(Patient.class)
	        .where(IAnyResource.RES_ID.exactly().code(patientId))
	        //.elementsSubset("Patient.identifier", "Patient.gender", "Patient.birthDate", "Patient.maritalStatus", "Patient.contact.address")
	        .sort().ascending(IAnyResource.RES_ID)  // Only one expected anyway
	        .returnBundle(Bundle.class)
	        .cacheControl(CacheControlDirective.noCache())
	        .execute();
      // @formatter:on

			// Inform of the unexpected circumstance of finding more than one
			if (results.getEntry().size() > 1) {
				String fhirIds = "";
				for (BundleEntryComponent entry : results.getEntry()) {
					fhirIds += entry.getResource().getIdElement().getIdPart() + ", ";
					fhirIds = fhirIds.substring(0, fhirIds.length() - 2);
				}
				logger.warn(
						"Unexpected more than one Patient with identifier [{}], FHIR ids [{}]. Returning the first by Resource Id.",
						patientId, fhirIds);
				return (Patient) results.getEntry().get(0).getResource();
			}

			if (results.getEntry().size() == 1) {
				return (Patient) results.getEntry().get(0).getResource();
			} else {
				logger.debug("No Patient with identifier [{}]", patientId);
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Check K-HEALTHinAIR Patient existence by the internal id
	 *
	 * @param patientId
	 * @return
	 */
	public Boolean existPatientByPatientId(String patientId) {
		// logger.debug("Checking existence of Patient [{}] in the CDR...", patientId);

		if (null == patientId || patientId.isEmpty()) {
			logger.debug("Patient without mandatory patientId field.");
			return false;
		}

		try {
		// @formatter:off
	    Bundle results = repositoryConnector.client()
	        .search()
	        .forResource(Patient.class)
	        .where(IAnyResource.RES_ID.exactly().code(patientId))
	        .returnBundle(Bundle.class)
	        .summaryMode(SummaryEnum.COUNT) // Search only: just return a count of the matching resources, without returning the actual matches
	        .cacheControl(CacheControlDirective.noCache())
	        .execute();
        // @formatter:on

			if (results.getTotal() > 0) {
				return true;
			} else {
				logger.debug("No Patient with id [{}].", patientId);
				return false;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Parse a FHIR Patient resource to a K-HEALTHinAIR Patient instance
	 *
	 * @param fhirPatient
	 * @return
	 * @throws Exception
	 */
	private net.atos.ari.health.khealthinair.model.Patient fhirPatientToKhealthinairPatient(Patient fhirPatient) {
		net.atos.ari.health.khealthinair.model.Patient khealthinairPatient = new net.atos.ari.health.khealthinair.model.Patient();

		khealthinairPatient.setPatientId(fhirPatient.getIdPart());

		if (fhirPatient.hasAddress() && !fhirPatient.getAddress().isEmpty()) {
			Address address = fhirPatient.getAddressFirstRep();
			if ("Spain".equalsIgnoreCase(address.getCountry())) {
				khealthinairPatient.setCountryOfBirth("0"); // Assuming Spain = 0
			} else {
				khealthinairPatient.setCountryOfBirth2(address.getCountry());
			}
		}

		if (fhirPatient.hasGender()) {
			switch (fhirPatient.getGender()) {
			case MALE:
				khealthinairPatient.setSex("1");
				break;
			case FEMALE:
				khealthinairPatient.setSex("2");
				break;
			case OTHER:
				khealthinairPatient.setSex("3");
				break;
			case UNKNOWN:
				khealthinairPatient.setSex("999");
				break;
			default:
				// No action needed
			}
		}

		if (fhirPatient.hasMaritalStatus() && fhirPatient.getMaritalStatus().hasCoding()) {
			for (Coding coding : fhirPatient.getMaritalStatus().getCoding()) {
				if (CodeSystem.SYSTEM_KHEALTHINAIR.equals(coding.getSystem())) {
					khealthinairPatient.setRelationshipstatus(coding.getCode());
					break;
				}
			}
		}

		if (fhirPatient.hasBirthDate()) {
			LocalDate birthDate = fhirPatient.getBirthDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			khealthinairPatient.setYearOfBirth(birthDate);
		}

		return khealthinairPatient;
	}

	/**
	 *
	 * @param patientId
	 * @return
	 */
	public OffsetDateTime getPatientLastModification(String patientId) {
		logger.debug("Getting lastUpdated from resource of patient [Patient/{}] in the CDR...", patientId);

		try {
		// @formatter:off
	      Bundle results = repositoryConnector.client()
	          .search()
	          .forResource(Patient.class)
	          .where(IAnyResource.RES_ID.exactly().code(patientId))
	          .elementsSubset("Patient.meta.lastUpdated")
	          .returnBundle(Bundle.class)
	          .cacheControl(CacheControlDirective.noCache())
	          .execute();
	      // @formatter:on

			if (results.getEntry().size() > 0) {
				return results.getEntry().get(0).getResource().getMeta().getLastUpdated().toInstant()
						.atOffset(ZoneOffset.UTC);
			} else {
				logger.debug("No Patient with id [Patient/{}].", patientId);
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Delete patient and related resources in cascade for the patientId
	 *
	 * @param patientId
	 * @return
	 */
	public MethodOutcome deletePatientInCascadeByPatientId(String patientId) {
		try {
		// @formatter:off
	     MethodOutcome methodOutcome =  repositoryConnector.client()
	       .delete()
	       .resourceById("Patient", patientId)
	       .cascade(DeleteCascadeModeEnum.DELETE)
	       .cacheControl(CacheControlDirective.noCache())
	       .execute();
	     // @formatter:on

			logger.debug("Patient [{}] successfully deleted. {}", patientId,
					((OperationOutcome) methodOutcome.getOperationOutcome()).getIssue().get(1).getDiagnostics());

			return methodOutcome;
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

}
