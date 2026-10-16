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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.atos.ari.health.khealthinair.cdr.api.config.ApiKeyRequestFilter;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("This is not UnitTest but Integration test")
public class AdminServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(ApiKeyRequestFilter.class);


	static final String API_KEY_ADMIN = "apikeyadmintest";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void expungeEverything() {
		try {
			Thread.sleep(5000);
			ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
					.post(new URI("/v1/admin/expunge-everything"))
					.contextPath("/hdh-cdr-api").header("X-API-Key", API_KEY_ADMIN).header("accept", "*/*"));
			resultActions.andReturn().getResponse().getStatus();
			logger.info("Test expungeEverything result: " + resultActions.andReturn().getResponse().getStatus());
			assertTrue(resultActions.andReturn().getResponse().getStatus() == 200);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
