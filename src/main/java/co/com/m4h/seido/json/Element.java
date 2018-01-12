package co.com.m4h.seido.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "name", "isRequired", "choices", "colCount"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Element {
    @JsonProperty("type")
    public String type;
    @JsonProperty("name")
    public String name;
    @JsonProperty("isRequired")
    public Boolean isRequired;
    @JsonProperty("choices")
    public List<Choice> choices;
    @JsonProperty("colCount")
    public Integer colCount;
}
