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
