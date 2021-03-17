
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

    public ApiModelCoord getCoord() {
        return apiModelCoord;
    }

    public void setCoord(ApiModelCoord apiModelCoord) {
        this.apiModelCoord = apiModelCoord;
    }

    public List<ApiModelWeather> getWeather() {
        return apiModelWeather;
    }

    public void setWeather(List<ApiModelWeather> apiModelWeather) {
        this.apiModelWeather = apiModelWeather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public ApiModelMain getMain() {
        return apiModelMain;
    }

    public void setMain(ApiModelMain apiModelMain) {
        this.apiModelMain = apiModelMain;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public ApiModelWind getWind() {
        return apiModelWind;
    }

    public void setWind(ApiModelWind apiModelWind) {
        this.apiModelWind = apiModelWind;
    }

    public ApiModelClouds getClouds() {
        return apiModelClouds;
    }

    public void setClouds(ApiModelClouds apiModelClouds) {
        this.apiModelClouds = apiModelClouds;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public ApiModelSys getSys() {
        return apiModelSys;
    }

    public void setSys(ApiModelSys apiModelSys) {
        this.apiModelSys = apiModelSys;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

}
