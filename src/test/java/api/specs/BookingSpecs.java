package api.specs;

import api.helpers.AllureLoggerFilter;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import tests.TestBase;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.*;
import static io.restassured.http.ContentType.JSON;

public class BookingSpecs extends TestBase {

    public static RequestSpecification bookingWithoutTokenRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .contentType(JSON)
            .log().method()
            .log().uri()
            .log().headers();

    public static ResponseSpecification successfulBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(200)
            .build();

    public static ResponseSpecification notFoundBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(404)
            .build();

    public static RequestSpecification bookingWithTokenRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .contentType(JSON)
            .header("Cookie", "token=" + getToken())
            .log().method()
            .log().uri()
            .log().headers();

    public static ResponseSpecification createdStatusBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(201)
            .build();

    public static ResponseSpecification forbiddenResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(403)
            .build();

}
