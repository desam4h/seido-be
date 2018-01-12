package co.com.m4h.seido.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by hernan on 6/30/17.
 */
@Getter
@Setter
@ToString
@Entity
public class Survey extends AbstractEntity {

	@ManyToOne
	private SurveyTemplate template;

	@ManyToOne
	private Event event;

	@ManyToOne
	@JsonIgnore
	private Patient patient;

	@Column(columnDefinition = "text")
	private String surveyAnswers;

	@Enumerated(EnumType.STRING)
	private SurveyState state;

	public Survey() {
		this.state = SurveyState.NOT_STARTED;
	}

	public Survey(SurveyTemplate template) {
		this();
		this.template = template;
	}

	public Survey(SurveyTemplate template, Event event) {
		this(template);
		this.event = event;
		this.patient = event.getPatient();
	}

	public Survey(SurveyTemplate template, Patient patient) {
		this(template);
		this.patient = patient;
	}
}