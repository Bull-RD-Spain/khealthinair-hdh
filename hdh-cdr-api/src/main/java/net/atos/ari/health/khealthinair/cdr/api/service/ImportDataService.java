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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.QuestionnaireResponse;
import org.hl7.fhir.r5.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;
import net.atos.ari.health.khealthinair.model.Patient;
import net.atos.ari.health.khealthinair.model.RedCAPRow;
import net.atos.ari.health.khealthinair.model.enums.CodeSystem;
import net.atos.ari.health.khealthinair.model.enums.Pilot;
import net.atos.ari.health.khealthinair.model.enums.QuestionnaireCode;

@Component
public class ImportDataService {

	private static final Logger logger = LoggerFactory.getLogger(ImportDataService.class);

	@Autowired
	private PatientService patientService;
	@Autowired
	private BinaryService binaryService;
	@Autowired
	private ObservationService observationService;
	@Autowired
	private QuestionnaireService questionnaireService;

	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
	SimpleDateFormat sdf_datetime_dmy = new SimpleDateFormat("yyyy-MM-dd H:mm");

	public ImportDataService(@Autowired FhirConfigProperties fhirConfigProperties) {
		sdf_datetime_dmy.setTimeZone(TimeZone.getTimeZone("UTC"));
		logger.debug("New instance of ImportDataService with SYSTEM_KHEALTHINAIR [{}].",
				CodeSystem.SYSTEM_KHEALTHINAIR);
	}

