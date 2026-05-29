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

    public static RequestSpecification getBookingRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .log().method()
            .log().uri()
            .log().headers();

    public static ResponseSpecification getBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(200)
            .build();

    public static ResponseSpecification getNotFoundBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(404)
            .build();

    public static RequestSpecification deleteBookingRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .contentType(JSON)
            .header("Cookie", "token=" + new UsefulMethodsForTests().createToken())
            .log().method()
            .log().uri()
            .log().headers();

    public static ResponseSpecification deleteBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(201)
            .build();

}
