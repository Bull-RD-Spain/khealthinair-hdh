package net.atos.ari.health.khealthinair.model.enums;

/**
 * Class for maintaining QuestionnairePart enum.
 *
 */
public enum QuestionnairePart {
	//@formatter:off
	NONE("None"),
	PARTA("Socio-Demographic data"),
	PARTB("Time spent in the Scenario/Location"),
	PARTC("PROMs, ICHOM (International Consortium for Health Outcomes Measurement)"),
	PARTD("PREMs (Patient-Reported Experience Measures, adaptation Of The Paris Survey)"),
	PARTE("Quality Of Life"),
	PARTF("Mental Health"),
	PARTG("Subjective IAQ Perception"),
	EXTRAGERMANY("Germany Canteen Staff");
	//@formatter:on

	private final String description;

	QuestionnairePart(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
