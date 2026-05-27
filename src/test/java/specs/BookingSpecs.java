package specs;

import helpers.AllureLoggerFilter;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.*;

public class BookingSpecs {

    public static RequestSpecification getBookingRequestSpec = with()
            .filter(new AllureLoggerFilter())
            .log().uri()
            .log().headers();

    public static ResponseSpecification getBookingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .expectStatusCode(200)
            .build();
}
