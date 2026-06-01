package api.specs;

import api.helpers.AllureLoggerFilter;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;

public class TokenSpecs {

    public static RequestSpecification getTokenRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .contentType(JSON)
            .log().method()
            .log().uri()
            .log().headers();

    public static ResponseSpecification getTokenResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(200)
            .build();

}
