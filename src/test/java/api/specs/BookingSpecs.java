package api.specs;

import api.helpers.AllureLoggerFilter;
import api.usefulMethods.UsefulMethodsForTests;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.*;
import static io.restassured.http.ContentType.JSON;

public class BookingSpecs {

    public static RequestSpecification bookingWithoutTokenRequestSpec = with()
            .filter(new AllureLoggerFilter())
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
            .header("Cookie", "token=" + new UsefulMethodsForTests().createToken())
            .log().method()
            .log().uri()
            .log().headers();

    public static ResponseSpecification createdStatusBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(201)
            .build();

}
