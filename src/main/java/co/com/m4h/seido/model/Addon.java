package co.com.m4h.seido.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Jose Molina on 21/2/18.
 */
@Getter
@Setter
@ToString
@Entity
public class Addon extends AbstractEntity {

	private String name;

	@JsonIgnore
	private String description;

	@ManyToOne
	@JsonIgnore
	private Company company;

	private int enabled;

	private String path;

	public Addon() {
	}

	/**
	 * Constructor useful for services that just have the AddonId.
	 * 
	 * @param id
	 *            Identifier of the Addon.
	 */
	public Addon(Long id) {
		this.setId(id);
	}
}
