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
