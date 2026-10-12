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
