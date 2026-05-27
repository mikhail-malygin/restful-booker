package specs;

import helpers.AllureLoggerFilter;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;

public class CreateBookingSpecs {

    public static RequestSpecification createBookingRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .contentType(JSON)
            .log().uri()
            .log().headers()
            .log().body();


    public static ResponseSpecification createBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(200)
            .build();
}
