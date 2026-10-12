package net.atos.ari.health.khealthinair.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.ari.health.khealthinair.model.enums.QuestionnaireCode;
import net.atos.ari.health.khealthinair.model.enums.QuestionnairePart;

/**
 * Class for maintaining RedCAPRow data.
 *
 */
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RedCAPRow {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface InstrumentAndQuestionnaire {
		QuestionnairePart QuestionnairePart();

		QuestionnaireCode questionnaireCode() default QuestionnaireCode.NONE;
	}

	private String redcap_repeat_instrument;
	private Integer redcap_repeat_instance;
	private String record_id_atos;

	/*-
	 * PART A
	 ************************************************************************/
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String date_part_a;

	// Sociodemographic questionnaire
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String yearofbirth;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String countryofbirth;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String countryofbirth2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String sex;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String educationlevel_oah;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String relationshipstatus;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_ext___0;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_ext___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_ext___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_ext___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_ext___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_ext___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String work_job;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String timespent5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String livingarrangements1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String livingarrengements2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String ethnicity;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String alcoholamount;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String smokingstatus1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String smokestatus2___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String smokestatus2___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String smokestatus2___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String smokestatus2___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String passivesmoke;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String height_2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String weight_2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String bmivalue_2;

	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTA, questionnaireCode = QuestionnaireCode.SOCIODEMOGRAPHIC)
	private String part_a_datos_sociodemograficos_complete;

	/*-
	 * PART B
	 ************************************************************************/
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTB, questionnaireCode = QuestionnaireCode.TIMESPENTINTHESCENARIO)
	private String date_part_b;

	// Time spent in the scenario
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTB, questionnaireCode = QuestionnaireCode.TIMESPENTINTHESCENARIO)
	private String timespent1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTB, questionnaireCode = QuestionnaireCode.TIMESPENTINTHESCENARIO)
	private String timespent2;
	// part_b_complete

	/*-
	 * PART C
	 * ICHOM Adult Health Standard Set (PROMS: Patient Reported Outcome Measures)
	 * Uses questionnaires: PROMIS Global-10, PHQ-2 / PHQ-9, GAD-2 / GAD-7, EQ-5D-5L, etc.
	 ************************************************************************/
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC)
	private String date_part_c;
	// repetition_ichom
	// complete_ichom

	// SACQ (Self-Administered Comorbidity Questionnaire)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___0;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___9;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___11;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___12;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___13;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq___14;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_others;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbidities14;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_heartdiseasefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_heartdiseasefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_highbloodpressurefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_highbloodpressurefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_lungdiseasefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_lungdiseasefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_diabetesfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_diabetesfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_stomachdiseasefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_stomachdiseasefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_kidneydiseasefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_kidneydiseasefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_liverdiseasefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_liverdiseasefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_blooddiseasefu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_blooddiseasefu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_cancerfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_cancerfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_depressionfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_depressionfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_osteoarthritisfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_osteoarthritisfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_backpainfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_backpainfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_rheumatoidarthritisfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_rheumatoidarthritisfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_otherfu1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbiditiessacq_otherfu2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String comorbidities_score;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String cardiovascularrisk_q3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String pavsday;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String pavstime;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String pavsdayxpavstime;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String seeing;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.SACQ)
	private String hearing;

	// PROMIS Global-10 Questionnaire
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q01;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q02;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q03;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q05;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q09r;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_08r;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_07r;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q06;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q04;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_10_q10r;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_score1_ph;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.PROMISGH10)
	private String promis_score2_mh;

	// WHODAS-12 (The World Health Organization Disability Assessment Schedule 2.0)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q01;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q02;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q03;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q04;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q05;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q06;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q07;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q08;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q09;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q11;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_q12;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_12_score_none;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_12_score_mild;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_12_score_moderate;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_12_score_severe;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHODAS12)
	private String whodas_12_score_extreme;

	// WHO-5 (The World Health Organization-Five Well-Being Index)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_q01;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_q02;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_q03;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_q04;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_q05;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_score_sum;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTC, questionnaireCode = QuestionnaireCode.WHO5)
	private String who_5_score;
	// uuid_v2
	// startdate_v2
	// enddate_v2
	// scheduledate_v2
	// status_v2
	// supplementaldata_v2
	// serializedresult_v2
	// part_c_ichom_complete

	/*-
	 * PART D
	 ************************************************************************/
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.NONE)
	private String date_part_d;
