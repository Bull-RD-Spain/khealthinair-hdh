package net.atos.ari.health.khealthinair.model.enums;

/**
 * Class for maintaining Pilot data.
 *
 */
public enum Pilot {
	//@formatter:off
	AT("Austria"),
	DE("Germany"),
	ES("Spain"),
	NL("Netherlands"),
	NO("Norway"),
	PL("Poland");
	//@formatter:on

	private final String name;

	Pilot(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}