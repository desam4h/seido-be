package co.com.m4h.seido.model;

import javax.persistence.Entity;
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
public class Specialty extends AbstractEntity {
	private String name;

	@ManyToOne
	private Company company;
}
