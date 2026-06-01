package api.clients;

import models.*;
import tests.TestBase;

import static api.specs.BookingSpecs.*;
import static api.specs.BookingSpecs.successfulBookingResponseSpec;
import static api.specs.TokenSpecs.getTokenRequestSpec;
import static api.specs.TokenSpecs.getTokenResponseSpec;
import static io.restassured.RestAssured.given;
import static api.specs.CreateBookingSpecs.createBookingRequestSpec;
import static api.specs.CreateBookingSpecs.createBookingResponseSpec;

public class BookingClients extends TestBase {

    public void getHealthCheckEndpoint() {
        given()
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }

    public String createToken() {

        RequestTokenDTO authBody = new RequestTokenDTO();
        authBody.setUsername(config.username());
        authBody.setPassword(config.password());

        ResponseTokenDTO response = given()
                .spec(getTokenRequestSpec)
                .body(authBody)
                .when()
                .post("/auth")
                .then()
                .spec(getTokenResponseSpec)
                .extract()
                .as(ResponseTokenDTO.class);

        return response.getToken();
    }

    public String tryCreateTokenWithWrongPassword() {

        RequestTokenDTO authBody = new RequestTokenDTO();
        authBody.setUsername(config.username());
        authBody.setPassword(config.password() + "1");

        ResponseBadCredentialsTokenDTO response = given()
                .spec(getTokenRequestSpec)
                .body(authBody)
                .when()
                .post("/auth")
                .then()
                .spec(getTokenResponseSpec)
                .extract()
                .as(ResponseBadCredentialsTokenDTO.class);

        return response.getReason();

    }

    public ResponseBookingDTO createNewBooking(BookingDTO booking) {

        return given()
                .spec(createBookingRequestSpec)
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .spec(createBookingResponseSpec)
                .extract()
                .as(ResponseBookingDTO.class);
    }

    public void deleteBooking(Integer id) {

        given()
                .spec(bookingWithTokenRequestSpec)
                .when()
                .delete("/booking/" + id)
                .then()
                .spec(createdStatusBookingResponseSpec);
    }

    public void deleteBookingWithoutAuthToken(Integer id) {

        given()
                .spec(bookingWithoutTokenRequestSpec)
                .when()
                .delete("/booking/" + id)
                .then()
                .spec(forbiddenResponseSpec);
    }

    public ResponseBookingsIdsDTO[] getAllBookings() {
        return given()
                .spec(bookingWithoutTokenRequestSpec)
                .when()
                .get("/booking")
                .then()
                .spec(successfulBookingResponseSpec)
                .extract().as(ResponseBookingsIdsDTO[].class);
    }

    public BookingDTO getBookingById(Integer id) {
        return given()
                .spec(bookingWithoutTokenRequestSpec)
                .when()
                .get("/booking/" + id)
                .then()
                .spec(successfulBookingResponseSpec)
                .extract().as(BookingDTO.class);
    }

    public ResponseBookingsIdsDTO[] getBookingsByFullName(String firstName, String lastName) {
        return given()
                .spec(bookingWithoutTokenRequestSpec)
                .queryParam("firstname", firstName)
                .queryParam("lastname", lastName)
                .when()
                .get("/booking")
                .then()
                .spec(successfulBookingResponseSpec)
                .extract().as(ResponseBookingsIdsDTO[].class);
    }

    public void notFoundGetBookingById(Integer id) {
        given()
                .spec(bookingWithoutTokenRequestSpec)
                .when()
                .get("/booking/" + id)
                .then()
                .spec(notFoundBookingResponseSpec);
    }

    public BookingDTO fullUpdatesBooking(BookingDTO bookingUpdated, Integer id) {

        return given()
                .spec(bookingWithTokenRequestSpec)
                .body(bookingUpdated)
                .when()
                .put("/booking/" + id)
                .then()
                .spec(successfulBookingResponseSpec)
                .extract().as(BookingDTO.class);
    }

    public BookingDTO partialUpdatesBooking(BookingDTO bookingPatch, Integer id) {
        return given()
                .spec(bookingWithTokenRequestSpec)
                .body(bookingPatch)
                .when()
                .patch("/booking/" + id)
                .then()
                .spec(successfulBookingResponseSpec)
                .extract().as(BookingDTO.class);
    }
}

