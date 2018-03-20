package co.com.m4h.seido.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "elements" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page {

	@JsonProperty("elements")
	public List<Element> elements = null;

	@JsonProperty("name")
	public String name;
}
