package co.com.m4h.seido.model;

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
public class Patient extends AbstractEntity {

	private String firstName;

	private String lastName;

	private String nuip;

	@ManyToOne
	@JsonIgnore
	private Company company;
}