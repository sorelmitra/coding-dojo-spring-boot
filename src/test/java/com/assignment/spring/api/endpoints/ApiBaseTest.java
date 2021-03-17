package com.assignment.spring.api.endpoints;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

class ApiBaseTest {

    private UriComponentsBuilder builder;

    private String baseUrl = "http://dummy.site";
    private String appKeyParam = "app-key";
    private String appKey = "dummyappkey";

    private ApiBase apiBase = new ApiBase(baseUrl, appKeyParam, appKey);

    @Test
    void buildPathBasic() {
        builder = apiBase.buildPath();
        assertEquals(baseUrl, builder.build().toUriString());
    }

    @Test void addKeyParamBasic() {
        builder = apiBase.buildPath();
        builder = apiBase.addKeyParam(builder);
        assertEquals(expectedUri(), builder.build().toUriString());
    }

    private String expectedUri() {
        return String.format("%s?%s=%s", baseUrl, appKeyParam, appKey);
    }
}