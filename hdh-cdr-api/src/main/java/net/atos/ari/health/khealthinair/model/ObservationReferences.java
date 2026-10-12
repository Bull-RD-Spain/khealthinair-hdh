package net.atos.ari.health.khealthinair.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for maintaining ObservationReferences data.
 *
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ObservationReferences {

  /**
   * The patient id.
   */
  @NotBlank(message = "patientId is mandatory")
  private String patientId;

  /**
   * The date of the Observation.
   */
  @NotNull(message = "Date may not be null")
  private OffsetDateTime date;

  /**
   * The encounter id.
   */
  @JsonProperty(required = false)
  private String encounterId;

  /**
   * The partOfId of the reference in case is related with a Procedure.
   */
  private String partOfId;

  /**
   * The hasMember.
   */
  private String hasMember;
}
