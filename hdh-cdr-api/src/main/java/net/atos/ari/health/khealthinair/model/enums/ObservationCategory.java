package net.atos.ari.health.khealthinair.model.enums;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;

/**
 * Class maintaining Observations Categories from
 * https://hl7.org/fhir/R5/valueset-observation-category.html
 *
 */
public enum ObservationCategory {
	// @formatter:off
	SOCIALHISTORY("social-history","http://terminology.hl7.org/CodeSystem/observation-category","Social History"),
    VITALSIGNS("vitalsigns","http://terminology.hl7.org/CodeSystem/observation-category","Vital Signs"),
    IMAGING("imaging","http://terminology.hl7.org/CodeSystem/observation-category","Imaging"),
    LABORATORY("laboratory","http://terminology.hl7.org/CodeSystem/observation-category","Laboratory"),
    PROCEDURE("procedure","http://terminology.hl7.org/CodeSystem/observation-category","Procedure"),
    SURVEY("survey","http://terminology.hl7.org/CodeSystem/observation-category","Survey"),
    EXAM("exam","http://terminology.hl7.org/CodeSystem/observation-category","Exam"),
    THERAPY("therapy","http://terminology.hl7.org/CodeSystem/observation-category","Therapy"),
    ACTIVITY("activity","http://terminology.hl7.org/CodeSystem/observation-category","Activity");
	// @formatter:on

	private CodeableConcept codeableConcept;

	private ObservationCategory(String code, String system, String display) {
		this.codeableConcept = new CodeableConcept().addCoding(new Coding(system, code, display));
	}

	public CodeableConcept getCodeableConcept() {
		return codeableConcept;
	}

	public String toCode() {
		return codeableConcept.getCodingFirstRep().getCode();
	}
	public String getDisplay() {
		return codeableConcept.getCodingFirstRep().getDisplay();
	}
	public String getSystem() {
		return codeableConcept.getCodingFirstRep().getSystem();
	}
}