	/**
	 *
	 * @param file
	 */
	public void processREDCapFile(MultipartFile file, Pilot pilot, String description) {
		logger.info("Processing file [{}]...", file.getOriginalFilename());

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(file.getInputStream(), "UTF-8"));) {

			// Removing BOM if exist, since it makes HeaderColumnNameMappingStrategy to fail
			String headerLine = bufferedReader.readLine();
			if (headerLine != null && headerLine.startsWith("\uFEFF")) {
				headerLine = headerLine.replace("\uFEFF", "");
			}
			List<String> allLines = new ArrayList<>();
			allLines.add(headerLine);
			bufferedReader.lines().forEach(allLines::add);
			Reader reader = new StringReader(String.join("\n", allLines));

			HeaderColumnNameMappingStrategy<RedCAPRow> strategy = new HeaderColumnNameMappingStrategy<>();
			strategy.setType(RedCAPRow.class);
			CsvToBean<RedCAPRow> csvToBean = new CsvToBeanBuilder<RedCAPRow>(reader).withType(RedCAPRow.class)
					.withSeparator('|').withSkipLines(0).withMappingStrategy(strategy).withIgnoreLeadingWhiteSpace(true)
					.build();

			String patientId = null;
			List<RedCAPRow> patientBlock = new ArrayList<>();

			// Blocks per Subject, until the row when record_id_atos changes
			int i = 1; // Header are already omitted by HeaderColumnNameMappingStrategy
			int start = 0;
			for (RedCAPRow row : csvToBean) {
				i++;
				// ASSUMPTION !!
				// The first row in the block should contain the "record_id_atos", with the
				// patientId
				if (row.getRecord_id_atos() != null && !row.getRecord_id_atos().isBlank()) {
					if (patientId == null) {
						patientId = row.getRecord_id_atos();
						start = i - 1;
					} else if (!row.getRecord_id_atos().equals(patientId)) {
						logger.debug("Rows block for record_id_atos [{}] range [{}-{}].", patientId, start, i - 1);
						processBlock(patientId, patientBlock);
						patientBlock.clear();
						patientId = row.getRecord_id_atos();
						start = i - 1;
					}
				} else {
					patientBlock.add(row);
				}
			}
			if (!patientBlock.isEmpty()) { // Process last block
				processBlock(patientId, patientBlock);
				patientBlock.clear();
			}

			// Saves a copy of the original file
			binaryService.uploadBinary(reader, file.getOriginalFilename(), pilot, description);

			logger.debug("CSV processing ended with [{}] rows.", i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*-
	 * It process a rows block which are for a specific patient. The logic is the following:
	 * - if it has a 'record_id_atos', should be the project ID-definition, needed for the Patient resource creation,
	 *   this row is only used to identify switch of patient and mark the finish of the block, not using the 'record_id'.
	 * - following rows are different 'instrument'+'repeat_instance', which in turn would contain one or more final questionnaires.
	 *   Note that when several questionnaires in a single instrument, they do not necessarily have to be all completed,
	 *   an extra check is necessary to deduce that the set of columns is provided.
	 *   The key formed by the instrument + instance + questionnaire type identifies a specific response in order to avoid duplicating its intake.
	 *   Since pilots may have included variations in the naming and structure of instruments and questionnaires,
	 *   but should not in terms of field names, these are always assumed to be common.
	 *   Categorizing the questionnaires into "parts" are only for grouping them for subsequent filtering and extraction.
	 *   Keep in mind, the REDCap instrument do not fix the 'part' categorization, but only the questionnaires we can find on it.
	 *
	 * @param recordId
	 * @param bloque
	 */
	private void processBlock(String patientId, List<RedCAPRow> patientBlock) {

		logger.debug("Processsing block for patientId [{}] with [{}] rows of questionnaire responses...", patientId,
				patientBlock.size());

		int i = 0;
		for (RedCAPRow row : patientBlock) {

			i++;
			logger.debug(
					"Processsing row [{}/{}], for patientId [{}], redcap-instrument [{}] and redcap-instance [{}]...",
					i, patientBlock.size(), patientId, row.getRedcap_repeat_instrument(),
					row.getRedcap_repeat_instance());

			if (QuestionnaireCode.SOCIODEMOGRAPHIC.inInstrument(row.getRedcap_repeat_instrument())) {
				if (row.getPart_a_datos_sociodemograficos_complete() == null
						|| !row.getPart_a_datos_sociodemograficos_complete().equals("2")) {
					logger.warn("Note that the Sociodemographic data is not marked as 'completed' for patientId [{}].",
							patientId);
				}

				/*-
				 * The PART A includes:
				 * - Patient creation (or update)
				 * - Educationlevel Observation
				 * - Sociodemographic questionnaire
				 */

				createPatient(patientId, row);

				if (null != row.getEducationlevel_oah() && !row.getEducationlevel_oah().isBlank()) {
					createEducationlevelObservation(patientId, row);
				}

				if (!questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.SOCIODEMOGRAPHIC,
						row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService
							.createResponseSociodemographic(patientId, row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

			} else { // OTHER QuestionnaireCode different from SOCIODEMOGRAPHIC

				if (!patientService.existPatientByPatientId(patientId)) {
					logger.error(
							"patientId [{}] was not created, an SOCIODEMOGRAPHIC is needed in advance, resources skipped.",
							patientId);
					return; // skip all the block if patient not created previously
				}

				/*-
				 * The PART B includes:
				 * - Time spent in the scenario questionnaire
				 */
				// Time spent in the scenario questionnaire
				if (QuestionnaireCode.TIMESPENTINTHESCENARIO.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId,
								QuestionnaireCode.TIMESPENTINTHESCENARIO, row.getRedcap_repeat_instrument(),
								row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse response = questionnaireService
							.createResponseTimeSpentInTheScenario(patientId, row);
					questionnaireService.saveQuestionnaireResponse(response);

				}

				/*-
				 * The PART C, ICHOM (International Consortium for Health Outcomes Measurement), that in turn is composed, in this approach, by the following:
				 * - SACQ (Self-Administered Comorbidity Questionnaire
				 * - PROMIS Global-10 Questionnaire (Patient-Reported Outcomes Measurement Information System)
				 * - WHODAS-12 (The World Health Organization Disability Assessment Schedule 2.0)
				 * - WHO-5 (The World Health Organization-Five Well-Being Index)
				 */

				// SACQ (Self-Administered Comorbidity Questionnaire)
				if (QuestionnaireCode.SACQ.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.SACQ,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseSACQ(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);

				}

				// PROMIS Global-10 Questionnaire (Patient-Reported Outcomes Measurement
				// Information System)
				if (QuestionnaireCode.PROMISGH10.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.PROMISGH10,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponsePROMIS(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// WHODAS-12 (The World Health Organization Disability Assessment Schedule 2.0)
				if (QuestionnaireCode.WHODAS12.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.WHODAS12,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseWHODAS12(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// WHO-5 (The World Health Organization-Five Well-Being Index)
				if (QuestionnaireCode.WHO5.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.WHO5,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseWHO5(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				/*-
				 * The PART D. PREMs (Patient-Reported Experience Measures, adaptation Of The Paris Survey)
				 * - PaRIS (Patient-Reported Indicator Surveys)
				 * - Observational IPCHEM checklist
				 * - FS/FOT (Forced Spirometry/Forced Oscillation Technique)
				 * - CAT (COPD Assessment Test)
				 * - mMRC (modified Medical Research Council Dyspnea scale)
				 * - TAI-12 (Test of Adherence to Inhalers 12)
				 * - MiniAQLQ (Mini Asthma Quality of Life Questionnaire)
				 * - ACT (Asthma Control Test)
				 * - SNOT-22 (Sino-Nasal Outcome Test)
				 */

				// PaRIS (Patient-Reported Indicator Surveys)
				if (QuestionnaireCode.PARIS.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.PARIS,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponsePaRIS(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// Observational IPCHEM checklist
				if (QuestionnaireCode.OBSERVATIONALIPCHEM.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId,
								QuestionnaireCode.OBSERVATIONALIPCHEM, row.getRedcap_repeat_instrument(),
								row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService
							.createResponseObservationalIPCHEM(patientId, row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

//				// FS/FOT (Forced Spirometry/Forced Oscillation Technique)
//				if (QuestionnaireCode.FSFOT.inInstrument(row.getRedcap_repeat_instrument())
//						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.FSFOT,
//								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
//					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseFSFOT(patientId,
//							row);
//					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
//				}

				// CAT (COPD Assessment Test)
				if (QuestionnaireCode.CAT.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.CAT,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseCAT(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// mMRC (modified Medical Research Council Dyspnea scale)
				if (QuestionnaireCode.MMRC.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.MMRC,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseMMRC(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// TAI-12 (Test of Adherence to Inhalers 12)
				if (QuestionnaireCode.TAI12.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.TAI12,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseTAI12(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// MiniAQLQ (Mini Asthma Quality of Life Questionnaire)
				if (QuestionnaireCode.MINIAQLQ.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.MINIAQLQ,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseMiniAQLQ(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// ACT (Asthma Control Test)
				if (QuestionnaireCode.ACT.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.ACT,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseACT(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				// SNOT-22 (Sino-Nasal Outcome Test)
				if (QuestionnaireCode.SNOT22.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.SNOT22,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseSNOT22(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				/*-
				 * The PART E. Quality of Life
				 */
				// EQ-5D-5L (5-level EQ-5D Questionnaire)
				if (QuestionnaireCode.EQ5D5L.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.EQ5D5L,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseEQ5D5L(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}
				// EQ-5D-Y-5L (5-level EQ-5D Youth Questionnaire)
				if (QuestionnaireCode.EQ5DY5L.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.EQ5DY5L,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponseEQ5DY5L(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}

				/*-
				 * The PART F. Mental Health
				 */
				// PHQ-9 (Patient Health Questionnaire)
				if (QuestionnaireCode.PHQ9.inInstrument(row.getRedcap_repeat_instrument())
						&& !questionnaireService.existQuestionnairesResponses(patientId, QuestionnaireCode.PHQ9,
								row.getRedcap_repeat_instrument(), row.getRedcap_repeat_instance().toString())) {
					QuestionnaireResponse questionnaireResponse = questionnaireService.createResponsePHQ9(patientId,
							row);
					questionnaireService.saveQuestionnaireResponse(questionnaireResponse);
				}
			}
		}
	}

	/**
	 *
	 * @param patientId
	 * @param row
	 * @return
	 */
	private String createEducationlevelObservation(String patientId, RedCAPRow row) {

		Observation educationlevel = new Observation();
		educationlevel.setSubject(new Reference("Patient/" + patientId));
		try {
			educationlevel.setEffective(new DateTimeType(sdf_datetime_dmy.parse(row.getDate_part_a()),
					TemporalPrecisionEnum.DAY, TimeZone.getTimeZone("UTC")));
		} catch (ParseException e) {
			logger.error("Cannot parse Part_a_datos_sociodemograficos_timestamp date.");
			e.printStackTrace();
		}
		educationlevel.addCategory(new CodeableConcept()
				.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
						.setCode("social-history").setDisplay("Social History")));

		switch (row.getEducationlevel_oah()) {
		case "0":
			educationlevel
					.setCode(new CodeableConcept().addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("None")));
			break;
		case "1":
			educationlevel
					.setCode(new CodeableConcept().addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Preschool Education")));
			break;
		case "2":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Primary Education (6-12 years)"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("ELEM").setDisplay("Elementary School")));
			break;
		case "3":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Secondary Education (12-16 years)"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("SEC").setDisplay("Some secondary or high school education")));
			break;
		case "4":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Upper secondary Education (16-18 years)"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("HS").setDisplay("High School or secondary school degree complete")));
			break;
		case "5":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Short Cycle Tertiary Education"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("ASSOC").setDisplay("Associate's or technical degree complete")));
			break;
		case "6":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Bachelor’s or equivalent level"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("BD").setDisplay("College or baccalaureate degree complete")));
			break;
		case "7":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Master’s or equivalent level"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("GD").setDisplay("Graduate or professional Degree complete")));
			break;
		case "8":
			educationlevel.setCode(new CodeableConcept()
					.addCoding(new Coding().setSystem(CodeSystem.SYSTEM_KHEALTHINAIR)
							.setCode(row.getEducationlevel_oah()).setDisplay("Doctorate or Equivalent Studies"))
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v3-EducationLevel")
							.setCode("POSTG").setDisplay("Doctoral or post graduate education")));
			break;
		default:
			logger.error("Not recogniced value for educationlevel_oah [{}].", row.getEducationlevel_oah());
		}

		return observationService.updateObservation(educationlevel);
	}

	/**
	 *
	 * @param patientId
	 * @param row
	 * @return
	 */
	private void createPatient(String patientId, RedCAPRow row) {
		Patient khealthinairPatient = new Patient();
		khealthinairPatient.setPatientId(patientId);

		if (patientService.existPatientByPatientId(patientId)) {
			khealthinairPatient = patientService.getKhealthinairPatientByPatientId(patientId);
			logger.debug("Patient [{}] already exists, compleating as much as possible.", patientId);
		}

		if (row.getYearofbirth() != null && !row.getYearofbirth().isEmpty()) {
			khealthinairPatient.setYearOfBirth(LocalDate.parse(row.getYearofbirth()));
		}
		if (row.getCountryofbirth() != null && !row.getCountryofbirth().isEmpty()) {
			khealthinairPatient.setCountryOfBirth(row.getCountryofbirth());
		}
		if (row.getCountryofbirth2() != null && !row.getCountryofbirth2().isEmpty()) {
			khealthinairPatient.setCountryOfBirth2(row.getCountryofbirth2());
		}
		if (row.getSex() != null && !row.getSex().isEmpty()) {
			khealthinairPatient.setSex(row.getSex());
		}
		if (row.getRelationshipstatus() != null && !row.getRelationshipstatus().isEmpty()) {
			khealthinairPatient.setRelationshipstatus(row.getRelationshipstatus());
		}

		patientService.updatePatient(khealthinairPatient);
	}
}
