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
