package net.atos.ari.health.khealthinair.model.enums;

import java.util.Arrays;
import java.util.Optional;

import org.hl7.fhir.r5.model.Enumerations;

public enum ResourceTypeSubset {
	//@formatter:off
	NULL(Enumerations.ResourceTypeEnum.NULL),
	//BUNDLE(Enumerations.ResourceTypeEnum.BUNDLE),
	BINARY(Enumerations.ResourceTypeEnum.BINARY),
	// NAMINGSYSTEM(Enumerations.ResourceTypeEnum.NAMINGSYSTEM),
	// STRUCTUREDEFINITION(Enumerations.ResourceTypeEnum.STRUCTUREDEFINITION),
	VALUESET(Enumerations.ResourceTypeEnum.VALUESET),
	CODESYSTEM(Enumerations.ResourceTypeEnum.CODESYSTEM),
	// SUBSCRIPTION(Enumerations.ResourceTypeEnum.SUBSCRIPTION),
	OBSERVATION(Enumerations.ResourceTypeEnum.OBSERVATION),
	CONDITION(Enumerations.ResourceTypeEnum.CONDITION),
	PATIENT(Enumerations.ResourceTypeEnum.PATIENT),
	// ENCOUNTER(Enumerations.ResourceTypeEnum.ENCOUNTER),
	QUESTIONNAIRE(Enumerations.ResourceTypeEnum.QUESTIONNAIRE),
	QUESTIONNAIRERESPONSE(Enumerations.ResourceTypeEnum.QUESTIONNAIRERESPONSE);
	//@formatter:on

	private final Enumerations.ResourceTypeEnum original;

	ResourceTypeSubset(Enumerations.ResourceTypeEnum original) {
		this.original = original;
	}

	public Enumerations.ResourceTypeEnum toOriginal() {
		return original;
	}

	public static Optional<ResourceTypeSubset> fromOriginal(Enumerations.ResourceTypeEnum type) {
		return Arrays.stream(values()).filter(e -> e.original == type).findFirst();
	}
}
