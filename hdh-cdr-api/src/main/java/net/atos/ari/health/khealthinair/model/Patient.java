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
