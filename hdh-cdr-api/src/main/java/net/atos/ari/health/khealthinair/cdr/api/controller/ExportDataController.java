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

package net.atos.ari.health.khealthinair.cdr.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.atos.ari.health.khealthinair.cdr.api.exception.SubjectNotFoundException;
import net.atos.ari.health.khealthinair.cdr.api.service.PatientService;
import net.atos.ari.health.khealthinair.cdr.api.service.QuestionnaireService;
import net.atos.ari.health.khealthinair.model.enums.QuestionnaireCode;

/**
 * This controller is designed as the main entry point for the data coming from
 * the Hospitals
 */
@RestController
@RequestMapping(value = "/v1/export")
@Tag(name = "Export Data", description = "Export Data API for the K-HEALTHinAIR project.")
@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin"), @SecurityRequirement(name = "apikeyForPilot") })
public class ExportDataController {

	@Autowired
	private PatientService patientService;
	@Autowired
	private QuestionnaireService questionnaireService;

	/**
	 * Get Questionnaires Responses
	 *
	 * @return The tabulated file with the questionnaires responses data
	 */
	@GetMapping(value = "/questionnaire-responses", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE, "text/csv" })
	// @formatter:off
	  @Operation(operationId = "getQuestionnaireResponses", summary = "Get a questionnaire responses",
	      description = "Get the questionnaire responses for all or a single patient for being used by the analysis.<br>"
	          + "Note the following dataset format details:"
	          + "<ul>"
	          + "  <li>**NONE**: *Auxiliary value, do not use as selector for export*</li>"
	          + "  <li>Questionnaire **SOCIODEMOGRAPHIC**: *Socio-demographic data*</li>"
	          + "  <li>Questionnaire **TIMESPENTINTHESCENARIO**: *Time spent in the scenario/location*</li>"
	          + "  <li>Questionnaire **SACQ**: *Self-Administered Comorbidity Questionnaire*</li>"
	          + "  <li>Questionnaire **PROMISGH10**: *Global-10 Questionnaire (Patient-Reported Outcomes Measurement Information System*</li>"
	          + "  <li>Questionnaire **WHODAS12**: *The World Health Organization Disability Assessment Schedule 2.0)*</li>"
	          + "  <li>Questionnaire **WHO5**: *The World Health Organization-Five Well-Being Index*</li>"
	          + "  <li>Questionnaire **PARIS**: *OECD's Patient-Reported Indicator Survey*</li>"
	          + "  <li>Questionnaire **IPCHEM**: *Observational IPCHEM checklist.*</li>"
	          // + "  <li>Questionnaire **FSFOT**: *Forced Spirometry/Forced Oscillation Technique*</li>"
	          + "  <li>Questionnaire **CAT**: *COPD Assessment Test*</li>"
	          + "  <li>Questionnaire **MMRC**: *modified Medical Research Council Dyspnea scale*</li>"
	          + "  <li>Questionnaire **TAI12**: *Test of Adherence to Inhalers 12*</li>"
	          + "  <li>Questionnaire **MINIAQLQ**: *Mini Asthma Quality of Life Questionnaire*</li>"
	          + "  <li>Questionnaire **ACT**: *Asthma Control Test*</li>"
	          + "  <li>Questionnaire **EQ5D5**: *EuroQoL five dimension five level questionnaire*</li>"
	          + "  <li>Questionnaire **PHQ9**: *Patient Mental Health Questionnaire Nine Item score*</li>"
	          // + "  <li>Questionnaire **IAQPERCEPTION**: *Subjetive-IAQ-Perception*</li>"
	          + "  <li>Questionnaire **GERMANYCANTEENSTAFF**: *Germany Canteen Staff*</li>"
	          + "</ul>")
	  // @formatter:on
	public ResponseEntity<byte[]> getQuestionnaireResponses(
			@RequestParam(name = "Questionnaire", required = true, defaultValue = "SOCIODEMOGRAPHIC") QuestionnaireCode questionnaireCode,
			@RequestParam(name = "PatientId", required = false) String patientId) {

		if (questionnaireCode != null && questionnaireCode == QuestionnaireCode.NONE) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Please, specify a valir questionnaire code.");
		}
		if (patientId != null && !patientId.isBlank() && !patientService.existPatientByPatientId(patientId)) {
			throw new SubjectNotFoundException("No patient found with ID [" + patientId + "].");
		}

		byte[] file = questionnaireService.getQuestionnaireResponses(questionnaireCode, patientId);
		if (file != null && file.length > 0) {
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + (StringUtils.hasText(patientId) ? patientId + "-" : "")
									+ questionnaireCode.name() + ".csv\"")
					.contentType(MediaType.parseMediaType("text/csv")).body(file);
		} else {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No results for Get the questionnaire responses");
		}
	}

}
