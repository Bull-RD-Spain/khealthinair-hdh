package net.atos.ari.health.khealthinair.cdr.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.atos.ari.health.khealthinair.cdr.api.exception.SubjectNotFoundException;
import net.atos.ari.health.khealthinair.cdr.api.service.AdminService;
import net.atos.ari.health.khealthinair.cdr.api.service.PatientService;
import net.atos.ari.health.khealthinair.cdr.api.service.QuestionnaireService;
import net.atos.ari.health.khealthinair.model.enums.ResourceTypeSubset;

/**
 * This controller is designed as the main entry point for admin operations
 *
 */
@RestController
@RequestMapping(value = "/v1/admin")
@Tag(name = "Admin", description = "Admin API for the K-HEALTHinAIR project.")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private QuestionnaireService questionnaireService;

	/**
	 * Expunge Everything
	 *
	 * @return
	 */
	@Async("asyncExecutor")
	@DeleteMapping(value = "/expunge-everything", produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(operationId = "expungeEverything_Admin", summary = "Expunge all FHIR resources", description = "Delete all the FHIR resources from the HDH-CDR."
			+ " The batch size (records deleted within a single transaction) will be determined by the value of Expunge Batch Size property (default 800)<br>"
			+ "<img src='data:image/gif;base64,R0lGODlh8ADwAPcAAB8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNR8uNB8uNB8uNB8uNB8uNB8uNB8uNB8uNB8uNB8uNB8uNB8uNB8uNCAvNCAvNCAvNCAvNCAvNCAvNCAvNCAvNCEvNCEvNCEvNCIwNCIwMyQxMy04MjZAMUBHL0lPLlJVLVpcLGNiKmxqKXx3J4uCJZiNI6SWIa2dILqnHsexHNS7GtzBGevNF/PUFvfWFfjXFfjXFfjXFfjXFfjXFfjXFfjXFfjXFfjXFfjXFfjXFfjXFfjYFfnYFfnYFfnYFfnYFfnYFfnYFfnYFfnYFfnYFfnYFfzbFf7dFf7dFf7dFf7dFf7dFf7dFf7dFf7dFf/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eFv/eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eF//eFv/eFv/eFv/eFv/eGP/eGf/eG//eHP/eHv/fH//fIP/fIf/fIv/fI//fJP/fJP/fJf/fJv/fJv/fJ//fJ//fJ//fJ//gKP/gKP/gKP/gKP/gKf/gK//gLf/hMP/hMv/hNv/iOv/iPv/jQf/jQv/jRP/jRv/jR//jR//jR//jR//jR//kSP/kSP/kSP/kSP/kTP/lUP/mV//oY//pbv/qdf/re//rfv/rf//rf//rf//rf//rf//rf//sgP/sgP/sgP/sgP/sgP/sgP/sgf/shP/th//ujf/vkf/wlv/wmv/xof/zqf/0tv/2vf/2w//2w//2w//52v/64f/75//87P/98//++//+/v/+/v/+/v/+/v/+/v/+/v///////////////////////////////////////////////////////////////////////////////////////////////////////////////////yH/C05FVFNDQVBFMi4wAwEAAAAh+QQJPADxACH+H0dlbmVyYXRlZCBieSBvbmxpbmVHSUZ0b29scy5jb20ALAAAAADwAPAAAAj+AOMJHEiwoMGDCBMqXMiwocOF16Ad07WrmbSHGDNq3Mixo8ePIEMuPPZJSiIp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wGOyd26qzEtZcp5Zra0cnyqxAzjwLTVwA+Ny2KLa0EI9x6V5O9vspzaa2O/+e498zcyMtdmerY02nNq+vdsct3kKddFgajmMy9dnTK9tudWAFmq2a8HNIjsE8akoKrUIXbCBOtR/O5sPfM0FO9aI7VvK1dGybWD4696OeN7aCVt+hcHWzdy4JVWJMD2v95oUJsqJotXKarAjjL0Pt20YDoknmnDx0EyKvX/pSKZorNCbpZvt29k2ecZDvbpS8Ca7kNC8nYnbycKzzZ+nmdxJnJPiG8nLN0LXEIAnTuC3i3iQnKxhq91EnZsZTsgxPs8mAQ0lQVHZWtRDOrLuXLkijJHi/Zy0+tR7HdeAsQtfHbqCu8kAfDsmbuPIik7znOFtPqXrKgWoy9LnKqb+UQ7WW269Zbnm39yAfi7AkIILB+21WB3FOm7KSv67LE7fbinMSX6/dJML6CjZD07dWGq5o4qxN2vNf+qLo/7STj0YzQCt+9iFeGrm4mzbCWumO87jj+zSS20a0tB3X27LfQ7Z3D3lU+uijU7oL8rZ5KEz29XiDWLg11reB4t6OmjGQKzbcq26FeUYdC0QuOCTYe3epOzonY3LrZugXP7bvy7dCCoY3RE2y/68jO7fIo3i12qtrR7qgE6q0G5F0UEQ4TfgCk7bXV6FpG7n/7ytw57e0h3tkGsQ4Sllr46tph3vj+61xL7kUY7dAF86WXHU4bFD9oxnDi6w9g3q2anG0OZ+4QPPm7UOMdvsF8m+1y/e0GSdy6eN8A8v7zB43yhfnHCwkANhDYku7ic/0mIerjh/pe098oSq8g2+Ey1fEAUk9CsP8UY75x/65vAO5dWOsOHOHolw1AlxDbnwSNE59CYO60cfonAd8PWMhyXe9prxCWDfENYQDc1wDHif93q/93zf937/94Af+II/+IRf+IZ/+Iif+Ip/DM3wDOqkT5Af+ZI/+ZRf+ZZ/+Zif+Zq/+Zzf+Z7/+aAf+qI/+qRf+qbPNAEBACH5BAk8AA8AIf4fR2VuZXJhdGVkIGJ5IG9ubGluZUdJRnRvb2xzLmNvbQAsAAAAAPAA8ACH//////////////////////////////////////////////////////////////////////////////////////33////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////CP4AHwgcSLCgwYMIEypcyLChw4cQI0qcSLGixYsYM2rcyLGjx48gQ4ocSbKkyZMoU6pcybKly5cwY8qcSbOmzZs4c+rcybOnz59AgwodSrSo0aNIkypdyrSp06dQo0qdSrWq1atYs2rdyrWr169gw4odS7as2bNo06pdy7at27dw48qdS7eu3bt48+rdy7ev37+AAwseTLiw4cOIEytezLix48eQI0ueTLmy5cuYM2vezLmz58+gQ4seTbq06dOoU6tezbq169ewY8ueTbu27du4c+vezbu379/AgwsfTry48ePIkytfzry58+fQo0ufTr269evYs2vfzr279+/gw5KLH0++vPnz6NOrX8++vfv38OPLn0+/vv37+PPr38+/v///AAYo4IAEFmjggQgmqOCCDDbo4IMQRijhhBRWaOGFGGao4YYcdujhhyCGKOKIJJZo4okopqjiiiy26OKLMMYo44w01mjjjcxV8J6O7vGI449ABinkkEQWaeSRSCap5JJMNunkk1BGKeWUVFZpZYoBAQA7' height='15px'/>"
			+ " <b>WARNING</b>, please note that the ML predictions saved as RiskAssessment resources will be lost since is something calculated based on daily datasets that will no longer be calculated, so the predictions history could be lost.")
	@SecurityRequirement(name = "apikeyForAdmin")
	public String expungeEverything() {
		return adminService.expungeEverything();
	}

	/**
	 * Expunge Questionnaires
	 *
	 * @return
	 */
	@DeleteMapping(value = "/expunge-questionnaires", produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(operationId = "expungeQuestionnaires_Admin", summary = "Expunge FHIR resources of type Questionnaire/QuestionnaireResponses", description = "Delete all the FHIR Questionnaires and QuestionnaireResponse from the HDH-CDR")
	@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin") })
	public String expungeQuestionnaires() {
		return adminService.expungeQuestionnaires();
	}

	/**
	 * Expunge Questionnaires
	 *
	 * @return
	 */
	@DeleteMapping(value = "/expunge-questionnaires-responses", produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(operationId = "expungeQuestionnairesResponses_Admin", summary = "Expunge FHIR resources of type QuestionnaireResponses", description = "Delete all the FHIR QuestionnaireResponse from the HDH-CDR")
	@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin") })
	public String expungeQuestionnairesResponses() {
		return adminService.expungeQuestionnairesResponses();
	}

	/**
	 * Initialize questionnaires
	 *
	 * @param resourceType
	 * @return
	 */
	@PostMapping(value = "/questionnaires/initialize")
	@Operation(operationId = "initializeQuestionnaires", summary = "InitializeQuestionnaires in the HDH-CDR", description = "InitializeQuestionnaires.")
	@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin") })
	public void initializeQuestionnaires() {
		questionnaireService.initializeQuestionnaires();
	}

	/**
	 * Count resources
	 *
	 * @param resourceType
	 * @return
	 */
	@PostMapping(value = "/count-resources")
	@Operation(operationId = "countResources_Admin", summary = "Count FHIR resources stored in the HDH-CDR", description = "If a resource type is specified, only those will be counted.<br>"
			+ "Note that the CDR has been configured for the project with only a subset of all possible standard resources.")
	@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin"),
			@SecurityRequirement(name = "apikeyForPilot") })
	public String countResources(
			@RequestParam(name = "resourceType", required = false) final ResourceTypeSubset resourceType) {
		return adminService.countResources(resourceType);
	}

	/**
	 * Capability Statement
	 *
	 * @return
	 */
	@GetMapping(value = "/metadata")
	@Operation(operationId = "getMetadata_Admin", summary = "Capability Statement", description = "Get the Capability Statement (metadata)")
	@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin") })
	public List<String> getMetadata() {
		return adminService.metadata();
	}

	/**
	 * Delete a Patient using internal id (Right To Be Forgotten)
	 *
	 * @param patientId
	 * @return
	 */
	@Async("asyncExecutor")
	@DeleteMapping(value = "/patients/{patientId}", produces = MediaType.TEXT_PLAIN_VALUE)
	// @formatter:off
	@Operation(operationId = "deletePatientByPatientId_Admin", summary = "Delete patient in the HDH, via patientId",
		description = "Delete patient record in the HDH, using as reference the internal K-HEALTHinAIR \"patientId\"")
	@SecurityRequirements({ @SecurityRequirement(name = "apikeyForAdmin"),
			@SecurityRequirement(name = "apikeyForPilot") })
	public ResponseEntity<String> deletePatientByPatientId(
			@Parameter(description = "This is the internal patientId", example = "0a0a0a0a-0a0a-0a0a0a0a0-0a0a0a0a0a0a")
			@PathVariable(name = "patientId", value = "patientId", required = true) final String patientId) {
    // @formatter:on
		if (patientId.isBlank() || !patientService.existPatientByPatientId(patientId)) {
			throw new SubjectNotFoundException(
					"No patient found with ID [" + ((patientId.isBlank()) ? "*empty*" : patientId) + "].");
		} else {
			patientService.deletePatientInCascadeByPatientId(patientId);
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body("Patient for patientId [" + patientId + "] deleted successfully.");
		}
	}

}
