package net.atos.ari.health.khealthinair.cdr.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Disabled("Skip all")
public class QuestionnaireServiceTest {

	// static final String BASE_URL = "http://localhost:8081/hdh-cdr-api/";
	@Value("${spring.security.api-key-admin}")
	String API_KEY_ADMIN;
	@Value("${spring.security.api-key-pilot}")
	String API_KEY_PILOT;

	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private int port;

	@Test
	@Disabled
	void testUploadREDCapFileWith_part_a_datos_sociodemograficos() throws Exception {
		String FILE_NAME = "TEST_KHiABCN200Pacientes-part_a_datos_sociodemograficos.csv";
		ClassPathResource resource = new ClassPathResource("ingest_from_csv/" + FILE_NAME);
		try (InputStream is = resource.getInputStream()) {
			MockMultipartFile file = new MockMultipartFile("REDCap_csv_file", FILE_NAME, "text/csv", is);
		// @formatter:off
			mockMvc
			.perform(multipart("/v1/import/uploadREDCapPFile")
					.file(file).param("Pilot", "ES")
					.param("Description", "desc")
					.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(content().string("Fichero CSV " + FILE_NAME + " procesado"));
	  // @formatter:off
		}
	}

	@Test
	@Disabled
	void testExport_SOCIODEMOGRAPHIC() throws Exception {
		// @formatter:off
		MvcResult result = mockMvc
			.perform(get("/v1/export/questionnaire-responses")
				.param("Questionnaire", "SOCIODEMOGRAPHIC")
				.contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andExpect(status().isOk())
			.andReturn();
		  // @formatter:on
		byte[] exportedContent = result.getResponse().getContentAsByteArray();
		assert (exportedContent.equals(null));
	}

	@Test
	// @Disabled
	void testCompareImportedwithExported() throws Exception {
		String FILE_NAME = "part_a_datos_sociodemograficos_TEST.csv";

		// @formatter:off
		mockMvc.perform(
				delete("/v1/admin/expunge-questionnaires-responses")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.header("X-API-Key", API_KEY_ADMIN)
				)
		.andExpect(status().isOk());
		// @formatter:on

		ClassPathResource fileToImport = new ClassPathResource("ingest_from_csv/" + FILE_NAME);
		List<Map<String, String>> fileImported;
		try (InputStream importIs = fileToImport.getInputStream()) {
			MockMultipartFile fileImportedContet = new MockMultipartFile("REDCap_csv_file", FILE_NAME, "text/csv",
					importIs);
			// @formatter:off
			mockMvc.perform(
			        multipart("/v1/import/uploadREDCapFile")
			            .file(fileImportedContet)
			            .param("Pilot", "ES")
			            .param("Description", "desc")
			            .contentType(MediaType.MULTIPART_FORM_DATA)
			            .header("X-API-Key", API_KEY_PILOT)
				    )
		    .andExpect(status().isOk());
            // @formatter:on

			fileImported = parseCsvFromInputStream(fileImportedContet.getInputStream(), '|');
		}
		System.out.println("📥 FileImported:");
		printCsv(fileImported);

		// @formatter:off
		MvcResult result = mockMvc.perform(
				get("/v1/export/questionnaire-responses")
					.param("Questionnaire", "SOCIODEMOGRAPHIC")
					.contentType("application/octet-stream")
					.header("X-API-Key", API_KEY_PILOT)
				)
			.andExpect(status().isOk()).andReturn();
		// @formatter:on

		String fileExportedContent = result.getResponse().getContentAsString();
		List<Map<String, String>> fileExported = parseCsv(fileExportedContent, '|');
		System.out.println("📤 FileExported:");
		printCsv(fileExported);

		// Common Columns
		Set<String> commonColumns = new HashSet<>(fileImported.get(0).keySet());
		commonColumns.retainAll(fileExported.get(0).keySet());
		System.out.println(String.join(" | ", commonColumns));

		// Filter CSV1 by value and reduce to common columns
		List<Map<String, String>> csv1Filtrado = fileImported.stream()
				.filter(row -> "part_a_datos_sociodemograficos".equals(row.get("redcap_repeat_instrument")))
				.map(row -> {
					Map<String, String> reduced = new LinkedHashMap<>();
					for (String col : commonColumns) {
						reduced.put(col, row.get(col));
					}
					return reduced;
				}).collect(Collectors.toList());

		System.out.println("🔍 Imported FILTERED AND REDUCED:");
		printCsv(csv1Filtrado);

		// Reduce CSV2 to common columns
		List<Map<String, String>> fileExportedReducido = fileExported.stream().map(row -> {
			Map<String, String> reduced = new LinkedHashMap<>();
			for (String col : commonColumns) {
				reduced.put(col, row.get(col));
			}
			return reduced;
		}).collect(Collectors.toList());
		System.out.println("🧩 Exported REDUCED:");
		printCsv(fileExportedReducido);

		boolean esSubset = fileExportedReducido.stream().allMatch(csv1Filtrado::contains);

		assertTrue(esSubset,
				"CSV2 must be a subset of imported CSV1 (filtered by redcap_repeat_instrument = part_a_sociodemographic_data)");
	}

	private List<Map<String, String>> parseCsv(String csvContent, char separator) throws Exception {
		try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvContent))
				.withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build()) {

			List<String[]> rows = reader.readAll();
			String[] headers = rows.get(0);

			return rows.subList(1, rows.size()).stream().map(row -> {
				Map<String, String> map = new LinkedHashMap<>();
				for (int i = 0; i < headers.length && i < row.length; i++) {
					map.put(headers[i], row[i]);
				}
				return map;
			}).collect(Collectors.toList());
		}
	}

	/**
	 *
	 * @param is
	 * @param separator
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, String>> parseCsvFromInputStream(InputStream is, char separator) throws Exception {
		try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(is))
				.withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build()) {

			List<String[]> rows = reader.readAll();
			String[] headers = rows.get(0);

			return rows.subList(1, rows.size()).stream().map(row -> {
				Map<String, String> map = new LinkedHashMap<>();
				for (int i = 0; i < headers.length && i < row.length; i++) {
					map.put(headers[i], row[i]);
				}
				return map;
			}).collect(Collectors.toList());
		}
	}

	/**
	 *
	 * @param csv
	 */
	private void printCsv(List<Map<String, String>> csv) {
		if (csv == null || csv.isEmpty()) {
			System.out.println("(Empty CSV)");
			return;
		}
		Set<String> headers = csv.get(0).keySet();
		System.out.println(String.join(" | ", headers));
		for (Map<String, String> row : csv) {
			System.out.println(headers.stream().map(col -> Optional.ofNullable(row.get(col)).orElse(""))
					.collect(Collectors.joining(" | ")));
		}
		System.out.println("Total rows: " + csv.size());
		System.out.println("------------------------------------------------------");
	}

}