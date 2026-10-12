package net.atos.ari.health.khealthinair.cdr.api;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import net.atos.ari.health.khealthinair.cdr.api.config.FhirConfigProperties;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@EnableConfigurationProperties
public class ClinicalDataRepositoryApiApplication {

  private static final Logger logger =
      LoggerFactory.getLogger(ClinicalDataRepositoryApiApplication.class);

  public ClinicalDataRepositoryApiApplication(
      @Autowired FhirConfigProperties fhirConfigProperties) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    logger.info(
        "ClinicalDataRepositoryApiApplication CDR-API\n"
            + "  _   _ ____  _   _     ____ ____  ____           _    ____ ___ \n"
            + " | | | |  _ \\| | | |   / ___|  _ \\|  _ \\         / \\  |  _ \\_ _|\n"
            + " | |_| | | | | |_| |  | |   | | | | |_) | ____  / _ \\ | |_) | | \n"
            + " |  _  | |_| |  _  |  | |___| |_| |  _ < |____|/ ___ \\|  __/| | \n"
            + " |_| |_|____/|_| |_|   \\____|____/|_| \\_\\     /_/   \\_\\_|  |___|  (v{})\n",
        fhirConfigProperties.getVersion());
  }

  public static void main(String[] args) {
    SpringApplication.run(ClinicalDataRepositoryApiApplication.class, args);
  }

}
