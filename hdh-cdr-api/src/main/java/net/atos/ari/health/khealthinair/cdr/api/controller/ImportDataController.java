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

package net.atos.ari.health.khealthinair.cdr.api.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.atos.ari.health.khealthinair.cdr.api.service.ImportDataService;
import net.atos.ari.health.khealthinair.model.enums.Pilot;

/**
 * This controller is designed as the main entry point for the data coming from
 * REDCap
 */
@RestController
@RequestMapping(value = "/v1/import")
@Tag(name = "Import Data", description = "Import Data API for the K-HEALTHinAIR project.")
@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin"), @SecurityRequirement(name = "apikeyForPilot") })
public class ImportDataController {

	@Autowired
	private ImportDataService importDataService;

	/**
	 *
	 * @return The tabulated file with the patient questionnaires data
	 */
	@PostMapping(value = "/uploadREDCapFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	// @formatter:off
	@Operation(operationId = "uploadREDCapFile",
		summary = "UploadRedCapFile",
		description = "Import a CSV RedCap file, as described in the K-HEALTHinAIR project for this aggregation.<br>"
			+ "Note the following characteristics:"
			+ "<ul>"
			+ "  <li>Contains a first column **'record_id_atos'** that identifies the subject to which the data in the following rows belong.</li>"
			+ "  <li>The next rows use the names used in the model definition (RedCAPRow).</li>"
			+ "</ul>")
	public ResponseEntity<String> uploadFile(@RequestParam("REDCap_csv_file") MultipartFile file,
			@RequestParam(name = "Pilot", defaultValue = "ES", required = true)
				@NotNull(message = "A Pilot selection must be provided") Pilot pilot,
			@RequestParam(name = "Description", defaultValue = "File from REDCap project report Data Export-KHIA-EVIDEN", required = true)
				@NotBlank(message = "A Description must be provided") String description) {
		// @formatter:on
		if (file == null || !"text/csv".equalsIgnoreCase(file.getContentType())) {
			return ResponseEntity.status(500).body("Empty or non CSV File");
		}
		if (file.getOriginalFilename().length() > 64) {
			return ResponseEntity.status(400).body(
					"Please, reduce the name of the file to max 64 chars so a copy can be included as Binary and name used as id");
		}
		if (pilot == null) {
			return ResponseEntity.status(400).body("Invalid or missing Pilot value");
		}
		description = description + " (" + LocalDate.now() + ")";

		importDataService.processREDCapFile(file, pilot, description);
		return ResponseEntity.ok("Fichero CSV " + file.getOriginalFilename() + " procesado");
	}

}
