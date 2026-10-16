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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r5.model.BooleanType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.IntegerType;
import org.hl7.fhir.r5.model.Questionnaire;
import org.hl7.fhir.r5.model.QuestionnaireResponse;
import org.hl7.fhir.r5.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.r5.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.r5.model.Reference;
import org.hl7.fhir.r5.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.opencsv.CSVWriter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import jakarta.annotation.PostConstruct;
import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;
import net.atos.ari.health.khealthinair.cdr.api.repository.RepositoryConnector;
import net.atos.ari.health.khealthinair.model.RedCAPRow;
import net.atos.ari.health.khealthinair.model.enums.CodeSystem;
import net.atos.ari.health.khealthinair.model.enums.QuestionnaireCode;

@Component
public class QuestionnaireService {

	private static final Logger logger = LoggerFactory.getLogger(QuestionnaireService.class);
	private final char CSV_SEPARATOR = '|';
	@Autowired
	private RepositoryConnector repositoryConnector;

	@Autowired
	private ResourceLoader resourceLoader;

	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
	SimpleDateFormat sdf_datetime_dmy = new SimpleDateFormat("yyyy-MM-dd H:mm");

//	private DateTimeFormatter offsetDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
//			.withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
	private String LANGUAGE = "EN"; // 'EN' by default

	public QuestionnaireService(@Autowired FhirConfigProperties fhirConfigProperties) {
		this.LANGUAGE = fhirConfigProperties.getLanguage();
		sdf_datetime_dmy.setTimeZone(TimeZone.getTimeZone("UTC"));
		logger.debug("New instance of QuestionnaireService with CodeSystem.SYSTEM_KHEALTHINAIR:[{}] and LANGUAGE:[{}].",
				CodeSystem.SYSTEM_KHEALTHINAIR, LANGUAGE);
	}

	@PostConstruct
	public void init() {
		initializeQuestionnaires();
	}

