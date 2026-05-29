package tests;

import api.usefulMethods.UsefulMethodsForTests;
import data.BookingDataGenerator;
import io.qameta.allure.Description;
import models.lombok.RequestBookingDTO;
import models.lombok.ResponseBookingsIdsDTO;
import models.lombok.ResponseBookingDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static api.specs.BookingSpecs.getBookingRequestSpec;
import static api.specs.BookingSpecs.getBookingResponseSpec;
import static api.specs.CreateBookingSpecs.createBookingRequestSpec;
import static api.specs.CreateBookingSpecs.createBookingResponseSpec;

public class RestfulBookingTests extends TestBase {

    @Test
    @Tag("GetBookings")
    @Tag("PositiveTests")
    @DisplayName("Get all bookings ids")
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
    @DisplayName("Get bookings by full name")
    @Description("Returns the ids of the bookings by full name")
    public void shouldReturnAllUserBookingsWhenGetBookingsByFilterFullName() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        Integer numbersOfBookings = bookingDataGenerator.getNumberOfBookings();

        List<RequestBookingDTO> bookingsOneUser = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        UsefulMethodsForTests usefulMethod = new UsefulMethodsForTests();

        try {
            for (int i = 0; i < numbersOfBookings; i++) {
                bookingsOneUser.add(bookingDataGenerator.generateDataBookingForOneUser());
            }

            step("Create a new booking for test", () -> {
                for (RequestBookingDTO element : bookingsOneUser) {
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
            for (Integer id : ids) {
                usefulMethod.deleteBooking(id);
            }
        }

    }


    @Test
    @Tag("CreateBookings")
    @Tag("PositiveTests")
    @DisplayName("Creates a new booking")
    @Description("Creates a new booking in the API")
    public void shouldCreateNewBooking() {
        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        RequestBookingDTO booking = bookingDataGenerator.generateDataForBooking();
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

            idCreatedBooking = response.getBookingid();
        } finally {
            usefulMethod.deleteBooking(idCreatedBooking);
        }
    }

}
