package net.atos.ari.health.khealthinair.model.enums;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;

/**
 * Class for maintaining ConditionCode data.
 *
 */
public enum ConditionCode {
	// @formatter:off
    COPD("13645005", "Chronic obstructive pulmonary disease");
    // @formatter:on

	private CodeableConcept codeableConcept;

	private ConditionCode(String code, String display) {
		this.codeableConcept = new CodeableConcept().addCoding(new Coding(CodeSystem.SYSTEM_SNOMEDCT, code, display));
	}

	public CodeableConcept getCodeableConcept() {
		return codeableConcept;
	}

}
