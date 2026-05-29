package tests;

import api.usefulMethods.UsefulMethodsForTests;
import data.BookingDataGenerator;
import data.TokenData;
import io.qameta.allure.Description;
import models.lombok.BookingDTO;
import models.lombok.ResponseBookingsIdsDTO;
import models.lombok.ResponseBookingDTO;
import models.lombok.ResponseTokenDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static api.specs.BookingSpecs.*;
import static api.specs.BookingSpecs.deleteBookingResponseSpec;
import static api.specs.TokenSpecs.getTokenRequestSpec;
import static api.specs.TokenSpecs.getTokenResponseSpec;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static api.specs.CreateBookingSpecs.createBookingRequestSpec;
import static api.specs.CreateBookingSpecs.createBookingResponseSpec;

public class RestfulBookingTests extends TestBase {

    @Test
    @Tag("CreateToken")
    @Tag("PositiveTests")
    @DisplayName("Creates a new auth token")
    @Description("Creates a new auth token to use for access to the PUT and DELETE /booking")
    public void shouldReturnCreatedToken() {

        TokenData token = new TokenData();
        token.generateToken();

        ResponseTokenDTO response = step("Make a request creates a new token", () ->
                given()
                        .spec(getTokenRequestSpec)
                        .body(token)
                        .when()
                        .post("/auth")
                        .then()
                        .spec(getTokenResponseSpec)
                        .extract()
                        .as(ResponseTokenDTO.class));

        step("Check a response creates a new token", () ->
                assertThat(response.getToken()).isNotNull());
    }

    @Test
    @Tag("GetBookings")
    @Tag("PositiveTests")
    @DisplayName("Returns all bookings ids")
    @Description("Returns the ids of all the bookings that exist within the API")
    public void shouldReturnAllBookings() {

        ResponseBookingsIdsDTO[] response = step("Make request get all bookings", () ->
                given()
                        .spec(getBookingRequestSpec)
                        .when()
                        .get("/booking")
                        .then()
                        .spec(getBookingResponseSpec)
                        .extract().as(ResponseBookingsIdsDTO[].class));

        step("Check response get all bookings", () ->
                assertThat(response[0].getBookingid()).isNotNull());
    }

    @Test
    @Tag("GetBookings")
    @Tag("PositiveTests")
    @DisplayName("Returns bookings by full name")
    @Description("Returns the ids of the bookings by full name")
    public void shouldReturnAllUserBookingsWhenGetBookingsByFilterFullName() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        Integer numbersOfBookings = bookingDataGenerator.getNumberOfBookings();

        List<BookingDTO> bookingsOneUser = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        UsefulMethodsForTests usefulMethod = new UsefulMethodsForTests();