//	repetition_paris

	// PaRIS (Patient-Reported Indicator Surveys)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility3_1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility4_1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String accesibility5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String communication1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String communication2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String communication3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String sharedecisionmaking1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String sharedecisionmaking2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String continuityandcoordination1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String continuityandcoordination2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String continuityandcoordination2_1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String continuityandcoordination3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.PARIS)
	private String continuityandcoordination4;
	// uuid_2
	// startdate
	// enddate_2
	// scheduledate_2
	// status_2
	// supplementaldata_2
	// serializedresult_2
	// part_d_paris_survey_prems_complete

	/*-
	 * Observational IPCHEM checklist (D1.8 pg.191)
	 ************************************************************************/
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String date;

	// Residencial characteristics
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_home;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_apartment;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String elevator;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String dust;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String remaining_material___7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element2___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element2___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element2___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element2___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element2___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element2___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element3___8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element4___7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element5___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element5___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element5___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element5___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element5___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element5___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String air_dct_element6___9;
	// Description of sampling microenvironment
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String room_with_window_on_street;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String number_of_times_per_day_the_windows_are_opened;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String traffic_intensity_around_sampling_site;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String sampling_site_dimension_m3;
	// Surroundings
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String dwelling_humid; // In D8.1 is in previous section
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String construction___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String construction___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String construction___3;
	// private String construction_1; ??
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String green_area;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String traffic;
	// Pets
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String pets;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String typepet;
	// Cooking
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String typeofcooker;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String extractor;
	// Heating and cooling
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String gasboiler_bath;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String gasheater_dwelling;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_heating_system___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_heating_system___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_heating_system___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_heating_system___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_heating_system___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String type_of_heating_system___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String cooling___0;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String cooling___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String cooling___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String cooling___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String heating_system_other;
	// General cleaning
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String general_cleaning;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String cleaning_products;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String cleaning_methods;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.OBSERVATIONALIPCHEM)
	private String allergic_cleaning_product;
	// private String remaining;
	// observacional_ipchem_complete

	/*-
	 FS/FOT (Forced Spirometry/Forced Oscillation Technique)
	 */
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String f1; // date
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String performance;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String dental_prostheses;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String dental_removed;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smoke;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhaler;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___1;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___2;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___3;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___4;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___5;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___6;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___7;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers___8;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String inhalers_push;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String symptoms;
//	// Vital constants
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String pulsioximetria;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String ta2;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String fc1;
//	// SmokerStatus
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokestatus___0;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokestatus___1;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokestatus___2;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokerstatus1;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokerstatus2;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokerstatus3;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokerstatus4;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String smokerstatus5;
//	// Machine register
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String ethnicity_2;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String height;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String weight;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String bmivalue;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String collaborative;
//	// FORCED OSCILLATION TECHNIQUE Measured (Predicted|5/95P|Z-Score)
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___1;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___2;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___4;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___5;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___6;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___7;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___8;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___9;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String emotion___10;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String fot_realized;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String r5;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String r5_20;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String ax;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String reactance;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String resonance_frequency;
//	// 2a.- FORCED SPIROMETRY Measured (Predicted|5/95P|Z-Score)
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String spirometry_realized;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String fvc;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String fev1;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String fev1_fvc;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String pef;
//	// LUNG TESTING COMMENTS
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String lung_test_comments;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String lung_test_comments_alds;
//	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.FSFOT)
//	private String fsfot_complete;
	// f1_v6m
	// pruebas_funcionales_v6m
	// performance_v6m
	// dental_prostheses_v6m
	// dental_removed_v6m
	// smoke_v6m
	// inhaler_v6m
	// inhalers_v6m___1
	// inhalers_v6m___2
	// inhalers_v6m___3
	// inhalers_v6m___4
	// inhalers_v6m___5
	// inhalers_v6m___6
	// inhalers_v6m___7
	// inhalers_v6m___8
	// inhalers_push_v6m
	// symptoms_v6m
	// pulsioximetria_v6m
	// ta2_v6m
	// fc1_v6m
	// smokestatus_v6m___0
	// smokestatus_v6m___1
	// smokestatus_v6m___2
	// smokerstatus1_v6m
	// smokerstatus2_v6m
	// smokerstatus3_v6m
	// smokerstatus4_v6m
	// smokerstatus5_v6m
	// ethnicity_2_v6m
	// edad_v6m
	// height_v6m
	// weight_v6m
	// bmivalue_v6m
	// collaborative_v6m
	// emotion_v6m___1
	// emotion_v6m___2
	// emotion_v6m___4
	// emotion_v6m___5
	// emotion_v6m___6
	// emotion_v6m___7
	// emotion_v6m___8
	// emotion_v6m___9
	// emotion_v6m___10
	// lothar_fot_v6m
	// fot_realized_v6m
	// qualitygrading_fot_v6m
	// r5_v6m
	// r20_v6m
	// r5_20_v6m
	// ax_v6m
	// reactance_v6m
	// resonance_frequency_v6m
	// lothar_fs_v6m
	// spirometry_realized_v6m
	// qualitygrading_fs_v6m_fev1
	// qualitygrading_fs_v6m_fvc
	// fvc_v6m
	// fev1_v6m
	// fev1_fvc_v6m
	// pef_v6m
	// lung_test_comments_v6m
	// lung_test_comments_alds_v6m
	// print_v6m
	// fsfot_v6m_complete

	// CAT (COPD Assessment Test)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String date_cat;
	// complete_cat
	// repetition_cat
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.CAT)
	private String cat_score;
	// copd_assessment_test_cat_complete

	// mMRC (modified Medical Research Council Dyspnea scale)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MMRC)
	private String date_mmrc;
	// complete_mmrc
	// repetition_mmrc
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MMRC)
	private String mmrc_score;
	// mmrc_complete

	// TAI-12 (Test of Adherence to Inhalers 12)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String date_tai;
	// inh_yes_no
	// repetition_tai
	// complete_tai
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai9;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai_score;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai11;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai12;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai_score1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai_score2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.TAI12)
	private String tai_score3;
	// tai12_complete

	// ATC (Asthma Control Test)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String date_act;
	// complete_act
	// repetition_act
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String act_1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String act_2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String act_3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String act_4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String act_5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.ACT)
	private String act_score;
	// asthma_control_test_act_complete

	// MiniAQLQ (Mini Asthma Quality of Life Questionnaire)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String date_miniaqlq;
	// complete_miniaqlq
	// repetition_miniaqlq
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq9;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq11;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq12;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq13;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq14;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq15;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String miniaqlq_score;
	// private String miniaqlq_score1;
	// private String miniaqlq_score_2;
	// private String miniaqlq_score_22;
	// private String miniaqlq_score_44;
	// private String miniaqlq_score_3;
	// private String miniaqlq_score_33;
	// private String miniaqlq_score_4;
	// private String sum_miniaqlq;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.MINIAQLQ)
	private String totalscore_miniaqlq;
	// miniaqlq_complete

	// SNOT-22 (Sino-Nasal Outcome Test)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String date_snot22;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot9;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot11;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot12;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot13;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot14;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot15;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot16;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot17;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot18;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot19;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot20;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot21;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot22;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___9;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___11;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___12;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___13;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___14;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___15;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___16;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___17;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___18;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___19;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___20;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___21;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot23___22;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTD, questionnaireCode = QuestionnaireCode.SNOT22)
	private String snot_score;
	// snot22_complete

	// EQ-5D-5L (5-level EQ-5D Questionnaire)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String eq5d5y_for_students_timestamp;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String mobility;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String self_care;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String usual_activities;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String pain_discomfort;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String health_today_scale;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTE, questionnaireCode = QuestionnaireCode.EQ5D5L)
	private String anxiety_depression;
	// eq5d5y_for_students_complete

	// PHQ-9 (Patient Health Questionnaire)
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String phq9_timestamp;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh9; // Not in PHQ-8
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh10; // adapted Poland
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mh11; // adapted Poland
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mentalhealthscore;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.PARTF, questionnaireCode = QuestionnaireCode.PHQ9)
	private String mentalhealthscore8; // Sum without mh9
	// phq9_complete

	// GermanyCanteenStaff
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String record_date;
	// private String date_complete;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp7;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp8;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp9;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String wp10;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String fatigue___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String fatigue___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String fatigue___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String fatigue___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String fatigue___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String fatigue___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String headache___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String headache___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String headache___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String headache___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String headache___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String headache___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String nausea___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String nausea___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String nausea___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String nausea___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String nausea___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String nausea___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String concentration___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String concentration___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String concentration___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String concentration___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String concentration___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String concentration___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_eyes___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_eyes___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_eyes___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_eyes___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_eyes___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_eyes___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String running_nose___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String running_nose___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String running_nose___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String running_nose___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String running_nose___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String running_nose___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_throat___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_throat___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_throat___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_throat___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_throat___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_throat___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String cough___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String cough___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String cough___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String cough___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String cough___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String cough___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String facial_skin___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String facial_skin___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String facial_skin___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String facial_skin___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String facial_skin___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String facial_skin___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_ears___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_ears___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_ears___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_ears___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_ears___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String itchy_ears___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_skin___1;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_skin___2;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_skin___3;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_skin___4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_skin___5;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String dry_skin___6;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String iaq_perception4;
	@InstrumentAndQuestionnaire(QuestionnairePart = QuestionnairePart.EXTRAGERMANY, questionnaireCode = QuestionnaireCode.GERMANYCANTEENSTAFF)
	private String iaq_perception5;
	// extra_germany_canteen_staff_complete;

	/*
	 * List Fields names for logging purposes
	 */
	String[]

			getFieldNames(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		String[] names = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			names[i] = fields[i].getName();
		}
		return names;
	}

	/*
	 * Return pairs of populated not null instance values
	 */
	public Map<String, String> getFieldsWithValues() {
		Map<String, String> fieldsWithValues = new HashMap<>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(this);
				if (value != null && !value.toString().isBlank()) {
					fieldsWithValues.put(field.getName(), value.toString());
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return fieldsWithValues;
	}

	/**
	 *
	 * @param clazz
	 * @param questionnaireCode
	 * @return
	 */
	public static List<String> getFieldNamesByQuestionnaireCode(Class<?> clazz, QuestionnaireCode questionnaireCode) {
		return Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(InstrumentAndQuestionnaire.class)).filter(f -> f
						.getAnnotation(InstrumentAndQuestionnaire.class).questionnaireCode().equals(questionnaireCode))
				.map(Field::getName).collect(Collectors.toList());
	}

}
