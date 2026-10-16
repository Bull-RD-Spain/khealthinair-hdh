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

package net.atos.ari.health.khealthinair.model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.ari.health.khealthinair.model.annotations.CountryISO3166;

/**
 * Class for maintaining Patient data in the context of K-HEALTHinAIR reference
 * data model
 *
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "patientId", "yearofbirth", "countryofBirth", "countryofBirth2", "sex", "relationshipstatus" })
public class Patient {

	/**
	 * The patient id.
	 */
	@JsonProperty(required = true)
	private String patientId;

	/**
	 * The patient Year Of Birth.
	 */
	@JsonProperty(required = false, value = "yearofbirth")
	private LocalDate yearOfBirth;

	/**
	 * The patient Country of Birth.
	 */
	@JsonProperty(required = false, value = "countryofBirth")
	@CountryISO3166
	private String countryOfBirth;

	/**
	 * The patient Country of Birth 2.
	 */
	@JsonProperty(required = false, value = "countryofBirth2")
	@CountryISO3166
	private String countryOfBirth2;

	/**
	 * The patient Sex.
	 */
	@JsonProperty(required = true, value = "sex")
	@NotBlank(message = "Sex is mandatory")
	private String sex;

	/**
	 * The patient relationshipstatus.
	 */
	@JsonProperty(required = false, value = "relationshipstatus")
	private String relationshipstatus;

	/**
	 * Compares this Patient object with another object for equality. It checks
	 *
	 * @param other
	 * @return
	 */
	public boolean equal(Patient other) {
		if (this == other) {
			return true;
		}
		if (other == null || this.getClass() != other.getClass()) {
			return false;
		}
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object thisValue = field.get(this);
				Object otherValue = field.get(other);
				if (!Objects.equals(thisValue, otherValue)) {
					return false;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