        try {
            for (int i = 0; i < numbersOfBookings; i++) {
                bookingsOneUser.add(bookingDataGenerator.generateDataBookingForOneUser());
            }

            step("Create a new booking for test", () -> {
                for (BookingDTO element : bookingsOneUser) {
                    usefulMethod.createNewBooking(element);
                }
            });

            ResponseBookingsIdsDTO[] response = step("Make request get bookings by first name", () ->
                    given()
                            .spec(getBookingRequestSpec)
                            .queryParam("firstname", bookingsOneUser.getFirst().getFirstname())
                            .queryParam("lastname", bookingsOneUser.getFirst().getLastname())
                            .when()
                            .get("/booking")
                            .then()
                            .spec(getBookingResponseSpec)
                            .extract().as(ResponseBookingsIdsDTO[].class));

            step("Check response get bookings by first name", () -> {
                assertThat(response.length).isEqualTo(numbersOfBookings);
                for (ResponseBookingsIdsDTO element : response) {
                    assertThat(element.getBookingid()).isGreaterThan(0);
                }
            });

            step("Save booking ids for deleting created bookings", () -> {
                for (ResponseBookingsIdsDTO element : response) {
                    ids.add(element.getBookingid());
                }
            });
        } finally {
            step("Clean up created test data", () -> {
                for (Integer id : ids) {
                    usefulMethod.deleteBooking(id);
                }
            });
        }
    }

    @Test
    @Tag("GetBookings")
    @Tag("PositiveTests")
    @DisplayName("Returns booking by id")
    @Description("Returns a specific booking based upon the booking id provided")
    public void shouldReturnBookingByFilterId() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        UsefulMethodsForTests usefulMethod = new UsefulMethodsForTests();
        Integer idCreatedBooking = 0;

        try {

            ResponseBookingDTO responseCreatedBooking = step("Create a new booking for test", () ->
                    given()
                            .spec(createBookingRequestSpec)
                            .body(booking)
                            .when()
                            .post("/booking")
                            .then()
                            .spec(createBookingResponseSpec)
                            .extract()
                            .as(ResponseBookingDTO.class));

            idCreatedBooking = responseCreatedBooking.getBookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO response = step("Make request get booking by id", () ->
                    given()
                            .spec(getBookingRequestSpec)
                            .when()
                            .get("/booking/" + finalId)
                            .then()
                            .spec(getBookingResponseSpec)
                            .extract().as(BookingDTO.class));

            step("Check response get booking by id", () -> {
                assertThat(response.getFirstname()).isEqualTo(booking.getFirstname());
                assertThat(response.getLastname()).isEqualTo(booking.getLastname());
                assertThat(response.getTotalprice()).isEqualTo(booking.getTotalprice());
                assertThat(response.getDepositpaid()).isEqualTo(booking.getDepositpaid());
                assertThat(response.getBookingdates().getCheckin()).isEqualTo(booking.getBookingdates().getCheckin());
                assertThat(response.getBookingdates().getCheckout()).isEqualTo(booking.getBookingdates().getCheckout());
                assertThat(response.getAdditionalneeds()).isEqualTo(booking.getAdditionalneeds());
            });

        } finally {
            Integer finalId = idCreatedBooking;
            step("Clean up created test data", () ->
                    usefulMethod.deleteBooking(finalId)
            );
        }
    }

    @Test
    @Tag("CreateBookings")
    @Tag("PositiveTests")
    @DisplayName("Creates a new booking")
    @Description("Creates a new booking in the API")
    public void shouldCreateNewBooking() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        UsefulMethodsForTests usefulMethod = new UsefulMethodsForTests();
        Integer idCreatedBooking = 0;

        try {

            ResponseBookingDTO response = step("Make request create a new booking", () ->
                    given()
                            .spec(createBookingRequestSpec)
                            .body(booking)
                            .when()
                            .post("/booking")
                            .then()
                            .spec(createBookingResponseSpec)
                            .extract()
                            .as(ResponseBookingDTO.class));

            step("Check response a new booking", () -> {
                assertThat(response.getBookingid()).isNotNull();
                assertThat(response.getBooking().getFirstname()).isEqualTo(booking.getFirstname());
                assertThat(response.getBooking().getLastname()).isEqualTo(booking.getLastname());
                assertThat(response.getBooking().getTotalprice()).isEqualTo(booking.getTotalprice());
                assertThat(response.getBooking().getDepositpaid()).isEqualTo(booking.getDepositpaid());
                assertThat(response.getBooking().getBookingdates().getCheckin()).isEqualTo(booking.getBookingdates().getCheckin());
                assertThat(response.getBooking().getBookingdates().getCheckout()).isEqualTo(booking.getBookingdates().getCheckout());
                assertThat(response.getBooking().getAdditionalneeds()).isEqualTo(booking.getAdditionalneeds());
            });

            step("Check the created booking is exist", () ->
                    usefulMethod.getBookingById(response.getBookingid())
            );

            idCreatedBooking = response.getBookingid();
        } finally {
            Integer idForDeletingBooking = idCreatedBooking;
            step("Clean up created test data", () ->
                    usefulMethod.deleteBooking(idForDeletingBooking));
        }
    }

    @Test
    @Tag("DeleteBookings")
    @Tag("PositiveTests")
    @DisplayName("Deletes a booking")
    @Description("Deletes a booking from the API. Requires an authorization token to be set in the header or a Basic auth header")
    public void shouldDeleteBooking() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        UsefulMethodsForTests usefulMethod = new UsefulMethodsForTests();

        ResponseBookingDTO response = step("Make request create a new booking", () ->
                given()
                        .spec(createBookingRequestSpec)
                        .body(booking)
                        .when()
                        .post("/booking")
                        .then()
                        .spec(createBookingResponseSpec)
                        .extract()
                        .as(ResponseBookingDTO.class));

        step("Check the created booking is exist", () ->
                usefulMethod.getBookingById(response.getBookingid())
        );

        step("Deletes a created booking", () ->
                given()
                        .spec(deleteBookingRequestSpec)
                        .when()
                        .delete("/booking/" + response.getBookingid())
                        .then()
                        .spec(deleteBookingResponseSpec));

        step("Check the deleted booking is not exist", () ->
                given()
                        .spec(getBookingRequestSpec)
                        .when()
                        .get("/booking/" + response.getBookingid())
                        .then()
                        .spec(getNotFoundBookingResponseSpec)
        );
    }

}
