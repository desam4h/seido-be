package co.com.m4h.seido.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

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

	// @ManyToOne
	// private Specialty specialty;

	@ManyToMany
	@JoinTable(name = "SURVEY_TEMPLATE_SPECIALTY", joinColumns = @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "SPECIALTY_ID", referencedColumnName = "ID"))
	private List<Specialty> specialties;

	private Integer order_id;
}
