
package com.assignment.spring.api.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.assignment.spring.api.model.ApiModelClouds;
import com.assignment.spring.api.model.ApiModelCoord;
import com.assignment.spring.api.model.ApiModelMain;
import com.assignment.spring.api.model.ApiModelSys;
import com.assignment.spring.api.model.ApiModelWeather;
import com.assignment.spring.api.model.ApiModelWind;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "coord",
    "weather",
    "base",
    "main",
    "visibility",
    "wind",
    "clouds",
    "dt",
    "sys",
    "id",
    "name",
    "cod"
})
public class ApiResponseWeather {

    @JsonProperty("coord")
    private ApiModelCoord apiModelCoord;
    @JsonProperty("weather")
    private List<ApiModelWeather> apiModelWeather = null;
    @JsonProperty("base")
    private String base;
    @JsonProperty("main")
    private ApiModelMain apiModelMain;
    @JsonProperty("visibility")
    private Integer visibility;
    @JsonProperty("wind")
    private ApiModelWind apiModelWind;
    @JsonProperty("clouds")
    private ApiModelClouds apiModelClouds;
    @JsonProperty("dt")
    private Integer dt;
    @JsonProperty("sys")
    private ApiModelSys apiModelSys;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("cod")
    private Integer cod;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("coord")
    public ApiModelCoord getCoord() {
        return apiModelCoord;
    }

    @JsonProperty("coord")
    public void setCoord(ApiModelCoord apiModelCoord) {
        this.apiModelCoord = apiModelCoord;
    }

    @JsonProperty("weather")
    public List<ApiModelWeather> getWeather() {
        return apiModelWeather;
    }

    @JsonProperty("weather")
    public void setWeather(List<ApiModelWeather> apiModelWeather) {
        this.apiModelWeather = apiModelWeather;
    }

    @JsonProperty("base")
    public String getBase() {
        return base;
    }

    @JsonProperty("base")
    public void setBase(String base) {
        this.base = base;
    }

    @JsonProperty("main")
    public ApiModelMain getMain() {
        return apiModelMain;
    }

    @JsonProperty("main")
    public void setMain(ApiModelMain apiModelMain) {
        this.apiModelMain = apiModelMain;
    }

    @JsonProperty("visibility")
    public Integer getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("wind")
    public ApiModelWind getWind() {
        return apiModelWind;
    }

    @JsonProperty("wind")
    public void setWind(ApiModelWind apiModelWind) {
        this.apiModelWind = apiModelWind;
    }

    @JsonProperty("clouds")
    public ApiModelClouds getClouds() {
        return apiModelClouds;
    }

    @JsonProperty("clouds")
    public void setClouds(ApiModelClouds apiModelClouds) {
        this.apiModelClouds = apiModelClouds;
    }

    @JsonProperty("dt")
    public Integer getDt() {
        return dt;
    }

    @JsonProperty("dt")
    public void setDt(Integer dt) {
        this.dt = dt;
    }

    @JsonProperty("sys")
    public ApiModelSys getSys() {
        return apiModelSys;
    }

    @JsonProperty("sys")
    public void setSys(ApiModelSys apiModelSys) {
        this.apiModelSys = apiModelSys;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("cod")
    public Integer getCod() {
        return cod;
    }

    @JsonProperty("cod")
    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
