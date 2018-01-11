package co.com.m4h.registros.model;

import javax.persistence.Entity;
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
public class Specialty extends AbstractEntity {
	private String name;

	@JsonIgnore
	@ManyToOne
	private Company company;
}
