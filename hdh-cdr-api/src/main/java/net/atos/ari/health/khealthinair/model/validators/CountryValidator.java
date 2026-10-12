package net.atos.ari.health.khealthinair.model.validators;

import java.util.Locale;
import java.util.Locale.IsoCountryCode;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.atos.ari.health.khealthinair.model.annotations.CountryISO3166;

public class CountryValidator implements ConstraintValidator<CountryISO3166, String> {

  /**
   * ISO3166-1 alpha-3 three letter country codes validation
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (Locale.getISOCountries(IsoCountryCode.PART1_ALPHA3).contains(value)) {
      return true;
    } else {
      return false;
    }
  }
}
