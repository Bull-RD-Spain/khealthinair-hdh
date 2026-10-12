package net.atos.ari.health.khealthinair.cdr.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fhir")
@Validated
public class FhirConfigProperties {

  @NotBlank
  @NotNull
  private String endpoint;

  private String version;

  private String language;

  private System system;

}
