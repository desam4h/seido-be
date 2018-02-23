package co.com.m4h.seido.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import co.com.m4h.seido.common.Constant;
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
public class Company extends AbstractEntity {
	private String name;

	@JsonFormat(pattern = Constant.ENTITY_GENERIC_DATE_PATTERN)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate createdDate;

	private String nit;

	@OneToMany(mappedBy = "company")
	@JsonIgnore
	private List<Addon> addons;

	public Company() {
	}

	/**
	 * Constructor useful for services that just have the companyId.
	 * 
	 * @param id
	 *            Identifier of the company.
	 */
	public Company(Long id) {
		this.setId(id);
	}
}
