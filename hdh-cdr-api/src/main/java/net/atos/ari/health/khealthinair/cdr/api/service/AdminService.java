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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r5.model.BooleanType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.CapabilityStatement;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.IntegerType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Parameters;
import org.hl7.fhir.r5.model.Questionnaire;
import org.hl7.fhir.r5.model.QuestionnaireResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.api.SummaryEnum;
import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;
import net.atos.ari.health.khealthinair.cdr.api.repository.RepositoryConnector;
import net.atos.ari.health.khealthinair.model.enums.CodeSystem;
import net.atos.ari.health.khealthinair.model.enums.ResourceTypeSubset;

@Component
public class AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

	private static final int EXPUNGE_LIMIT = 1000;

	@Autowired
	private RepositoryConnector repositoryConnector;

	public AdminService(@Autowired FhirConfigProperties fhirConfigProperties) {

		logger.debug("New instance of AdminService with CodeSystem.SYSTEM_KHEALTHINAIR:[{}].",
				CodeSystem.SYSTEM_KHEALTHINAIR);
	}

	/**
	 * ExpungeEverything will delete all resources by using $expunge at
	 * System-Level, this includes deleted and history. Exposing this is considered
	 * risky, and needs to be activated a proper flag in the HAPI-FHIR config. You
	 * can check is real by monitoring SELECT * FROM le_hapi.hfj_res_ver;
	 *
	 * @return
	 */
	public String expungeEverything() {
		logger.info("ExpungeEverything operation...");

		Parameters parameters = new Parameters();
		parameters.addParameter().setName("limit").setValue(new IntegerType(EXPUNGE_LIMIT));
		parameters.addParameter().setName("expungeDeletedResources").setValue(new BooleanType(true));
		parameters.addParameter().setName("expungePreviousVersions").setValue(new BooleanType(true));
		parameters.addParameter().setName("expungeEverything").setValue(new BooleanType(true));

		int count = EXPUNGE_LIMIT;
		do {
		// @formatter:off
      Parameters result = repositoryConnector.client()
          .operation()
          .onServer()
          .named("expunge")
          .withParameters(parameters)
          .cacheControl(CacheControlDirective.noCache())
          .execute();
      // @formatter:on
			count = ((IntegerType) result.getParameter("count").getValue()).getValue();
			logger.info("ExpungeEverything operation execution, count: [" + count + "]");
		} while (count == EXPUNGE_LIMIT);

		return "ExpungeEverything operation finish " + countResources(null);
	}

	/**
	 * expungeDeletedResourcesAndPreviousVersions will delete deleted resources and
	 * history by using $expunge at System-Level. This allows to free up space in
	 * the database, have more realistic counters and get rid of unnecessary
	 * obsolete content.
	 *
	 * @return
	 */
	public String expungeDeletedResourcesAndPreviousVersions() {
		logger.info("ExpungeDeletedResourcesAndPreviousVersions operation...");

		Parameters parameters = new Parameters();
		parameters.addParameter().setName("limit").setValue(new IntegerType(EXPUNGE_LIMIT));
		parameters.addParameter().setName("expungeDeletedResources").setValue(new BooleanType(true));
		parameters.addParameter().setName("expungePreviousVersions").setValue(new BooleanType(true));
		parameters.addParameter().setName("expungeEverything").setValue(new BooleanType(false));

		int count = EXPUNGE_LIMIT;
		do {
		// @formatter:off
      Parameters result = repositoryConnector.client()
          .operation()
          .onServer()
          .named("expunge")
          .withParameters(parameters)
          .cacheControl(CacheControlDirective.noCache())
          .execute();
      // @formatter:on
			count = ((IntegerType) result.getParameter("count").getValue()).getValue();
			logger.debug("ExpungeDeletedResourcesAndPreviousVersions operation execution, count: [" + count + "]");
		} while (count == EXPUNGE_LIMIT);

		return "ExpungeDeletedResourcesAndPreviousVersions operation finish.";
	}

	/**
	 * expungeQuestionnaires will delete all QuestionnaireResponse and Questionnaire
	 * resources by using $expunge at Type-Level
	 *
	 * @return
	 */
	public String expungeQuestionnaires() {
		/*--
		 * The client should get requests with this form:
		 * POST [base]/QuestionnaireResponse/$expunge (Content-Type: application/fhir+json)
		 * POST [base]/Questionnaire/$expunge         (Content-Type: application/fhir+json)
		 *
		 * { "resourceType": "Parameters",
		 *   "parameter": [
		 *     {"name":"limit","valueInteger": EXPUNGE_LIMIT},
		 *     {"name": "expungeDeletedResources","valueBoolean": true},
		 *     {"name": "expungePreviousVersions","valueBoolean": true}
		 *   ]
		 * }
		 */
	// @formatter:off
    //  Parameters parameters = new Parameters();
    //  parameters.addParameter().setName("limit").setValue(new IntegerType(EXPUNGE_LIMIT));
    //  parameters.addParameter().setName("expungeDeletedResources").setValue(new BooleanType(false));
    //  parameters.addParameter().setName("expungePreviousVersions").setValue(new BooleanType(true));
    //  parameters.addParameter().setName("expungeEverything").setValue(new BooleanType(false));
    //    Parameters result = repositoryConnector.client()
    //        .operation()
    //        .onType(org.hl7.fhir.r5.model.QuestionnaireResponse.class)
    //        .named("expunge")
    //        .withParameters(parameters)
    //        .cacheControl(CacheControlDirective.noCache())
    //        .execute();
    //    logger.info("expungeQuestionnairesResponse operation, count: ["
    //        + result.getParameter("count").getValue().primitiveValue() + "]");
    //
    //    result = repositoryConnector.client()
    //        .operation()
    //        .onType(org.hl7.fhir.r5.model.Questionnaire.class)
    //        .named("expunge")
    //        .withParameters(parameters)
    //        .cacheControl(CacheControlDirective.noCache())
    //        .execute();
    //    logger.info("expungeQuestionnaires operation, count: ["
    //        + result.getParameter("count").getValue().primitiveValue() + "]");
    // @formatter:on
		// The previous Operations are not implemented in the HAPI FHIR version used
		// 6.6.0
		// so manual one-by-one deletion needs to be performed.
		// QuestionnaireResponse.INCLUDE_QUESTIONNAIRE not valid since relation is now
		// CanonicalType
		// neither CASCADE in the deletion method from Questionnaires

		// TODO: This section could be removed in the future
		// DELETE QuestionnairesResponses --------------------------------

	// @formatter:off
    Bundle questionnaireResponseResult = repositoryConnector.client()
        .search()
        .forResource(QuestionnaireResponse.class)
        .elementsSubset("QuestionnaireResponse.id")
        .returnBundle(Bundle.class)
        .cacheControl(CacheControlDirective.noCache())
        .execute();
    // @formatter:on

		List<String> questionnaireResponseIds = new ArrayList<>();
		if (questionnaireResponseResult.getEntry().size() > 0) {
			do {
				for (BundleEntryComponent questionnaireResponse : questionnaireResponseResult.getEntry()) {
					if (questionnaireResponse.getResource() instanceof QuestionnaireResponse) {
						questionnaireResponseIds.add(questionnaireResponse.getResource().getIdElement().getIdPart());
					}
				}
				if (questionnaireResponseResult.getLink(IBaseBundle.LINK_NEXT) != null) {
					questionnaireResponseResult = repositoryConnector.client().loadPage()
							.next(questionnaireResponseResult).execute();
				} else {
					break;
				}
			} while (true);

			logger.info("Deleting [{}] QuestionnaireResponses...", questionnaireResponseIds.size());
			int i = 0;
			for (String questionnairResponseId : questionnaireResponseIds) {
				i++;
		// @formatter:off
        MethodOutcome methodOutcome =  repositoryConnector.client()
          .delete()
          .resourceById("QuestionnaireResponse", questionnairResponseId)
          .cacheControl(CacheControlDirective.noCache())
          .execute();
        // @formatter:on
				logger.debug("{}/{} QuestionnaireResponse [{}] delete result: {}", i, questionnaireResponseIds.size(),
						questionnairResponseId,
						((OperationOutcome) methodOutcome.getOperationOutcome()).getIssue().get(0).getDiagnostics());
			}
		} else {
			logger.debug("No QuestionnaireResponse for deletion");
		}

		// DELETE Questionnaires --------------------------------

	// @formatter:off
    Bundle questionnaireResult = repositoryConnector.client()
        .search()
        .forResource(Questionnaire.class)
        .elementsSubset("Questionnaire.id")
        .returnBundle(Bundle.class)
        .cacheControl(CacheControlDirective.noCache())
        .execute();
    // @formatter:on

		List<String> questionnaireIds = new ArrayList<>();
		if (questionnaireResult.getEntry().size() > 0) {
			do {
				for (BundleEntryComponent questionnaire : questionnaireResult.getEntry()) {
					if (questionnaire.getResource() instanceof Questionnaire) {
						questionnaireIds.add(questionnaire.getResource().getIdElement().getIdPart());
					}
				}
				if (questionnaireResult.getLink(IBaseBundle.LINK_NEXT) != null) {
					questionnaireResult = repositoryConnector.client().loadPage().next(questionnaireResult).execute();
				} else {
					break;
				}
			} while (true);

			logger.info("Deleting [{}] Questionnaires...", questionnaireIds.size());
			int i = 0;
			for (String questionnaireId : questionnaireIds) {
				i++;
		// @formatter:off
        MethodOutcome methodOutcome =  repositoryConnector.client()
          .delete()
          .resourceById("Questionnaire", questionnaireId)
          .cacheControl(CacheControlDirective.noCache())
          .execute();
        // @formatter:on
				logger.debug("{}/{} Questionnaire [{}] delete result: {}", i, questionnaireIds.size(), questionnaireId,
						((OperationOutcome) methodOutcome.getOperationOutcome()).getIssue().get(0).getDiagnostics());
			}
		} else {
			logger.debug("No Questionnaires for deletion");
		}

		expungeDeletedResourcesAndPreviousVersions();

		return "expungeQuestionnaires (Questionnaires + QuestionnairesResponses) operation finish. "
				+ countResources(null);
	}

	/**
	 * expungeQuestionnairesesponses will delete all QuestionnaireResponse resources
	 * by using $expunge at Type-Level
	 *
	 * @return
	 */
	public String expungeQuestionnairesResponses() {

	// @formatter:off
    Bundle questionnaireResponseResult = repositoryConnector.client()
        .search()
        .forResource(QuestionnaireResponse.class)
        .elementsSubset("QuestionnaireResponse.id")
        .returnBundle(Bundle.class)
        .cacheControl(CacheControlDirective.noCache())
        .execute();
    // @formatter:on

		List<String> questionnaireResponseIds = new ArrayList<>();
		if (questionnaireResponseResult.getEntry().size() > 0) {
			do {
				for (BundleEntryComponent questionnaireResponse : questionnaireResponseResult.getEntry()) {
					if (questionnaireResponse.getResource() instanceof QuestionnaireResponse) {
						questionnaireResponseIds.add(questionnaireResponse.getResource().getIdElement().getIdPart());
					}
				}
				if (questionnaireResponseResult.getLink(IBaseBundle.LINK_NEXT) != null) {
					questionnaireResponseResult = repositoryConnector.client().loadPage()
							.next(questionnaireResponseResult).execute();
				} else {
					break;
				}
			} while (true);

			logger.info("Deleting [{}] QuestionnaireResponses...", questionnaireResponseIds.size());
			int i = 0;
			for (String questionnairResponseId : questionnaireResponseIds) {
				i++;
		// @formatter:off
        MethodOutcome methodOutcome =  repositoryConnector.client()
          .delete()
          .resourceById("QuestionnaireResponse", questionnairResponseId)
          .cacheControl(CacheControlDirective.noCache())
          .execute();
        // @formatter:on
				logger.debug("{}/{} QuestionnaireResponse [{}] delete result: {}", i, questionnaireResponseIds.size(),
						questionnairResponseId,
						((OperationOutcome) methodOutcome.getOperationOutcome()).getIssue().get(0).getDiagnostics());
			}
		} else {
			logger.debug("No QuestionnaireResponse for deletion");
		}

		expungeDeletedResourcesAndPreviousVersions();

		return "expungeQuestionnairesResponses operation finish. " + countResources(null);
	}

	/**
	 * Count Resources method provides information about status of the database
	 *
	 * @param resourceType
	 */
	public String countResources(final ResourceTypeSubset resourceType) {

		String returnMessage = "Resources count:\n";
		Bundle resourceTypeSearch = null;
		if (null != resourceType && !resourceType.toOriginal().equals(Enumerations.ResourceTypeEnum.NULL)) {
			// @formatter:off
	        resourceTypeSearch = repositoryConnector.client()
		        .search().forResource(resourceType.toOriginal().toCode())
		        .encodedJson()
		        .summaryMode(SummaryEnum.COUNT) // Search only: just return a count of the matching resources, without returning the actual matches
		        .returnBundle(Bundle.class)
		        .cacheControl(new CacheControlDirective().setNoCache(true))
		        .execute();
	        // @formatter:on
			returnMessage += "- Count " + resourceType + ": " + resourceTypeSearch.getTotal() + " \n";

		} else {
			List<String> resourceTypes = metadata();
			// Resources that returns HTTP 400 Bad Request when calling with _summary=count
			resourceTypes.removeAll(List.of("OperationDefinition", "SearchParameter", "Group", "Subscription"));
			Iterator<String> resourceTypesIterator = resourceTypes.iterator();
			String currentResourceType = null;
			while (resourceTypesIterator.hasNext()) {
				currentResourceType = resourceTypesIterator.next();
				// @formatter:off
		        resourceTypeSearch = repositoryConnector.client()
		            .search()
		            .forResource(currentResourceType)
		            .encodedJson()
		            .summaryMode(SummaryEnum.COUNT) // Search only: just return a count of the matching resources, without returning the actual matches
		            .returnBundle(Bundle.class)
		            .usingStyle(SearchStyleEnum.GET)
		            .cacheControl(CacheControlDirective.noCache())
		            .execute();
		        // @formatter:on
				returnMessage += "- Count " + currentResourceType + ": " + resourceTypeSearch.getTotal() + " \n";
			}
		}
		logger.debug(returnMessage + "counting resources ended.");
		return returnMessage;
	}

	/**
	 * Capability Statement (metadata) This is used to get the list of configured
	 * supported_resource_types
	 *
	 * @param resourceType
	 */
	public List<String> metadata() {
		// @formatter:off
	    CapabilityStatement capabilityStatement =
	        repositoryConnector.client()
	        .capabilities()
	        .ofType(CapabilityStatement.class)
	        .cacheControl(CacheControlDirective.noCache())
	        .execute();
	    // @formatter:on
		List<String> listOfTypes = new ArrayList<>();
		for (int i = 0; i < capabilityStatement.getRest().get(0).getResource().size(); i++) {
			listOfTypes.add(capabilityStatement.getRest().get(0).getResource().get(i).getType().toString());
		}
		return listOfTypes;
	}
}
