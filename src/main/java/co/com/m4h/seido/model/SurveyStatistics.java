package co.com.m4h.seido.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by hernan on 6/30/17.
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyStatistics {

	@Id
	private Long surveyId;

	private Long companyId;

	private Long specialtyId;

	private Long templateId;

	private Long eventId;

	private Long patientId;

	private String surveyAnswersCsv;

}