	/*-
	 * Inicialization of FHIR Questionnaire definitions	 *
	 * Every month, participants will answer questions on:
	- socio-demographic data,
	- QoL (EQ-5D-5L for staff and mental health (PHQ-9), together with time spent/activity in the scenario.
	 *
	 */
	public void initializeQuestionnaires() {
		logger.info("Initializing questionnaires...");
		try {
			// *****************************************************
			// PART A.
			// - Socio-demographic data
			if (!existQuestionnaireByName(QuestionnaireCode.SOCIODEMOGRAPHIC.name())) {
				initializationOfQuestionnaire("Sociodemographic.json");
			}

			// *****************************************************
			// PART B.
			// - Time spent in the scenario
			if (!existQuestionnaireByName(QuestionnaireCode.TIMESPENTINTHESCENARIO.name())) {
				initializationOfQuestionnaire("TimeSpentInTheScenario.json");
			}

			// *****************************************************
			// PART C. ICHOM Adult Health Standard Set:
			// - SACQ (Self-Administered Comorbidity Questionnaire)
			if (!existQuestionnaireByName("SACQ")) {
				initializationOfQuestionnaire("SACQ.json");
			}
			// - PROMIS Global-10 Questionnaire
			if (!existQuestionnaireByName(QuestionnaireCode.PROMISGH10.name())) {
				initializationOfQuestionnaire("PROMISGH10.json");
			}
			// - WHODAS-12 (The World Health Org. Disability Assessment Schedule 2.0)
			if (!existQuestionnaireByName(QuestionnaireCode.WHODAS12.name())) {
				initializationOfQuestionnaire("WHODAS-12.json");
			}
			// - WHO-5 (The World Health Organization-Five Well-Being Index)
			if (!existQuestionnaireByName(QuestionnaireCode.WHO5.name())) {
				initializationOfQuestionnaire("WHO-5.json");
			}

			// *****************************************************
			// PART D. PREMS (Patient-Reported Experience Measures, adaptation of the PaRIS
			// survey)
			// - PaRIS (Patient-Reported Indicator Surveys)
			if (!existQuestionnaireByName(QuestionnaireCode.PARIS.name())) {
				initializationOfQuestionnaire("PaRIS.json");
			}
			// - Observational IPCHEM
			if (!existQuestionnaireByName(QuestionnaireCode.OBSERVATIONALIPCHEM.name())) {
				initializationOfQuestionnaire("ObservationalIPCHEM.json");
			}
			// - CAT (COPD Assessment Test)
			if (!existQuestionnaireByName(QuestionnaireCode.CAT.name())) {
				initializationOfQuestionnaire("CAT.json");
			}
			// - mMRC (modified Medical Research Council Dyspnea scale)
			if (!existQuestionnaireByName(QuestionnaireCode.MMRC.name())) {
				initializationOfQuestionnaire("mMRC.json");
			}
			// - TAI-12 (Test of Adherence to Inhalers 12)
			if (!existQuestionnaireByName(QuestionnaireCode.TAI12.name())) {
				initializationOfQuestionnaire("TAI-12.json");
			}
			// - MiniAQLQ (Mini Asthma Quality of Life Questionnaire)
			if (!existQuestionnaireByName(QuestionnaireCode.MINIAQLQ.name())) {
				initializationOfQuestionnaire("MiniAQLQ.json");
			}
			// - ACT (Asthma Control Test)
			if (!existQuestionnaireByName(QuestionnaireCode.ACT.name())) {
				initializationOfQuestionnaire("ACT.json");
			}
			// - SNOT-22 (Sino-Nasal Outcome Test)
			if (!existQuestionnaireByName(QuestionnaireCode.SNOT22.name())) {
				initializationOfQuestionnaire("SNOT-22.json");
			}

			// *****************************************************
			// PART E.Quality Of Life
			// - EQ-5D-5L (5-level EQ-5D Questionnaire)
			if (!existQuestionnaireByName(QuestionnaireCode.EQ5D5L.name())) {
				initializationOfQuestionnaire("EQ-5D-5L.json");
			}
			// - EQ-5D-Y-5L (5-level EQ-5D Youth Questionnaire)
			if (!existQuestionnaireByName(QuestionnaireCode.EQ5DY5L.name())) {
				initializationOfQuestionnaire("EQ-5D-Y-5L.json");
			}

			// *****************************************************
			// PART F. Mental Health
			// - PHQ-9 (Patient Health Questionnaire)
			if (!existQuestionnaireByName(QuestionnaireCode.PHQ9.name())) {
				initializationOfQuestionnaire("PHQ-9.json");
			}

			// *****************************************************
			// PART EXTRAS
			// - GermanyCanteenStaff
			if (!existQuestionnaireByName(QuestionnaireCode.GERMANYCANTEENSTAFF.name())) {
				initializationOfQuestionnaire("GermanyCanteenStaff.json");
			}

			logger.info("Finished questionnaires initialization.");
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	public boolean initializationOfQuestionnaire(String jsonFile) {
		Resource resourceFile = resourceLoader.getResource("classpath:questionnaires/" + jsonFile);
		try (InputStream inputStream = resourceFile.getInputStream()) {
			FhirContext ctx = repositoryConnector.ctx();
			Questionnaire questionnaire = ctx.newJsonParser().parseResource(org.hl7.fhir.r5.model.Questionnaire.class,
					inputStream);

			// Check against a closed list or existing in the DB, based on the Identifier or
			// the /url
//			if (!QuestionnaireCode.contains(questionnaire.getIdentifierFirstRep().getValue())) {
//				throw new IllegalArgumentException("Questionnaire identifier ["
//						+ questionnaire.getIdElement().getIdPart()
//						+ "] not recognized in the QuestionnaireCode enumeration."
//						+ Arrays.stream(QuestionnaireCode.values()).map(Enum::name).collect(Collectors.toList()));
//			}

			saveQuestionnaire(questionnaire);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Check for existence of questionnaire by questionnaireName
	 *
	 * @param questionnaireName
	 * @return
	 */
	public Boolean existQuestionnaireByName(String questionnaireName) {
		try {
		// @formatter:off
      Bundle results = repositoryConnector.client()
          .search()
          .forResource(org.hl7.fhir.r5.model.Questionnaire.class)
          .cacheControl(CacheControlDirective.noCache())
          .where(new StringClientParam("name").matchesExactly().value(questionnaireName))
          .returnBundle(Bundle.class)
          .summaryMode(SummaryEnum.COUNT) // Search only: just return a count of the matching resources, without returning the actual matches
          .execute();
      // @formatter:on

			// Inform of the unexpected circumstance of finding more than one
			if (results.getTotal() > 1) {
				logger.error("Questionnaire [{}] unexpected more than one of it.", questionnaireName);
			}
			if (results.getTotal() > 0) {
				logger.info("Questionnaire [{}] definition already found in the HDH-CDR.", questionnaireName);
				return true;
			} else {
				logger.info("Questionnaire [{}] definition not found in the HDH-CDR.", questionnaireName);
				return false;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Check for existence of questionnaire response by the id saved as identifier
	 *
	 * @param system
	 * @param value
	 * @return
	 */
	public Boolean existQuestionnairesResponses(String patientId, QuestionnaireCode questionnaireCode,
			String instrument, String instance) {
		try {
			// @formatter:off
		  Bundle results = repositoryConnector.client()
		      .search()
		      .forResource(org.hl7.fhir.r5.model.QuestionnaireResponse.class)
		      .cacheControl(CacheControlDirective.noCache())
		      .where(org.hl7.fhir.r5.model.QuestionnaireResponse.QUESTIONNAIRE.hasId(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + questionnaireCode.name().toLowerCase()))
		      .and(org.hl7.fhir.r5.model.QuestionnaireResponse.SUBJECT.hasId("Patient/" + patientId))
		      .and(org.hl7.fhir.r5.model.QuestionnaireResponse.IDENTIFIER.exactly()
			          .systemAndCode(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument", instrument))
		      .and(org.hl7.fhir.r5.model.QuestionnaireResponse.IDENTIFIER.exactly()
			          .systemAndCode(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance", instance))
		      .and(org.hl7.fhir.r5.model.QuestionnaireResponse.SUBJECT.hasId("Patient/" + patientId))
		      .returnBundle(Bundle.class)
		      .summaryMode(SummaryEnum.COUNT) // Search only: just return a count of the matching resources, without returning the actual matches
		      .execute();
           // @formatter:on

			if (results.getTotal() > 0) {
				logger.debug(
						"Questionnaire Response for patientId [{}], redcap-instrument [{}], redcap-instance [{}] and questionnaireCode [{}] already exits, skip.",
						patientId, instrument, instance, questionnaireCode.name());
				return true;
			} else {
				logger.debug(
						"Questionnaire Response for patientId [{}], redcap-instrument [{}], redcap-instance [{}] and questionnaireCode [{}] do not exist, create...",
						patientId, instrument, instance, questionnaireCode.name());
				return false;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Get and returns questionnaire by questionnaireName or null if it does not
	 * exists
	 *
	 * @param questionnaireName
	 * @return
	 */
	public org.hl7.fhir.r5.model.Questionnaire getQuestionnaireByName(String questionnaireName) {
		try {
		// @formatter:off
      Bundle results = repositoryConnector.client()
          .search()
          .forResource(org.hl7.fhir.r5.model.Questionnaire.class)
          .cacheControl(CacheControlDirective.noCache())
          .where(new StringClientParam("name").matchesExactly().value(questionnaireName))
          .returnBundle(Bundle.class)
          .execute();
      // @formatter:on

			if (results.getEntry().size() > 0) {
				return (org.hl7.fhir.r5.model.Questionnaire) results.getEntry().get(0).getResource();
			} else {
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Get and returns questionnaireRespose by identifier or null if it does not
	 * exists
	 *
	 * @param system
	 * @param value
	 * @return
	 */
	public org.hl7.fhir.r5.model.QuestionnaireResponse getQuestionnaireResponseByIdentifier(String system,
			String value) {
		try {
		// @formatter:off
      Bundle results = repositoryConnector.client()
          .search()
          .forResource(org.hl7.fhir.r5.model.QuestionnaireResponse.class)
          .cacheControl(CacheControlDirective.noCache())
          .where(org.hl7.fhir.r5.model.QuestionnaireResponse.IDENTIFIER.exactly()
              .systemAndCode(system, value))
          .elementsSubset("QuestionnaireResponse.item", "QuestionnaireResponse.linkId", "QuestionnaireResponse.authored")
          .returnBundle(Bundle.class)
          .execute();
      // @formatter:on
			if (results.getEntry().size() > 0) {
				return (org.hl7.fhir.r5.model.QuestionnaireResponse) results.getEntry().get(0).getResource();
			} else {
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Return all the Questionnaire responses of a questionnaireName
	 *
	 * @param patientId
	 * @param questionnaireName
	 * @param questionName
	 * @param onlyLast          if true, will only retrieve score/s from the last
	 *                          questionnaire response
	 * @return
	 */
	public List<QuestionnaireResponse> getQuestionnaireResponsesByPatientIdQuestionnaireName(String patientId,
			QuestionnaireCode questionnaireCode) {
		try {
			// First get the complete canonical(Questionnaire) for filter in
			// QuestionnaireResponse.questionnaire
//			String questionnaireUrl = "";
//		// @formatter:off
//        Bundle resultsQuestionnaire = repositoryConnector.client()
//          .search()
//          .forResource(org.hl7.fhir.r5.model.Questionnaire.class)
//          .cacheControl(CacheControlDirective.noCache())
//          .where(org.hl7.fhir.r5.model.Questionnaire.NAME.matchesExactly().value(questionnaireCode.name()))
//          .elementsSubset("Questionnaire.name", "Questionnaire.url")
//          .count(1) // only one Questionnaire is expected, only deal with a first single resource page and skip LINK_NEXT
//          .returnBundle(Bundle.class)
//          .execute();
//        // @formatter:on
//			if (resultsQuestionnaire.getTotal() == 0) {
//				logger.debug("Questionnaire [{}] not found", questionnaireCode.name());
//				return null;
//			} else {
//				questionnaireUrl = ((org.hl7.fhir.r5.model.Questionnaire) resultsQuestionnaire.getEntry().get(0)
//						.getResource()).getUrl();
//			}

		// @formatter:off
        IQuery<Bundle> query = repositoryConnector.client()
          .search()
          .forResource(org.hl7.fhir.r5.model.QuestionnaireResponse.class)
          .cacheControl(CacheControlDirective.noCache())
          .where(org.hl7.fhir.r5.model.QuestionnaireResponse.QUESTIONNAIRE.hasId(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + questionnaireCode.name().toLowerCase()))
          .elementsSubset("QuestionnaireResponse.subject", "QuestionnaireResponse.authored", "QuestionnaireResponse.questionnaire", "QuestionnaireResponse.item", "QuestionnaireResponse.linkId", "QuestionnaireResponse.status")
          .sort().ascending(org.hl7.fhir.r5.model.QuestionnaireResponse.SUBJECT)
          //.sort().descending(org.hl7.fhir.r5.model.QuestionnaireResponse.AUTHORED)
          .returnBundle(Bundle.class);
        // @formatter:on
			if (null != patientId) {
				query = query.where(org.hl7.fhir.r5.model.QuestionnaireResponse.SUBJECT.hasId(patientId));
			}
			Bundle results = query.execute();

			if (results.getEntry().size() > 0) {
				List<QuestionnaireResponse> questionnaireResponses = new ArrayList<>();
				do {
					for (Bundle.BundleEntryComponent bundleEntryComponent : results.getEntry()) {
						questionnaireResponses
								.add((org.hl7.fhir.r5.model.QuestionnaireResponse) bundleEntryComponent.getResource());
					}
					if (results.getLink(IBaseBundle.LINK_NEXT) != null) {
						results = repositoryConnector.client().loadPage().next(results).execute();
					} else {
						return questionnaireResponses;
					}
				} while (true);
			} else {
				logger.debug("No QuestionnaireResponse found for questionnaireName [{}].", questionnaireCode.name());
				return null;
			}
		} catch (IllegalStateException ise) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting FHIR", null);
		}
	}

	/**
	 * Saves Questionnaire as FHIR resource
	 *
	 * @param khealthinairQuestionnaire
	 * @return
	 * @throws Exception
	 */
	public String saveQuestionnaire(final Questionnaire questionnaire) {
		logger.debug("Questionnaire [{}] creation in repository...", questionnaire.getName());

		// @formatter:off
        MethodOutcome outcome = repositoryConnector
	        .client()
	        .update() // To keep the id generated and use it in the url
	        .resource(questionnaire)
	        .cacheControl(CacheControlDirective.noCache())
	        .execute();
        // @formatter:on

		if (outcome.getCreated() != null && outcome.getCreated()) {
			logger.info("Questionnaire [{}] created.", questionnaire.getName());
			return outcome.getId().getIdPart();
		} else if (outcome.getResponseStatusCode() == 200) {
			logger.info("Questionnaire [{}] created.", questionnaire.getName());
			return outcome.getId().getIdPart();
		} else {
			logger.error("Questionnaire [{}] creation/update failed. Error: {}",
					questionnaire.getIdElement().getIdPart());
			return null;
		}
	}

	/**
	 *
	 * @param QuestionnaireResponse
	 * @return
	 */
	public String saveQuestionnaireResponse(QuestionnaireResponse questionnaireResponse) {
		logger.debug("Creating QuestionnaireResponse for [{}] and Subject [{}].",
				questionnaireResponse.getQuestionnaire()
						.substring(questionnaireResponse.getQuestionnaire().lastIndexOf('/') + 1),
				questionnaireResponse.getSubject().getReference());

		// @formatter:off
        MethodOutcome outcome = repositoryConnector
	        .client()
	        .update()
	        .resource(questionnaireResponse)
	        .cacheControl(CacheControlDirective.noCache())
	        .execute();
        // @formatter:on

		if (outcome.getCreated() != null && outcome.getCreated()) {
			logger.info("QuestionnaireResponse created for [{}] and Subject [{}].",
					questionnaireResponse.getQuestionnaire()
							.substring(questionnaireResponse.getQuestionnaire().lastIndexOf('/') + 1),
					questionnaireResponse.getSubject().getReference());
			return outcome.getId().getIdPart();
		} else if (outcome.getResponseStatusCode() == 200) {
			logger.info("QuestionnaireResponse created for [{}] and Subject [{}].",
					questionnaireResponse.getQuestionnaire()
							.substring(questionnaireResponse.getQuestionnaire().lastIndexOf('/') + 1),
					questionnaireResponse.getSubject().getReference());
			return outcome.getId().getIdPart();

		} else {
			logger.error("QuestionnaireResponse [{}] creation failed. Error: {}",
					questionnaireResponse.getIdElement().getIdPart());
			return null;
		}
	}

	/**
	 * Sociodemographic questionnaire
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseSociodemographic(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.SOCIODEMOGRAPHIC.name().toLowerCase());
		response.setId(
				patientId + "-" + QuestionnaireCode.SOCIODEMOGRAPHIC.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.SOCIODEMOGRAPHIC.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_a() != null && !row.getDate_part_a().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_a()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addStringItem(response, null, "yearofbirth", row.getYearofbirth());
		addStringItem(response, null, "countryofbirth", row.getCountryofbirth());
		addStringItem(response, null, "countryofbirth2", row.getCountryofbirth2());
		addCodingItem(response, null, "sex", CodeSystem.SYSTEM_KHEALTHINAIR, row.getSex(), "Gender");
		addCodingItem(response, null, "educationlevel_oah", CodeSystem.SYSTEM_KHEALTHINAIR, row.getEducationlevel_oah(),
				"educationlevel_oah");
		addIntegerItem(response, null, "relationshipstatus", row.getRelationshipstatus());
		addBooleanItem(response, null, "work_ext___0", row.getWork_ext___0());
		addBooleanItem(response, null, "work_ext___1", row.getWork_ext___1());
		addBooleanItem(response, null, "work_ext___2", row.getWork_ext___2());
		addBooleanItem(response, null, "work_ext___3", row.getWork_ext___3());
		addBooleanItem(response, null, "work_ext___4", row.getWork_ext___4());
		addBooleanItem(response, null, "work_ext___5", row.getWork_ext___5());
		addStringItem(response, null, "work_job", row.getWork_job());
		addStringItem(response, null, "timespent5", row.getTimespent5());
		addCodingItem(response, null, "livingarrangements1", CodeSystem.SYSTEM_KHEALTHINAIR,
				row.getLivingarrangements1(), "livingarrangements1");
		addIntegerItem(response, null, "livingarrengements2", row.getLivingarrengements2());
		addCodingItem(response, null, "ethnicity", CodeSystem.SYSTEM_KHEALTHINAIR, row.getEthnicity(), "ethnicity");
		addIntegerItem(response, null, "alcoholamount", row.getAlcoholamount());
		addCodingItem(response, null, "smokingstatus1", CodeSystem.SYSTEM_KHEALTHINAIR, row.getSmokingstatus1(),
				"smokingstatus1");
		addBooleanItem(response, null, "smokestatus2___1", row.getSmokestatus2___1());
		addBooleanItem(response, null, "smokestatus2___2", row.getSmokestatus2___2());
		addBooleanItem(response, null, "smokestatus2___3", row.getSmokestatus2___3());
		addBooleanItem(response, null, "smokestatus2___4", row.getSmokestatus2___4());
		addCodingItem(response, null, "passivesmoke", CodeSystem.SYSTEM_KHEALTHINAIR, row.getPassivesmoke(),
				"passivesmoke");
		addIntegerItem(response, null, "height_2", row.getHeight_2());
		addIntegerItem(response, null, "weight_2", row.getWeight_2());
		addIntegerItem(response, null, "bmivalue_2", row.getBmivalue_2());

		if (row.getPart_a_datos_sociodemograficos_complete() == "2") {
			response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		} else {
			response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS);
		}

		return response;
	}

	/**
	 * Time spent in the scenario questionnaire
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseTimeSpentInTheScenario(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.TIMESPENTINTHESCENARIO.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.TIMESPENTINTHESCENARIO.name() + "-"
				+ row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.TIMESPENTINTHESCENARIO.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_b() != null && !row.getDate_part_b().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_b()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "timespent1", row.getTimespent1());
		addIntegerItem(response, null, "timespent2", row.getTimespent2());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * SACQ (Self-Administered Comorbidity Questionnaire)
	 *
	 * FIXME SACQ questionnaire in D1.8, pg 183, listed questions are incomplete
	 * without direct correspondency with spanish translation in BMC pilot
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseSACQ(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.SACQ.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.SACQ.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.SACQ.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_c() != null && !row.getDate_part_c().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_c()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addBooleanItem(response, null, "comorbiditiessacq___0", row.getComorbiditiessacq___0());
		addBooleanItem(response, null, "comorbiditiessacq___1", row.getComorbiditiessacq___1());
		addBooleanItem(response, null, "comorbiditiessacq___2", row.getComorbiditiessacq___2());
		addBooleanItem(response, null, "comorbiditiessacq___3", row.getComorbiditiessacq___3());
		addBooleanItem(response, null, "comorbiditiessacq___4", row.getComorbiditiessacq___4());
		addBooleanItem(response, null, "comorbiditiessacq___5", row.getComorbiditiessacq___5());
		addBooleanItem(response, null, "comorbiditiessacq___6", row.getComorbiditiessacq___6());
		addBooleanItem(response, null, "comorbiditiessacq___7", row.getComorbiditiessacq___7());
		addBooleanItem(response, null, "comorbiditiessacq___8", row.getComorbiditiessacq___8());
		addBooleanItem(response, null, "comorbiditiessacq___9", row.getComorbiditiessacq___9());
		addBooleanItem(response, null, "comorbiditiessacq___10", row.getComorbiditiessacq___10());
		addBooleanItem(response, null, "comorbiditiessacq___11", row.getComorbiditiessacq___11());
		addBooleanItem(response, null, "comorbiditiessacq___12", row.getComorbiditiessacq___12());
		addBooleanItem(response, null, "comorbiditiessacq___13", row.getComorbiditiessacq___13());
		addBooleanItem(response, null, "comorbiditiessacq___14", row.getComorbiditiessacq___14());
		addStringItem(response, null, "comorbiditiessacq_others", row.getComorbiditiessacq_others());
		addStringItem(response, null, "comorbidities14", row.getComorbidities14());
		addBooleanItem(response, null, "comorbiditiessacq_heartdiseasefu1", row.getComorbiditiessacq_heartdiseasefu1());
		addBooleanItem(response, null, "comorbiditiessacq_heartdiseasefu2", row.getComorbiditiessacq_heartdiseasefu2());
		addBooleanItem(response, null, "comorbiditiessacq_highbloodpressurefu1",
				row.getComorbiditiessacq_highbloodpressurefu1());
		addBooleanItem(response, null, "comorbiditiessacq_highbloodpressurefu2",
				row.getComorbiditiessacq_highbloodpressurefu2());
		addBooleanItem(response, null, "comorbiditiessacq_lungdiseasefu1", row.getComorbiditiessacq_lungdiseasefu1());
		addBooleanItem(response, null, "comorbiditiessacq_lungdiseasefu2", row.getComorbiditiessacq_lungdiseasefu2());
		addBooleanItem(response, null, "comorbiditiessacq_diabetesfu1", row.getComorbiditiessacq_diabetesfu1());
		addBooleanItem(response, null, "comorbiditiessacq_diabetesfu2", row.getComorbiditiessacq_diabetesfu2());
		addBooleanItem(response, null, "comorbiditiessacq_stomachdiseasefu1",
				row.getComorbiditiessacq_stomachdiseasefu1());
		addBooleanItem(response, null, "comorbiditiessacq_stomachdiseasefu2",
				row.getComorbiditiessacq_stomachdiseasefu2());
		addBooleanItem(response, null, "comorbiditiessacq_kidneydiseasefu1",
				row.getComorbiditiessacq_kidneydiseasefu1());
		addBooleanItem(response, null, "comorbiditiessacq_kidneydiseasefu2",
				row.getComorbiditiessacq_kidneydiseasefu2());
		addBooleanItem(response, null, "comorbiditiessacq_liverdiseasefu1", row.getComorbiditiessacq_liverdiseasefu1());
		addBooleanItem(response, null, "comorbiditiessacq_liverdiseasefu2", row.getComorbiditiessacq_liverdiseasefu2());
		addBooleanItem(response, null, "comorbiditiessacq_blooddiseasefu1", row.getComorbiditiessacq_blooddiseasefu1());
		addBooleanItem(response, null, "comorbiditiessacq_blooddiseasefu2", row.getComorbiditiessacq_blooddiseasefu2());
		addBooleanItem(response, null, "comorbiditiessacq_cancerfu1", row.getComorbiditiessacq_cancerfu1());
		addBooleanItem(response, null, "comorbiditiessacq_cancerfu2", row.getComorbiditiessacq_cancerfu2());
		addBooleanItem(response, null, "comorbiditiessacq_depressionfu1", row.getComorbiditiessacq_depressionfu1());
		addBooleanItem(response, null, "comorbiditiessacq_depressionfu2", row.getComorbiditiessacq_depressionfu2());
		addBooleanItem(response, null, "comorbiditiessacq_osteoarthritisfu1",
				row.getComorbiditiessacq_osteoarthritisfu1());
		addBooleanItem(response, null, "comorbiditiessacq_osteoarthritisfu2",
				row.getComorbiditiessacq_osteoarthritisfu2());
		addBooleanItem(response, null, "comorbiditiessacq_backpainfu1", row.getComorbiditiessacq_backpainfu1());
		addBooleanItem(response, null, "comorbiditiessacq_backpainfu2", row.getComorbiditiessacq_backpainfu2());
		addBooleanItem(response, null, "comorbiditiessacq_rheumatoidarthritisfu1",
				row.getComorbiditiessacq_rheumatoidarthritisfu1());
		addBooleanItem(response, null, "comorbiditiessacq_rheumatoidarthritisfu2",
				row.getComorbiditiessacq_rheumatoidarthritisfu2());
		addBooleanItem(response, null, "comorbiditiessacq_otherfu1", row.getComorbiditiessacq_otherfu1());
		addBooleanItem(response, null, "comorbiditiessacq_otherfu2", row.getComorbiditiessacq_otherfu2());
		addIntegerItem(response, null, "comorbidities_score", row.getComorbidities_score());
		addStringItem(response, null, "cardiovascularrisk_q3", row.getCardiovascularrisk_q3());
		addStringItem(response, null, "pavsday", row.getPavsday());
		addStringItem(response, null, "pavstime", row.getPavstime());
		addStringItem(response, null, "pavsdayxpavstime", row.getPavsdayxpavstime());
		addStringItem(response, null, "seeing", row.getSeeing());
		addStringItem(response, null, "hearing", row.getHearing());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * PROMIS Global-10 Questionnaire (Patient-Reported Outcomes Measurement
	 * Information System)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponsePROMIS(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.PROMISGH10.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.PROMISGH10.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.PROMISGH10.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_c() != null && !row.getDate_part_c().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_c()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "promis_10_q01", row.getPromis_10_q01());
		addIntegerItem(response, null, "promis_10_q02", row.getPromis_10_q02());
		addIntegerItem(response, null, "promis_10_q03", row.getPromis_10_q03());
		addIntegerItem(response, null, "promis_10_q05", row.getPromis_10_q05());
		addIntegerItem(response, null, "promis_10_q09r", row.getPromis_10_q09r());
		addIntegerItem(response, null, "promis_10_08r", row.getPromis_10_08r());
		addIntegerItem(response, null, "promis_10_07r", row.getPromis_10_07r());
		addIntegerItem(response, null, "promis_10_q06", row.getPromis_10_q06());
		addIntegerItem(response, null, "promis_10_q04", row.getPromis_10_q04());
		addIntegerItem(response, null, "promis_10_q10r", row.getPromis_10_q10r());
		addIntegerItem(response, null, "promis_score1_ph", row.getPromis_score1_ph());
		addIntegerItem(response, null, "promis_score2_mh", row.getPromis_score2_mh());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * WHODAS-12 (The World Health Organization Disability Assessment Schedule 2.0)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseWHODAS12(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.WHODAS12.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.WHODAS12.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.WHODAS12.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_c() != null && !row.getDate_part_c().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_c()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "whodas_q01", row.getWhodas_q01());
		addIntegerItem(response, null, "whodas_q02", row.getWhodas_q02());
		addIntegerItem(response, null, "whodas_q03", row.getWhodas_q03());
		addIntegerItem(response, null, "whodas_q04", row.getWhodas_q04());
		addIntegerItem(response, null, "whodas_q05", row.getWhodas_q05());
		addIntegerItem(response, null, "whodas_q06", row.getWhodas_q06());
		addIntegerItem(response, null, "whodas_q07", row.getWhodas_q07());
		addIntegerItem(response, null, "whodas_q08", row.getWhodas_q08());
		addIntegerItem(response, null, "whodas_q09", row.getWhodas_q09());
		addIntegerItem(response, null, "whodas_q10", row.getWhodas_q10());
		addIntegerItem(response, null, "whodas_q11", row.getWhodas_q11());
		addIntegerItem(response, null, "whodas_q12", row.getWhodas_q12());
		addIntegerItem(response, null, "whodas_12_score_none", row.getWhodas_12_score_none());
		addIntegerItem(response, null, "whodas_12_score_mild", row.getWhodas_12_score_mild());
		addIntegerItem(response, null, "whodas_12_score_moderate", row.getWhodas_12_score_moderate());
		addIntegerItem(response, null, "whodas_12_score_severe", row.getWhodas_12_score_severe());
		addIntegerItem(response, null, "whodas_12_score_extreme", row.getWhodas_12_score_extreme());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * WHO-5 (The World Health Organization-Five Well-Being Index)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseWHO5(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.WHO5.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.WHO5.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.WHO5.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_c() != null && !row.getDate_part_c().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_c()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "who_5_q01", row.getWho_5_q01());
		addIntegerItem(response, null, "who_5_q02", row.getWho_5_q02());
		addIntegerItem(response, null, "who_5_q03", row.getWho_5_q03());
		addIntegerItem(response, null, "who_5_q04", row.getWho_5_q04());
		addIntegerItem(response, null, "who_5_q05", row.getWho_5_q05());
		addIntegerItem(response, null, "who_5_score_sum", row.getWho_5_score_sum());
		addIntegerItem(response, null, "who_5_score", row.getWho_5_score());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * PaRIS (Patient-Reported Indicator Surveys)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponsePaRIS(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.PARIS.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.PARIS.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.PARIS.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_part_d() != null && !row.getDate_part_d().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_part_d()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// Accessibility
		QuestionnaireResponseItemComponent accesibility = new QuestionnaireResponseItemComponent();
		addStringItem(response, accesibility, "accesibility1", row.getAccesibility1());
		addStringItem(response, accesibility, "accesibility2", row.getAccesibility2());
		addIntegerItem(response, accesibility, "accesibility3", row.getAccesibility3());
		addStringItem(response, accesibility, "accesibility3_1", row.getAccesibility3_1());
		addIntegerItem(response, accesibility, "accesibility4", row.getAccesibility4());
		addStringItem(response, accesibility, "accesibility4_1", row.getAccesibility4_1());
		addStringItem(response, accesibility, "accesibility5", row.getAccesibility5());
		response.addItem(accesibility);

		// Communication
		QuestionnaireResponseItemComponent communication = new QuestionnaireResponseItemComponent();
		addStringItem(response, communication, "communication1", row.getCommunication1());
		addStringItem(response, communication, "communication2", row.getCommunication2());
		addStringItem(response, communication, "communication3", row.getCommunication3());
		response.addItem(communication);

		// Shared Decision Making
		QuestionnaireResponseItemComponent sharedecisionmaking = new QuestionnaireResponseItemComponent();
		addStringItem(response, sharedecisionmaking, "sharedecisionmaking1", row.getSharedecisionmaking1());
		addStringItem(response, sharedecisionmaking, "sharedecisionmaking2", row.getSharedecisionmaking2());
		response.addItem(sharedecisionmaking);

		// Continuity and Coordination
		QuestionnaireResponseItemComponent continuityandcoordination = new QuestionnaireResponseItemComponent();
		addStringItem(response, continuityandcoordination, "continuityandcoordination1",
				row.getContinuityandcoordination1());
		addIntegerItem(response, continuityandcoordination, "continuityandcoordination2",
				row.getContinuityandcoordination2());
		addStringItem(response, continuityandcoordination, "continuityandcoordination2_1",
				row.getContinuityandcoordination2_1());
		addStringItem(response, continuityandcoordination, "continuityandcoordination3",
				row.getContinuityandcoordination3());
		addStringItem(response, continuityandcoordination, "continuityandcoordination4",
				row.getContinuityandcoordination4());
		response.addItem(continuityandcoordination);

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * Observational IPCHEM checklist
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseObservationalIPCHEM(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.OBSERVATIONALIPCHEM.name().toLowerCase());
		response.setId(
				patientId + "-" + QuestionnaireCode.OBSERVATIONALIPCHEM.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.OBSERVATIONALIPCHEM.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate() != null && !row.getDate().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// Residencial characteristics
		QuestionnaireResponseItemComponent residencialCharacteristics = new QuestionnaireResponseItemComponent();
		addIntegerItem(response, residencialCharacteristics, "type_of_home", row.getType_of_home()); // 1 Casa, 2 Piso
		addIntegerItem(response, residencialCharacteristics, "room_with_window_on_street",
				row.getRoom_with_window_on_street());
		addIntegerItem(response, residencialCharacteristics, "elevator", row.getElevator());
		addIntegerItem(response, residencialCharacteristics, "dust", row.getDust());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___1", row.getRemaining_material___1());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___2", row.getRemaining_material___2());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___3", row.getRemaining_material___3());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___4", row.getRemaining_material___4());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___5", row.getRemaining_material___5());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___6", row.getRemaining_material___6());
		addIntegerItem(response, residencialCharacteristics, "remaining_material___7", row.getRemaining_material___7());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element___1", row.getAir_dct_element___1());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element___2", row.getAir_dct_element___2());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element___3", row.getAir_dct_element___3());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element___4", row.getAir_dct_element___4());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element___5", row.getAir_dct_element___5());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element2___1", row.getAir_dct_element2___1());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element2___2", row.getAir_dct_element2___2());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element2___3", row.getAir_dct_element2___3());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element2___4", row.getAir_dct_element2___4());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element2___5", row.getAir_dct_element2___5());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element2___6", row.getAir_dct_element2___6());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___1", row.getAir_dct_element3___1());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___2", row.getAir_dct_element3___2());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___3", row.getAir_dct_element3___3());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___4", row.getAir_dct_element3___4());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___5", row.getAir_dct_element3___5());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___6", row.getAir_dct_element3___6());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___7", row.getAir_dct_element3___7());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element3___8", row.getAir_dct_element3___8());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___1", row.getAir_dct_element4___1());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___2", row.getAir_dct_element4___2());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___3", row.getAir_dct_element4___3());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___4", row.getAir_dct_element4___4());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___5", row.getAir_dct_element4___5());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___6", row.getAir_dct_element4___6());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element4___7", row.getAir_dct_element4___7());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element5___1", row.getAir_dct_element5___1());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element5___2", row.getAir_dct_element5___2());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element5___3", row.getAir_dct_element5___3());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element5___4", row.getAir_dct_element5___4());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element5___5", row.getAir_dct_element5___5());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element5___6", row.getAir_dct_element5___6());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___1", row.getAir_dct_element6___1());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___2", row.getAir_dct_element6___2());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___3", row.getAir_dct_element6___3());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___4", row.getAir_dct_element6___4());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___5", row.getAir_dct_element6___5());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___6", row.getAir_dct_element6___6());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___7", row.getAir_dct_element6___7());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___8", row.getAir_dct_element6___8());
		addIntegerItem(response, residencialCharacteristics, "air_dct_element6___9", row.getAir_dct_element6___9());
		response.addItem(residencialCharacteristics);

		// Description of sampling microenvironment
		QuestionnaireResponseItemComponent samplingMicroenvironment = new QuestionnaireResponseItemComponent();
//		deshumidificador
		addIntegerItem(response, samplingMicroenvironment, "room_with_window_on_street",
				row.getRoom_with_window_on_street());
		addIntegerItem(response, samplingMicroenvironment, "number_of_times_per_day_the_windows_are_opened",
				row.getNumber_of_times_per_day_the_windows_are_opened());
		addIntegerItem(response, samplingMicroenvironment, "traffic_intensity_around_sampling_site",
				row.getTraffic_intensity_around_sampling_site());
		addIntegerItem(response, samplingMicroenvironment, "sampling_site_dimension_m3",
				row.getSampling_site_dimension_m3());
		response.addItem(samplingMicroenvironment);

		// Surroundings
		QuestionnaireResponseItemComponent surroundings = new QuestionnaireResponseItemComponent();
		addIntegerItem(response, surroundings, "dwelling_humid", row.getDwelling_humid());
		addIntegerItem(response, surroundings, "construction___1", row.getConstruction___1());
		addIntegerItem(response, surroundings, "construction___2", row.getConstruction___2());
		addIntegerItem(response, surroundings, "construction___3", row.getConstruction___3());
		addIntegerItem(response, surroundings, "green_area", row.getGreen_area());
		addIntegerItem(response, surroundings, "traffic", row.getTraffic());
		response.addItem(surroundings);

		// Pets
		QuestionnaireResponseItemComponent pets = new QuestionnaireResponseItemComponent();
		addIntegerItem(response, pets, "pets", row.getPets());
		addIntegerItem(response, pets, "typepet", row.getTypepet());
		response.addItem(pets);

		// Cooking
		QuestionnaireResponseItemComponent cooking = new QuestionnaireResponseItemComponent();
		addIntegerItem(response, cooking, "typeofcooker", row.getTypeofcooker());
		addIntegerItem(response, cooking, "extractor", row.getExtractor());
		response.addItem(cooking);

		// Heating and cooling
		QuestionnaireResponseItemComponent heatingAndCooling = new QuestionnaireResponseItemComponent();
		addIntegerItem(response, heatingAndCooling, "gasboiler_bath", row.getGasboiler_bath());
		addIntegerItem(response, heatingAndCooling, "gasheater_dwelling", row.getGasheater_dwelling());
		addIntegerItem(response, heatingAndCooling, "type_of_heating_system___1", row.getType_of_heating_system___1());
		addIntegerItem(response, heatingAndCooling, "type_of_heating_system___2", row.getType_of_heating_system___2());
		addIntegerItem(response, heatingAndCooling, "type_of_heating_system___3", row.getType_of_heating_system___3());
		addIntegerItem(response, heatingAndCooling, "type_of_heating_system___4", row.getType_of_heating_system___4());
		addIntegerItem(response, heatingAndCooling, "type_of_heating_system___5", row.getType_of_heating_system___5());
		addIntegerItem(response, heatingAndCooling, "type_of_heating_system___6", row.getType_of_heating_system___6());
		addIntegerItem(response, heatingAndCooling, "cooling___0", row.getCooling___0());
		addIntegerItem(response, heatingAndCooling, "cooling___1", row.getCooling___1());
		addIntegerItem(response, heatingAndCooling, "cooling___2", row.getCooling___2());
		addIntegerItem(response, heatingAndCooling, "cooling___3", row.getCooling___3());
		addIntegerItem(response, heatingAndCooling, "heating_system_other", row.getHeating_system_other());
		response.addItem(heatingAndCooling);

		// General cleaning
		QuestionnaireResponseItemComponent generalCleaning = new QuestionnaireResponseItemComponent();
		addIntegerItem(response, generalCleaning, "general_cleaning", row.getGeneral_cleaning());
		addIntegerItem(response, generalCleaning, "cleaning_products", row.getCleaning_products());
		addIntegerItem(response, generalCleaning, "cleaning_methods", row.getCleaning_methods());
		addIntegerItem(response, generalCleaning, "allergic_cleaning_product", row.getAllergic_cleaning_product());
		response.addItem(generalCleaning);

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * FS/FOT (Forced Spirometry/Forced Oscillation Technique)
	 *
	 * @return
	 */
//	public QuestionnaireResponse createResponseFSFOT(String patientId, RedCAPRow row) {
//
//		QuestionnaireResponse response = new QuestionnaireResponse();
//		response.setSubject(new Reference("Patient/" + patientId));
//		response.setQuestionnaire(
//				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.FSFOT.name().toLowerCase());
//		response.setId(patientId + "-" + QuestionnaireCode.FSFOT.name() + "-" + row.getRedcap_repeat_instance());
//		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
//				.setValue(QuestionnaireCode.FSFOT.getQuestionnairePart().name()));
//		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
//				.setValue(row.getRedcap_repeat_instrument()));
//		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
//				.setValue(row.getRedcap_repeat_instance().toString()));
//
//		if (row.getF1() != null && !row.getF1().isEmpty()) {
//			try {
//				response.setAuthored(sdf_datetime_dmy.parse(row.getF1()));
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}

	// TBC

//		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
//		return response;
//	}

	/**
	 * CAT (COPD Assessment Test)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseCAT(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.CAT.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.CAT.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.CAT.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_cat() != null && !row.getDate_cat().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_cat()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "cat_1", row.getCat_1()); // Cough
		addIntegerItem(response, null, "cat_2", row.getCat_2()); // Phlegm (sputum production)
		addIntegerItem(response, null, "cat_3", row.getCat_3()); // Chest tightness
		addIntegerItem(response, null, "cat_4", row.getCat_4()); // Breathlessness
		addIntegerItem(response, null, "cat_5", row.getCat_5()); // Activity limitation
		addIntegerItem(response, null, "cat_6", row.getCat_6()); // Confidence leaving home
		addIntegerItem(response, null, "cat_7", row.getCat_7()); // Sleep quality
		addIntegerItem(response, null, "cat_8", row.getCat_8()); // Energy level

		addIntegerItem(response, null, "cat_score", row.getCat_score());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * mMRC (COPD Assessment Test)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseMMRC(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.MMRC.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.MMRC.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.MMRC.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_mmrc() != null && !row.getDate_mmrc().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_mmrc()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (row.getMmrc_score() != null && !row.getMmrc_score().isEmpty()) {
			switch (row.getMmrc_score()) {
			case "0":
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_KHEALTHINAIR, row.getMmrc_score(),
						"Dyspnea only with strenuous exercise");
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_LOINC, "LA9629-2",
						"Grade 1: Not troubled by breathlessness except on strenuous exercise");
				break;
			case "1":
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_KHEALTHINAIR, row.getMmrc_score(),
						"Dyspnea when hurrying or walking up a slight hill");
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_LOINC, "LA9630-0",
						"Grade 2: Short of breath when hurrying on the level or walking up a slight hill");
				break;
			case "2":
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_KHEALTHINAIR, row.getMmrc_score(),
						"Walks slower than people of the same age because of dyspnea or has to stop for breath when walking at own pace");
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_LOINC, "LA9631-8",
						"Grade 3: Walks slower than most people on the level, stops after a mile or so, or stops after 15 minutes walking at own pace");
				break;
			case "3":
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_KHEALTHINAIR, row.getMmrc_score(),
						"Stops for breath after walking 100 yards (91 m) or after a few minutes");
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_LOINC, "LA11897-8",
						"Grade 4: Stops for breath after walking about 100 yds or after a few minutes on level ground");
				break;
			case "4":
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_KHEALTHINAIR, row.getMmrc_score(),
						"I am too breathless to leave the house, or I am breathless when dressing/undressing");
				addCodingItem(response, null, "mmrc_score", CodeSystem.SYSTEM_LOINC, "LA11898-6",
						"Grade 5: Too breathless to leave the house, or breathless when undressing");
				break;
			default:
				throw new IllegalArgumentException("QuestionnaireResponse mmrc_score answer value type ["
						+ row.getMmrc_score() + "] not expected 0-4.");
			}
		}
		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * TAI-12 (Test of Adherence to Inhalers 12)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseTAI12(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.TAI12.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.TAI12.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.TAI12.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_tai() != null && !row.getDate_tai().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_tai()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "tai1", row.getTai1());
		addIntegerItem(response, null, "tai2", row.getTai2());
		addIntegerItem(response, null, "tai3", row.getTai3());
		addIntegerItem(response, null, "tai4", row.getTai4());
		addIntegerItem(response, null, "tai5", row.getTai5());
		addIntegerItem(response, null, "tai6", row.getTai6());
		addIntegerItem(response, null, "tai7", row.getTai7());
		addIntegerItem(response, null, "tai8", row.getTai8());
		addIntegerItem(response, null, "tai9", row.getTai8());
		addIntegerItem(response, null, "tai10", row.getTai10());
		addIntegerItem(response, null, "tai_score", row.getTai_score());
		addIntegerItem(response, null, "tai11", row.getTai11());
		addIntegerItem(response, null, "tai12", row.getTai12());
		addIntegerItem(response, null, "tai_score1", row.getTai_score1());
		addIntegerItem(response, null, "tai_score2", row.getTai_score2());
		addIntegerItem(response, null, "tai_score3", row.getTai_score3());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * MiniAQLQ (Mini Asthma Quality of Life Questionnaire)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseMiniAQLQ(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.MINIAQLQ.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.MINIAQLQ.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.MINIAQLQ.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_miniaqlq() != null && !row.getDate_miniaqlq().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_miniaqlq()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "miniaqlq1", row.getMiniaqlq1());
		addIntegerItem(response, null, "miniaqlq2", row.getMiniaqlq2());
		addIntegerItem(response, null, "miniaqlq3", row.getMiniaqlq3());
		addIntegerItem(response, null, "miniaqlq4", row.getMiniaqlq4());
		addIntegerItem(response, null, "miniaqlq5", row.getMiniaqlq5());
		addIntegerItem(response, null, "miniaqlq6", row.getMiniaqlq6());
		addIntegerItem(response, null, "miniaqlq7", row.getMiniaqlq7());
		addIntegerItem(response, null, "miniaqlq8", row.getMiniaqlq8());
		addIntegerItem(response, null, "miniaqlq9", row.getMiniaqlq9());
		addIntegerItem(response, null, "miniaqlq10", row.getMiniaqlq10());
		addIntegerItem(response, null, "miniaqlq11", row.getMiniaqlq11());
		addIntegerItem(response, null, "miniaqlq12", row.getMiniaqlq12());
		addIntegerItem(response, null, "miniaqlq13", row.getMiniaqlq13());
		addIntegerItem(response, null, "miniaqlq14", row.getMiniaqlq14());
		addIntegerItem(response, null, "miniaqlq15", row.getMiniaqlq15());
		addIntegerItem(response, null, "totalscore_miniaqlq", row.getTotalscore_miniaqlq());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * ACT (Asthma Control Test)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseACT(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.ACT.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.ACT.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.ACT.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_act() != null && !row.getDate_act().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_act()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "act_1", row.getAct_1());
		addIntegerItem(response, null, "act_2", row.getAct_2());
		addIntegerItem(response, null, "act_3", row.getAct_3());
		addIntegerItem(response, null, "act_4", row.getAct_4());
		addIntegerItem(response, null, "act_5", row.getAct_5());
		addIntegerItem(response, null, "act_score", row.getAct_score());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * SNOT-22 (Sino-Nasal Outcome Test)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseSNOT22(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.SNOT22.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.SNOT22.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.SNOT22.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getDate_snot22() != null && !row.getDate_snot22().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getDate_snot22()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "snot1", row.getSnot1());
		addIntegerItem(response, null, "snot2", row.getSnot2());
		addIntegerItem(response, null, "snot3", row.getSnot3());
		addIntegerItem(response, null, "snot4", row.getSnot4());
		addIntegerItem(response, null, "snot5", row.getSnot5());
		addIntegerItem(response, null, "snot6", row.getSnot6());
		addIntegerItem(response, null, "snot7", row.getSnot7());
		addIntegerItem(response, null, "snot8", row.getSnot8());
		addIntegerItem(response, null, "snot9", row.getSnot9());
		addIntegerItem(response, null, "snot10", row.getSnot10());
		addIntegerItem(response, null, "snot11", row.getSnot11());
		addIntegerItem(response, null, "snot12", row.getSnot12());
		addIntegerItem(response, null, "snot13", row.getSnot13());
		addIntegerItem(response, null, "snot14", row.getSnot14());
		addIntegerItem(response, null, "snot15", row.getSnot15());
		addIntegerItem(response, null, "snot16", row.getSnot16());
		addIntegerItem(response, null, "snot17", row.getSnot17());
		addIntegerItem(response, null, "snot18", row.getSnot18());
		addIntegerItem(response, null, "snot19", row.getSnot19());
		addIntegerItem(response, null, "snot20", row.getSnot20());
		addIntegerItem(response, null, "snot21", row.getSnot21());
		addIntegerItem(response, null, "snot22", row.getSnot22());
		// snot23 22 true/false options (maximum of 5 items)
		addIntegerItem(response, null, "snot23", row.getSnot23___1()); // Need to blow nose
		addIntegerItem(response, null, "snot23", row.getSnot23___2()); // Nasal congestion/obstruction
		addIntegerItem(response, null, "snot23", row.getSnot23___3()); // Sneezing
		addIntegerItem(response, null, "snot23", row.getSnot23___4()); // Continuous nasal discharge
		addIntegerItem(response, null, "snot23", row.getSnot23___5()); // Cough
		addIntegerItem(response, null, "snot23", row.getSnot23___6()); // Feeling of mucus draining down the back of the
																		// throat
		addIntegerItem(response, null, "snot23", row.getSnot23___7()); // Thick nasal discharge
		addIntegerItem(response, null, "snot23", row.getSnot23___8()); // Feeling of blocked ear
		addIntegerItem(response, null, "snot23", row.getSnot23___9()); // Dizziness
		addIntegerItem(response, null, "snot23", row.getSnot23___10()); // Ear pain
		addIntegerItem(response, null, "snot23", row.getSnot23___11()); // Facial pain/pressure
		addIntegerItem(response, null, "snot23", row.getSnot23___12()); // Changes in taste/smell
		addIntegerItem(response, null, "snot23", row.getSnot23___13()); // Difficulty falling asleep
		addIntegerItem(response, null, "snot23", row.getSnot23___14()); // Waking up during the night
		addIntegerItem(response, null, "snot23", row.getSnot23___15()); // Poor sleep quality
		addIntegerItem(response, null, "snot23", row.getSnot23___16()); // Waking up feeling tired
		addIntegerItem(response, null, "snot23", row.getSnot23___17()); // Fatigue or tiredness
		addIntegerItem(response, null, "snot23", row.getSnot23___18()); // Reduced performance/productivity
		addIntegerItem(response, null, "snot23", row.getSnot23___19()); // Reduced concentration
		addIntegerItem(response, null, "snot23", row.getSnot23___20()); // Frustrated/restless/irritable
		addIntegerItem(response, null, "snot23", row.getSnot23___21()); // Sad
		addIntegerItem(response, null, "snot23", row.getSnot23___22()); // Embarrassed
		addIntegerItem(response, null, "snot_score", row.getSnot_score());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * EQ-5D-5L (5-level EQ-5D Questionnaire)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseEQ5D5L(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.EQ5D5L.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.EQ5D5L.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.EQ5D5L.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getEq5d5y_for_students_timestamp() != null && !row.getEq5d5y_for_students_timestamp().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getEq5d5y_for_students_timestamp()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "mobility", row.getMobility());
		addIntegerItem(response, null, "self_care", row.getSelf_care());
		addIntegerItem(response, null, "usual_activities", row.getUsual_activities());
		addIntegerItem(response, null, "pain_discomfort", row.getPain_discomfort());
		addIntegerItem(response, null, "anxiety_depression", row.getAnxiety_depression());
		addIntegerItem(response, null, "health_today_scale", row.getHealth_today_scale());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * EQ-5D-Y-5L (5-level EQ-5D Questionnaire Youth)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponseEQ5DY5L(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/"
				+ QuestionnaireCode.EQ5DY5L.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.EQ5DY5L.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.EQ5DY5L.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getEq5d5y_for_students_timestamp() != null && !row.getEq5d5y_for_students_timestamp().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getEq5d5y_for_students_timestamp()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "mobility", row.getMobility());
		addIntegerItem(response, null, "self_care", row.getSelf_care());
		addIntegerItem(response, null, "usual_activities", row.getUsual_activities());
		addIntegerItem(response, null, "pain_discomfort", row.getPain_discomfort());
		addIntegerItem(response, null, "anxiety_depression", row.getAnxiety_depression());
		addIntegerItem(response, null, "health_today_scale", row.getHealth_today_scale());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

	/**
	 * PHQ-9 (Patient Health Questionnaire)
	 *
	 * @return
	 */
	public QuestionnaireResponse createResponsePHQ9(String patientId, RedCAPRow row) {

		QuestionnaireResponse response = new QuestionnaireResponse();
		response.setSubject(new Reference("Patient/" + patientId));
		response.setQuestionnaire(
				CodeSystem.SYSTEM_KHEALTHINAIR + "/fhir/Questionnaire/" + QuestionnaireCode.PHQ9.name().toLowerCase());
		response.setId(patientId + "-" + QuestionnaireCode.PHQ9.name() + "-" + row.getRedcap_repeat_instance());
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/part")
				.setValue(QuestionnaireCode.PHQ9.getQuestionnairePart().name()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instrument")
				.setValue(row.getRedcap_repeat_instrument()));
		response.addIdentifier(new Identifier().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR + "/redcap-instance")
				.setValue(row.getRedcap_repeat_instance().toString()));

		if (row.getPhq9_timestamp() != null && !row.getPhq9_timestamp().isEmpty()) {
			try {
				response.setAuthored(sdf_datetime_dmy.parse(row.getPhq9_timestamp()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		addIntegerItem(response, null, "mh1", row.getMh1());
		addIntegerItem(response, null, "mh2", row.getMh2());
		addIntegerItem(response, null, "mh3", row.getMh3());
		addIntegerItem(response, null, "mh4", row.getMh4());
		addIntegerItem(response, null, "mh5", row.getMh5());
		addIntegerItem(response, null, "mh6", row.getMh6());
		addIntegerItem(response, null, "mh7", row.getMh7());
		addIntegerItem(response, null, "mh8", row.getMh8());
		addIntegerItem(response, null, "mh9", row.getMh9());
		addIntegerItem(response, null, "mh10", row.getMh9()); // Adapted Poland
		addIntegerItem(response, null, "mh11", row.getMh9()); // Adapted Poland
		addIntegerItem(response, null, "mentalhealthscore", row.getMentalhealthscore());
		addIntegerItem(response, null, "mentalhealthscore8", row.getMentalhealthscore8());

		response.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		return response;
	}

//	/**
//	 * Create a Questionnaire given the structure
//	 *
//	 * @param khiaQuestionnaire
//	 * @return string with the repository result
//	 */
//	public String createQuestionnaire(final net.atos.ari.health.khealthinair.model.Questionnaire khiaQuestionnaire) {
//		logger.info("Creating khiaQuestionnaire in repository: {}", khiaQuestionnaire);
//
//		// Create a bundle that will be used as a transaction
//		Bundle bundle = new Bundle();
//
//		Observation questionnaire = new Observation();
//
//		// Patient, If exists we need patiendRef to the subject
//                String patientId = khiaQuestionnaire.getPatientId();
//                // Getting patientRef (the identifier of the patient in FHIR) for an existing patient
//                String patientRef = "";
//                if (patientRef == null) {
//                        // Not exits so we create a new patient
//                        /*Patient newPatient = repositoryConnector.createPatientResource(patientId);
//                        bundle.addEntry()
//                                .setResource(newPatient)
//                                .getRequest()
//                                .setUrl(newPatient.getId())
//                                .setMethod(HTTPVerb.POST);
//                        //See patiendRef
//                        patientRef = newPatient.getId();*/
//                        return "No patient found with ID " + patientId + ". Please, insert the patient first.";
//                }
//
//                logger.debug("Patient reference is {} for khiaQuestionnaire", patientRef);
//                // Assigning patientRef to the questionnaire
//                questionnaire.setSubject(new Reference(patientRef));
//                // End Patient section
//
//		// Temporary UUID so that other resources in the transaction can refer to it
//		questionnaire.setId(IdType.newRandomUuid());
//
//		questionnaire.addCategory()
//			.addCoding()
//			.setSystem("http://hl7.org/fhir/observation-category")
//			.setCode("survey")
//			.setDisplay("Survey");
//
//		questionnaire.setStatus(ObservationStatus.REGISTERED);
//
//		questionnaire.getCode()
//			.addCoding()
//			.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//			.setCode("405152002")
//			.setDisplay("Quality of life satisfaction (observable entity)");
//
//		questionnaire.getValueIntegerType().setValue(khiaQuestionnaire.getScoreQuest());
//
//		questionnaire.getReferenceRangeFirstRep().getType()
//		        .addCoding()
//		        .setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//		        .setCode(khiaQuestionnaire.getTypeQuestionnaire().getCode())
//		        .setDisplay(khiaQuestionnaire.getTypeQuestionnaire().getDisplay());
//		questionnaire.addReferenceRange().setText(khiaQuestionnaire.getTypeQuestionnaire().getName());
//
//		questionnaire.getEffectiveDateTimeType().setValue(khiaQuestionnaire.getDate());
//
//		bundle.setType(Bundle.BundleType.TRANSACTION);
//
//		// Add the Questionnaire as an entry
//		bundle.addEntry()
//		   .setFullUrl(questionnaire.getIdElement().getValue())
//		   .setResource(questionnaire)
//		   .getRequest()
//		      .setUrl("Observation")
//		      .setMethod(Bundle.HTTPVerb.POST);
//
//		Bundle resp = repositoryConnector.executeTransactionWithBundle(bundle);
//
//		// Log the response
//		logger.info("Creating observation in repository response: {}", repositoryConnector.ctx().newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
//
//		return repositoryConnector.ctx().newJsonParser().setPrettyPrint(true).encodeResourceToString(resp);
//	}
//
//	/**
//      * Finds existing questionnaire in the repository
//      *
//      * @param questionnaireName
//      * @return questionnaire reference or null
//      */
//     protected  String findQuestionnaireRef(String questionnaireName) {
//             // Checking if questionnaireRef already exists
//             logger.debug("Checking if questionnaire {} already exists in the repository...", questionnaireName);
//
//             Questionnaire questionnaire = getQuestionnaireByRef(questionnaireName);
//
//             if (questionnaire == null) {
//                     logger.debug("No questionnaire found with provided name {}", questionnaireName);
//                     return null;
//             } else {
//                     logger.debug("Questionnaire found with name: {} and fhir name: {}", questionnaireName, questionnaire.getName()); //questionnaire.getIdElement().getIdPart()
//                     //return questionnaire.getResourceType() + "/" + questionnaire.getIdElement().getIdPart(); //questionnaire.getName()
//                     return questionnaire.getName();
//             } // questionnaire.getIdElement().getResourceType()
//     }
//
//     /**
//      * Finds existing questionnaire in the repository
//      *
//      * @param questionnaireName
//      * @return questionnaire reference or null
//      */
//     protected  Questionnaire getQuestionnaireByRef(String questionnaireName) {
//             // Checking if questionnaireRef already exists
//             logger.debug("Checking if questionnaire {} already exists in the repository...", questionnaireName);
//             Bundle questionnaireSearch = repositoryConnector.client().search()
//                             .forResource(Questionnaire.class)
//                             .where(Questionnaire.NAME.matchesExactly().value(questionnaireName))
//                             //.where(Questionnaire.IDENTIFIER.exactly().systemAndIdentifier(SYSTEM, questionnaireName))
//                             .returnBundle(Bundle.class)
//                             .cacheControl(CacheControlDirective.noCache()).execute();
//
//             if (questionnaireSearch.getTotal() == 0) {
//                     logger.debug("No questionnaire found with provided name {}", questionnaireName);
//                     return null;
//             } else {
//                     Questionnaire questionnaire = (Questionnaire) questionnaireSearch.getEntryFirstRep().getResource();
//                     logger.debug("Questionnaire found with name: {} and fhir name: {}", questionnaireName, questionnaire.getName()); // .getIdElement().getIdPart()
//                     return questionnaire;
//             }
//     }
//
//     /**
//      * Creates FHIR Questionnaire object
//      *
//      * @param questionnaireName
//      * @return Questionnaire as FHIR resource
//      */
//     protected Questionnaire createQuestionnaireResource(String questionnaireName) {
//             Questionnaire questionnaire = new Questionnaire();
//             questionnaire.setName(questionnaireName); //Healthy Eating Assessment
//             questionnaire.setId(IdDt.newRandomUuid());
//
//             questionnaire.setStatus(PublicationStatus.ACTIVE);
//
//             //myNutritionalQuestionnaire.setName("Healthy Eating Assessment");
//             //questionnaire.setTitle("Healthy Eating Assessment");
//             questionnaire.setTitle(questionnaireName);
//
//             //healthyFood
//             Coding code1 = new Coding();
//             code1.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//                     .setCode("230096002")
//                     .setDisplay("Health food intake (observable entity)");
//             questionnaire.addItem()
//                     .setLinkId("1")
//                     .addCode(code1)
//                     .setPrefix("1.")
//                     .setText("How would you rate your overall habits of eating healthy foods?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //unhealthyFood
//             questionnaire.addItem()
//                     .setLinkId("2")
//                     .addCode(code1)
//                     .setPrefix("2.")
//                     .setText("How many times a day did you eat fast/fried food/or packaged snacks high in fat/salt/or sugar?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //fruit
//             Coding code3 = new Coding();
//             code3.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//                     .setCode("787758000")
//                     .setDisplay("Estimated intake of fruit servings in 24 hours (observable entity)");
//             questionnaire.addItem()
//                     .setLinkId("3")
//                     .addCode(code3)
//                     .setPrefix("3.")
//                     .setText("How many servings (1 serving = 1/2 cup) of fresh, canned, frozen or dried fruit did you eat each day?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //vegetables
//             Coding code4 = new Coding();
//             code4.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//                     .setCode("789334009")
//                     .setDisplay("Estimated intake of vegetable servings in 24 hours (observable entity)");
//             questionnaire.addItem()
//                     .setLinkId("4")
//                     .addCode(code4)
//                     .setPrefix("4.")
//                     .setText("How many servings of fresh, canned, frozen or dried vegetables did you eat each day?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //drinks
//             Coding code5 = new Coding();
//             code5.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//                     .setCode("818990008")
//                     .setDisplay("Estimated oral intake of sugar sweetened drinks in 24 hours (observable entity)");
//             questionnaire.addItem()
//                     .setLinkId("5")
//                     .addCode(code5)
//                     .setPrefix("5.")
//                     .setText("How many regular soda, sweet tea, juice, energy/sports drinks, sweetened-coffee or other sugar sweetened beverages did you drink each day?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //snacks
//             questionnaire.addItem()
//                     .setLinkId("6")
//                     .setPrefix("6.")
//                     .setText("How many times a day did you eat regular (not low-fat) snack chips or crackers?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //sweets
//             Coding code7 = new Coding();
//             code7.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//                     .setCode("226463006")
//                     .setDisplay("Sweets intake (observable entity)");
//             questionnaire.addItem()
//                     .setLinkId("7")
//                     .addCode(code7)
//                     .setPrefix("7.")
//                     .setText("How many times a day did you eat sweet foods (not the low-fat kind) or desserts, like chocolate or ice cream, and other sweets?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //butter
//             questionnaire.addItem()
//                     .setLinkId("8")
//                     .setPrefix("8.")
//                     .setText("How much margarine, butter, lard or muktuk/meat fat did you add to vegetables, bannock, potatoes, bread, corn or dried meat?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //dairy
//             Coding code9 = new Coding();
//             code9.setSystem(CodeSystem.SYSTEM_SNOMEDCT)
//                     .setCode("787756001")
//                     .setDisplay("Estimated intake of dairy foods servings in 24 hours (observable entity)");
//             questionnaire.addItem()
//                     .setLinkId("9")
//                     .addCode(code9)
//                     .setPrefix("9.")
//                     .setText("How many times a day did you eat dairy products (milk, unsweetened yogurt, low fat cheese)?")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //meatFishBeans
//             questionnaire.addItem()
//                     .setLinkId("10")
//                     .setPrefix("10.")
//                     .setText("How many times a day did you eat meat/fish/beans? (Circle one number)")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             //total
//             questionnaire.addItem()
//                     .setLinkId("11")
//                     .setText("Total score")
//                     .setType(QuestionnaireItemType.INTEGER);
//
//             logger.debug("New questionnaire name: {}", questionnaire.getName());
//             return questionnaire;
//     }
//
//	public byte[] getPatientData(String patientId) {
//
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
//		try {
//
//			ColumnPositionMappingStrategy<MLTabulatedExportFileRecord> mapStrategy = new ColumnPositionMappingStrategy<>();
//			mapStrategy.setType(MLTabulatedExportFileRecord.class);
//			CSVWriter writer = new CSVWriter(outputStreamWriter, MLTabulatedExportFileRecord.CSV_SEPARATOR,
//					Character.MIN_VALUE, '"', System.lineSeparator());
//			StatefulBeanToCsv<MLTabulatedExportFileRecord> csvBuilder = new StatefulBeanToCsvBuilder<MLTabulatedExportFileRecord>(
//					writer).withQuotechar(ICSVWriter.NO_QUOTE_CHARACTER).withMappingStrategy(mapStrategy)
//					.withSeparator(MLTabulatedExportFileRecord.CSV_SEPARATOR).build();
//
//			String[] header = { "patientId", "date", "questionnaire", "question", "answer", "value" };
//			writer.writeNext(header);
//
//			if (null != patientId) {
//				logger.info("ML file generation, getting patient [Patient/{}] data...", patientId);
//				getExportFileData(csvBuilder, patientId);
//			} else {
//				logger.info("ML file generation, getting all patients data...");
//				List<String> patients = patientService.getPatientsIds();
//				if (null == patients || patients.size() == 0) {
//					throw new SubjectNotFoundException("No patients found.");
//				}
//				for (String patient_Id : patients) {
//					getExportFileData(csvBuilder, patient_Id);
//				}
//			}
//			writer.close();
//			logger.info("ML file generation ended.");
//			return byteArrayOutputStream.toByteArray();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

//	public void getExportFileData(StatefulBeanToCsv<TabulatedExportFileRecord> csvBuilder, String patientId)
//			throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
//
//		csvBuilder.write(new MLTabulatedExportFileRecord(patientId, "InclusionDate", OffsetDateTime.now().toString(),
//				offsetDateTimeFormatter.format(LocalTime.now())));
//
//		// TBC
//
//	}

	/**
	 *
	 * @param response
	 * @param itemComponent
	 * @param linkId
	 * @param value
	 */
	private void addStringItem(QuestionnaireResponse response, QuestionnaireResponseItemComponent itemComponent,
			String linkId, String value) {

		if (value != null && !value.trim().isEmpty()) {
			if (null != itemComponent) {
				itemComponent.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
						.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
								.setValue(new StringType(value))));
			} else {
				if (value != null && !value.trim().isEmpty()) {
					response.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
							.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
									.setValue(new StringType(value))));
				}
			}

		}
	}

	/**
	 *
	 * @param response
	 * @param itemComponent
	 * @param linkId
	 * @param system
	 * @param code
	 * @param display
	 */
	private void addCodingItem(QuestionnaireResponse response, QuestionnaireResponseItemComponent itemComponent,
			String linkId, String system, String code, String display) {

		if (code != null && !code.trim().isEmpty()) {
			if (null != itemComponent) {
				itemComponent.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
						.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
								.setValue(new Coding(system, code, display))));
			} else {
				response.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
						.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
								.setValue(new Coding(system, code, display))));
			}
		}
	}

	/**
	 *
	 * @param response
	 * @param itemComponent
	 * @param linkId
	 * @param value
	 */
	private void addBooleanItem(QuestionnaireResponse response, QuestionnaireResponseItemComponent itemComponent,
			String linkId, String value) {
		if (value != null && !value.trim().isEmpty()) {
			if (null != itemComponent) {
				itemComponent.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
						.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
								.setValue(new BooleanType("1".equals(value)))));
			} else {
				response.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
						.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
								.setValue(new BooleanType("1".equals(value)))));
			}
		}
	}

	/**
	 *
	 * @param response
	 * @param itemComponent
	 * @param linkId
	 * @param value
	 */
	private void addIntegerItem(QuestionnaireResponse response, QuestionnaireResponseItemComponent itemComponent,
			String linkId, String value) {
		if (value != null && !value.trim().isEmpty()) {
			try {
				if (null != itemComponent) {
					itemComponent
							.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
									.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
											.setValue(new IntegerType(Integer.parseInt(value)))));
				} else {
					response.addItem(new QuestionnaireResponse.QuestionnaireResponseItemComponent().setLinkId(linkId)
							.addAnswer(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
									.setValue(new IntegerType(Integer.parseInt(value)))));
				}
			} catch (NumberFormatException e) {
				logger.error("Invalid integer value [{}]", value);
			}
		}
	}

	public boolean isMultipleChoice(String fieldName) {
		if (Pattern.matches(".*___\\d+$", fieldName)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get an Excel file with the specific data defined for ML for a specific
	 * patient or all them
	 *
	 * @param patientId
	 * @return
	 */
	public byte[] getQuestionnaireResponses(QuestionnaireCode questionnaireCode, String patientId) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
		try {
			List<QuestionnaireResponse> questionnaireResponses = getQuestionnaireResponsesByPatientIdQuestionnaireName(
					patientId, questionnaireCode);
			if (questionnaireResponses != null && questionnaireResponses.size() > 0) {

				List<String> questionnaireFields = RedCAPRow.getFieldNamesByQuestionnaireCode(RedCAPRow.class,
						questionnaireCode);
				questionnaireFields.add(0, "patientId");
				CSVWriter writer = new CSVWriter(outputStreamWriter, CSV_SEPARATOR, Character.MIN_VALUE, '"',
						System.lineSeparator());
				writer.writeNext(questionnaireFields.toArray(new String[0]));

				for (QuestionnaireResponse questionnaireResponse : questionnaireResponses) {
					logger.debug("QuestionnaireResponse [{}] found.", questionnaireResponse.getIdElement().getIdPart());
					// QuestionnaireResponse o the QuestionnaireCode kind,
					// we have to check if is plain list of answers or has groups of them:
					// - If questionnaireResponseItemComponent ...
					// - If questionnaireResponseItemComponent ...
					// Questionnaires with groupsComposed

					Map<String, String> answers = new HashMap<>();
					List<QuestionnaireResponseItemComponent> questionnaireResponseItemComponents = questionnaireResponse
							.getItem();

					List<String> csvRow = new ArrayList<>();
					csvRow.add(questionnaireResponse.getSubject().getReference().replace("Patient/", ""));
					if (null != questionnaireResponse.getAuthored()) {
						csvRow.add(sdf_datetime_dmy.format(questionnaireResponse.getAuthored()));
					} else {
						logger.error("questionnaireResponse [{}] found (without Authored date)",
								questionnaireResponse.getIdElement().getIdPart());
					}

					for (QuestionnaireResponseItemComponent questionnaireResponseItemComponent : questionnaireResponseItemComponents) {
						List<QuestionnaireResponseItemAnswerComponent> questionnaireResponseItemAnswerComponents = questionnaireResponseItemComponent
								.getAnswer();
						if (questionnaireResponseItemAnswerComponents.size() == 1) {

						}
						QuestionnaireResponseItemAnswerComponent questionnaireResponseItemAnswerComponent = questionnaireResponseItemAnswerComponents
								.get(0);

						if (null != questionnaireResponseItemAnswerComponent) {
							switch (questionnaireResponseItemAnswerComponent.getValue().fhirType().toString()) {
							case "string":
								answers.put(questionnaireResponseItemComponent.getLinkId(),
										questionnaireResponseItemAnswerComponent.getValue().toString());
								break;
							case "integer":
								answers.put(questionnaireResponseItemComponent.getLinkId(),
										((IntegerType) questionnaireResponseItemAnswerComponent.getValue())
												.asStringValue());
								break;
							case "decimal":
								answers.put(questionnaireResponseItemComponent.getLinkId(),
										questionnaireResponseItemAnswerComponent.getValue().toString());
								break;
							case "boolean":
								answers.put(questionnaireResponseItemComponent.getLinkId(),
										((BooleanType) questionnaireResponseItemAnswerComponent.getValue()).getValue()
												? "1"
												: "0");
								break;
							case "Coding":
								answers.put(questionnaireResponseItemComponent.getLinkId(),
										((Coding) questionnaireResponseItemAnswerComponent.getValue()).getCode());
								break;
							default:
								throw new IllegalArgumentException("QuestionnaireResponse answer value type ["
										+ questionnaireResponseItemAnswerComponent.getValue().fhirType()
										+ "] not expected, valids are DECIMAL and STRING.");
							}

						}

					}
					for (int i = 2; i < questionnaireFields.size(); i++) {
						csvRow.add(answers.getOrDefault(questionnaireFields.get(i), ""));
					}

					writer.writeNext(csvRow.toArray(new String[0]));
				}

				writer.close();
				logger.info("Export CSV file generation ended.");
				return byteArrayOutputStream.toByteArray();
			} else {
				return null;
			}
		} catch (

		IOException e) {
			e.printStackTrace();
			// } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
			// e.printStackTrace();
		}
		return null;
	}

}
