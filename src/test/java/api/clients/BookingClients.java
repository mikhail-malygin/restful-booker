package api.clients;

import data.TokenData;
import models.lombok.BookingDTO;
import models.lombok.ResponseBookingDTO;
import models.lombok.ResponseBookingsIdsDTO;
import models.lombok.ResponseTokenDTO;

import static api.specs.BookingSpecs.*;
import static api.specs.BookingSpecs.successfulBookingResponseSpec;
import static api.specs.TokenSpecs.getTokenRequestSpec;
import static api.specs.TokenSpecs.getTokenResponseSpec;
import static io.restassured.RestAssured.given;
import static api.specs.CreateBookingSpecs.createBookingRequestSpec;
import static api.specs.CreateBookingSpecs.createBookingResponseSpec;

public class BookingClients {

    public String createToken() {
        TokenData token = new TokenData();
        token.generateToken();
        ResponseTokenDTO response = given()
                .spec(getTokenRequestSpec)
                .body(token)
                .when()
                .post("/auth")
                .then()
                .spec(getTokenResponseSpec)
                .extract()
                .as(ResponseTokenDTO.class);

        return response.getToken();
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

