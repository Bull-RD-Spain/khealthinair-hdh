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
 * Class for maintaining CodeSystem data.
 *
 */
public class CodeSystem {

  /**
   * K-HEALTHinAIR CodeSystem (used when custom code is necessary)
   */
  public static final String SYSTEM_KHEALTHINAIR = "https://k-healthinair.eu";

  /**
   * UCUM CodeSystem
   */
  public static final String SYSTEM_UCUM = "http://unitsofmeasure.org";

  /**
   * HL7 TERMINOLOGY CodeSystem
   */
  public static final String SYSTEM_HL7_TERMINOLOGY = "http://terminology.hl7.org";

  /**
   * SNOMED CT CodeSystem
   */
  public static final String SYSTEM_SNOMEDCT = "http://snomed.info/sct";

  /**
   * LOINC CodeSystem
   */
  public static final String SYSTEM_LOINC = "http://loinc.org";
}
