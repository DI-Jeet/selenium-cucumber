package com.sky.web.services;

import com.sky.web.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * REST-Assured wrapper for API-level test preconditions.
 * Use to seed test data or verify backend state without going through the UI.
 */
public class ApiClient {

    private final String baseUrl;

    public ApiClient() {
        this.baseUrl = ConfigReader.getBaseUrl();
    }

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private RequestSpecification baseSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    public Response get(String path) {
        return baseSpec().when().get(path).then().extract().response();
    }

    public Response get(String path, Map<String, ?> queryParams) {
        return baseSpec().queryParams(queryParams).when().get(path).then().extract().response();
    }

    public Response post(String path, Object body) {
        return baseSpec().body(body).when().post(path).then().extract().response();
    }

    public Response post(String path, Map<String, Object> headers, Object body) {
        return baseSpec().headers(headers).body(body).when().post(path).then().extract().response();
    }

    public Response delete(String path) {
        return baseSpec().when().delete(path).then().extract().response();
    }
}
