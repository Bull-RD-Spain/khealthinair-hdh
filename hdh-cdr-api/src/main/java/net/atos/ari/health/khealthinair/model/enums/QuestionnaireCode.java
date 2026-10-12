package net.atos.ari.health.khealthinair.model.enums;

import java.util.Arrays;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;

/**
 * Class for maintaining ConditionCode data.
 *
 */
public enum QuestionnaireCode {
	//@formatter:off
	NONE(QuestionnairePart.NONE, "NONE", CodeSystem.SYSTEM_KHEALTHINAIR, ""),

	// PART A
	SOCIODEMOGRAPHIC(QuestionnairePart.PARTA, "SOCIODEMOGRAPHIC", CodeSystem.SYSTEM_KHEALTHINAIR, "Socio-demographic data",
		"part_a_datos_sociodemograficos"),

	// PART B
	TIMESPENTINTHESCENARIO(QuestionnairePart.PARTB, "TIMESPENTINTHESCENARIO", CodeSystem.SYSTEM_KHEALTHINAIR, "Time spent in the scenario/location",
		"part_b", "Part B + eq5d5y + phq9 adapted"),

	// PART C PROMS (Patient Reported Outcome Measures)
	SACQ(QuestionnairePart.PARTC, "SACQ", CodeSystem.SYSTEM_KHEALTHINAIR, "Self-Administered Comorbidity Questionnaire",
		"part_c_ichom"),
	PROMISGH10(QuestionnairePart.PARTC, "85524-7", CodeSystem.SYSTEM_LOINC, "Global-10 Questionnaire (Patient-Reported Outcomes Measurement Information System",
		"part_c_ichom"),
	WHODAS12(QuestionnairePart.PARTC, "71930-0", CodeSystem.SYSTEM_LOINC, "The World Health Organization Disability Assessment Schedule 2.0)",
		"part_c_ichom"),
	WHO5(QuestionnairePart.PARTC, "72109-2", CodeSystem.SYSTEM_LOINC, "The World Health Organization-Five Well-Being Index",
		"part_c_ichom"),

	// PART D PREMS (Patient-Reported Experience Measures)
	PARIS(QuestionnairePart.PARTD, "PaRIS", CodeSystem.SYSTEM_KHEALTHINAIR, "OECD’s Patient-Reported Indicator Surveys",
		"part_d_paris_survey_prems"),
	OBSERVATIONALIPCHEM(QuestionnairePart.PARTD, "OBSERVATIONALIPCHEM", CodeSystem.SYSTEM_KHEALTHINAIR, "OBSERVATIONALIPCHEM",
		"part_d_paris_survey_prems"),
//	FSFOT(QuestionnairePart.PARTD, "19868-9", CodeSystem.SYSTEM_LOINC,"Forced Spirometry/Forced Oscillation Technique",
//		"fsfot"),
	CAT(QuestionnairePart.PARTD, "446660005", CodeSystem.SYSTEM_SNOMEDCT, "COPD Assessment Test",
		 "copd_assessment_test_cat"),
	MMRC(QuestionnairePart.PARTD, "mMRC", CodeSystem.SYSTEM_KHEALTHINAIR, "modified Medical Research Council Dyspnea scale",
		"mmrc"),
	TAI12(QuestionnairePart.PARTD, "TAI-12", CodeSystem.SYSTEM_KHEALTHINAIR, "Test of Adherence to Inhalers 12",
		"tai12"),
	MINIAQLQ(QuestionnairePart.PARTD, "763695004", CodeSystem.SYSTEM_SNOMEDCT, "Mini Asthma Quality of Life Questionnaire",
		"miniaqlq"),
	ACT(QuestionnairePart.PARTD, "82674-3", CodeSystem.SYSTEM_LOINC, "Asthma Control Test",
		"asthma_control_test_act"),
	SNOT22(QuestionnairePart.PARTD, "SNOT-22", CodeSystem.SYSTEM_KHEALTHINAIR, "Sino-Nasal Outcome Test",
		"snot22"),

	// PART E Quality Of Life (EQ5D5L/EQ5DY5L)
	EQ5D5L(QuestionnairePart.PARTE, "73041000052103", CodeSystem.SYSTEM_SNOMEDCT, "EuroQoL five dimension five level questionnaire (assessment scale)",
			""),
	EQ5DY5L(QuestionnairePart.PARTE, "770633007", CodeSystem.SYSTEM_SNOMEDCT, "EuroQol Five Dimension (Youth) instrument (assessment scale)",
			""),

	// PART F Mental Health (PHQ9/PHQ8)
	PHQ9(QuestionnairePart.PARTF, "720433000", CodeSystem.SYSTEM_SNOMEDCT, "Patient Mental Health Questionnaire Nine Item score",
		""),

//	// PART G Subjective IAQ Perception
//	IAQPERCEPTION(QuestionnairePart.PARTG, "Subjetive-IAQ-Perception", CodeSystem.SYSTEM_KHEALTHINAIR, "Subjetive-IAQ-Perception",
//		""),

	// Extra
	GERMANYCANTEENSTAFF(QuestionnairePart.EXTRAGERMANY, "Germany Canteen Staff", CodeSystem.SYSTEM_KHEALTHINAIR, "Germany Canteen Staff",
		"");

	//@formatter:on

	private QuestionnairePart questionnairePart;
	private CodeableConcept codeableConcept;
	String[] instruments;

	private QuestionnaireCode(QuestionnairePart questionnairePart, String code, String system, String display,
			String... instruments) {
		this.questionnairePart = questionnairePart;
		this.codeableConcept = new CodeableConcept().addCoding(new Coding(CodeSystem.SYSTEM_SNOMEDCT, code, display));
		this.instruments = instruments;
	}

	public QuestionnairePart getQuestionnairePart() {
		return questionnairePart;
	}

	public CodeableConcept getCodeableConcept() {
		return codeableConcept;
	}

	public String[] getinstruments() {
		return instruments;
	}

	public static boolean contains(String code) {
		if (code == null)
			return false;
		try {
			QuestionnaireCode.valueOf(code.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Returns true id the Questionnaire could exist in such instrument
	 *
	 * @param instrument
	 * @return
	 */
	public boolean inInstrument(String instrument) {
		if (instrument == null) {
			return false;
		} else {
			return Arrays.stream(this.instruments).anyMatch(instrumentList -> instrumentList.equals(instrument));
		}
	}

	public static QuestionnaireCode fromInstrument(String input) {
		if (input == null)
			return null;
		String normalized = input.trim().toLowerCase();
		QuestionnaireCode match = Arrays.stream(QuestionnaireCode.values()).filter(questionnaireCode -> Arrays
				.stream(questionnaireCode.instruments).anyMatch(instrument -> instrument.equalsIgnoreCase(normalized)))
				.findFirst().orElse(null);
//		logger.debug("Instrument for alias [{}] matched as Part [{}]", input, match.name());
		return match;
	}

}
