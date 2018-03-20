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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "showCompletedPage", "goNextPageAutomatic", "pageNextText", "pagePrevText", "pages",
		"showProgressBar" })
public class SurveyJs {

	@JsonProperty("showCompletedPage")
	public Boolean showCompletedPage;

	@JsonProperty("goNextPageAutomatic")
	public Boolean goNextPageAutomatic;

	@JsonProperty("pageNextText")
	public String pageNextText;

	@JsonProperty("pagePrevText")
	public String pagePrevText;

	@JsonProperty("pages")
	public List<Page> pages = null;

	@JsonProperty("showProgressBar")
	public String showProgressBar;
}
