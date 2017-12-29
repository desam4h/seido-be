package co.com.m4h.registros.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

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
public class SurveyTemplate extends AbstractEntity {
	private String name;

	@Column(columnDefinition = "text")
	private String jsSurvey;

	@Enumerated(EnumType.STRING)
	private SurveyType type;

	@ManyToOne
	private Specialty specialty;
}
