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