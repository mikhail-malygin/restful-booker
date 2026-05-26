package restfulBooker;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class RestfulBookingTests extends TestBase {

    @Test
    public void getAllBookings() {
        given()
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .log().all();
    }
}
