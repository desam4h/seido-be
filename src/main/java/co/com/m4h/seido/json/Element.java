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
@JsonPropertyOrder({ "type", "name", "title", "isRequired", "choices", "colCount" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Element {

	@JsonProperty("type")
	public String type;

	@JsonProperty("name")
	public String name;

	@JsonProperty("title")
	public String title;

	@JsonProperty("isRequired")
	public Boolean isRequired;

	@JsonProperty("choices")
	public List<Choice> choices;

	@JsonProperty("colCount")
	public Integer colCount;
}
