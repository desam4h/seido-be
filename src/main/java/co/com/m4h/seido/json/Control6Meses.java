package co.com.m4h.seido.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Jose Molina on 12/2/18.
 */

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "patientId", "name", "surgeryDate", "email" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Control6Meses {

	@JsonProperty("patientId")
	public Long patientId;

	@JsonProperty("name")
	public String name;

	@JsonProperty("surgeryDate")
	private String surgeryDate;

	@JsonProperty("email")
	public String email;

	public Control6Meses(Long id, String name, String date, String email) {
		this.patientId = id;
		this.name = name;
		this.surgeryDate = date;
		this.email = email;
	}
}
