package tests;

import api.clients.BookingClients;
import data.BookingDataGenerator;
import io.qameta.allure.Description;
import models.lombok.BookingDTO;
import models.lombok.ResponseBookingsIdsDTO;
import models.lombok.ResponseBookingDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;

import static org.assertj.core.api.Assertions.assertThat;

public class RestfulBookingTests extends TestBase {

    @Test
    @DisplayName("Get health check endpoint")
    @Description("A simple health check endpoint to confirm whether the API is up and running.")
    public void shouldReturnHealthCheckEndpoint() {
        BookingClients bookingClients = new BookingClients();

        step("Make a request get a health check endpoint and check response", bookingClients::getHealthCheckEndpoint);
    }

    @Test
    @Tag("PositiveTests")
    @DisplayName("Creates a new auth token")
    @Description("Creates a new auth token to use for access to the PUT and DELETE /booking")
    public void shouldReturnCreatedToken() {
        BookingClients bookingClients = new BookingClients();

        String response = step("Make a request creates a new token", bookingClients::createToken);

        step("Check a response creates a new token", () ->
                assertThat(response).isNotNull());
    }

    @Test
    @Tag("GetBookings")
    @Tag("PositiveTests")
    @DisplayName("Returns all bookings ids")
    @Description("Returns the ids of all the bookings that exist within the API")
    public void shouldReturnAllBookings() {
        BookingClients bookingClients = new BookingClients();

        ResponseBookingsIdsDTO[] response = step("Make a request get all bookings", bookingClients::getAllBookings);

        step("Check a response get all bookings", () ->
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
        BookingClients bookingClients = new BookingClients();

        try {
            for (int i = 0; i < numbersOfBookings; i++) {
                bookingsOneUser.add(bookingDataGenerator.generateDataBookingForOneUser());
            }

            step("Create a new booking for test", () -> {
                for (BookingDTO element : bookingsOneUser) {
                    bookingClients.createNewBooking(element);
                }
            });

            ResponseBookingsIdsDTO[] response = step("Make a request get bookings by full name", () ->
                    bookingClients.getBookingsByFullName(bookingsOneUser.getFirst().getFirstname(),
                            bookingsOneUser.getFirst().getLastname()));

            step("Check a response get bookings by first name", () -> {
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
                    bookingClients.deleteBooking(id);
                }
            });
        }
    }

    @Test
    @Tag("GetBookings")
    @Tag("NegativeTests")
    @DisplayName("Returns not found the bookings by full name")
    @Description("Returns not found the bookings by full name")
    public void shouldReturnNotFoundUserBookingsWhenGetBookingsByFilterFullName() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        BookingClients bookingClients = new BookingClients();

        ResponseBookingsIdsDTO[] response = step("Make a request get bookings by full name: not found", () ->
                bookingClients.getBookingsByFullName(booking.getFirstname(),
                        booking.getLastname()));

        step("Check a response get bookings by first name: not found", () -> assertThat(response).isNullOrEmpty());
    }

    @Test
    @Tag("GetBookings")
    @Tag("PositiveTests")
    @DisplayName("Returns a booking by id")
    @Description("Returns a specific booking based upon the booking id provided")
    public void shouldReturnBookingByFilterId() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        BookingClients bookingClients = new BookingClients();
        Integer idCreatedBooking = 0;

        try {

            ResponseBookingDTO responseCreatedBooking = step("Create a new booking for test", () ->
                    bookingClients.createNewBooking(booking));

            idCreatedBooking = responseCreatedBooking.getBookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO response = step("Make a request get booking by id", () ->
                    bookingClients.getBookingById(finalId));

            step("Check a response get booking by id", () -> {
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
                    bookingClients.deleteBooking(finalId)
            );
        }
    }

    @Test
    @Tag("GetBookings")
    @Tag("NegativeTests")
    @DisplayName("Returns not found booking by an unexist id")
    @Description("Returns not found booking by an unexist id")
    public void shouldReturnNotFoundBookingByFilterId() {

        BookingClients bookingClients = new BookingClients();
        Integer idUnexistBooking = 0;

        step("Make a request get booking by an unexist id and check a response not found booking",
                () -> bookingClients.notFoundGetBookingById(idUnexistBooking));

    }

    @Test
    @Tag("CreateBookings")
    @Tag("PositiveTests")
    @DisplayName("Creates a new booking")
    @Description("Creates a new booking in the API")
    public void shouldCreateNewBooking() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        BookingClients bookingClients = new BookingClients();
        Integer idCreatedBooking = 0;

        try {

            ResponseBookingDTO response = step("Make a request create a new booking", () ->
                    bookingClients.createNewBooking(booking));

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
                    bookingClients.getBookingById(response.getBookingid())
            );

            idCreatedBooking = response.getBookingid();
        } finally {
            Integer idForDeletingBooking = idCreatedBooking;
            step("Clean up created test data", () ->
                    bookingClients.deleteBooking(idForDeletingBooking));
        }
    }

    @Test
    @Tag("UpdateBookings")
    @Tag("PositiveTests")
    @DisplayName("Full updates a current booking")
    @Description("Full updates a current booking")
    public void shouldFullUpdateBooking() {

        BookingDataGenerator bookingDataInitial = new BookingDataGenerator();
        BookingDataGenerator bookingDataUpdated = new BookingDataGenerator();
        BookingDTO bookingInitial = bookingDataInitial.generateDataForBooking();
        BookingDTO bookingUpdated = bookingDataUpdated.generateDataForBooking();
        BookingClients bookingClients = new BookingClients();
        Integer idCreatedBooking = 0;

        try {

            ResponseBookingDTO responseCreatedBooking = step("Create a new booking for test", () ->
                    bookingClients.createNewBooking(bookingInitial));

            idCreatedBooking = responseCreatedBooking.getBookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO responseUpdatedBooking = step("Full updates a booking", () ->
                    bookingClients.fullUpdatesBooking(bookingUpdated, finalId));

            step("Check response full update a booking", () -> {
                assertThat(responseUpdatedBooking.getFirstname()).isEqualTo(bookingUpdated.getFirstname());
                assertThat(responseUpdatedBooking.getLastname()).isEqualTo(bookingUpdated.getLastname());
                assertThat(responseUpdatedBooking.getTotalprice()).isEqualTo(bookingUpdated.getTotalprice());
                assertThat(responseUpdatedBooking.getDepositpaid()).isEqualTo(bookingUpdated.getDepositpaid());
                assertThat(responseUpdatedBooking.getBookingdates().getCheckin()).isEqualTo(bookingUpdated.getBookingdates().getCheckin());
                assertThat(responseUpdatedBooking.getBookingdates().getCheckout()).isEqualTo(bookingUpdated.getBookingdates().getCheckout());
                assertThat(responseUpdatedBooking.getAdditionalneeds()).isEqualTo(bookingUpdated.getAdditionalneeds());
            });

            BookingDTO getResponse = step("Make a request get updated booking by id", () ->
                    bookingClients.getBookingById(finalId));

            step("Check current values response get updated booking by id", () -> {
                assertThat(getResponse.getFirstname()).isEqualTo(bookingUpdated.getFirstname());
                assertThat(getResponse.getLastname()).isEqualTo(bookingUpdated.getLastname());
                assertThat(getResponse.getTotalprice()).isEqualTo(bookingUpdated.getTotalprice());
                assertThat(getResponse.getDepositpaid()).isEqualTo(bookingUpdated.getDepositpaid());
                assertThat(getResponse.getBookingdates().getCheckin()).isEqualTo(bookingUpdated.getBookingdates().getCheckin());
                assertThat(getResponse.getBookingdates().getCheckout()).isEqualTo(bookingUpdated.getBookingdates().getCheckout());
                assertThat(getResponse.getAdditionalneeds()).isEqualTo(bookingUpdated.getAdditionalneeds());
            });

        } finally {
            Integer finalId = idCreatedBooking;
            step("Clean up created test data", () ->
                    bookingClients.deleteBooking(finalId)
            );
        }
    }

    @Test
    @Tag("UpdateBookings")
    @Tag("PositiveTests")
    @DisplayName("Partial updates a current booking by price and booking dates")
    @Description("Updates a current booking with a partial payload: price and booking dates")
    public void shouldPartialUpdatePriceAndDatesBooking() {

        BookingDataGenerator bookingDataInitial = new BookingDataGenerator();
        BookingDataGenerator bookingDataPatch = new BookingDataGenerator();
        BookingDTO bookingInitial = bookingDataInitial.generateDataForBooking();
        BookingDTO bookingPatch = bookingDataPatch.partialUpdateBookingByPriceAndBookingDates();
        BookingClients bookingClients = new BookingClients();
        Integer idCreatedBooking = 0;

        try {

            ResponseBookingDTO responseCreatedBooking = step("Create a new booking for test", () ->
                    bookingClients.createNewBooking(bookingInitial));

            idCreatedBooking = responseCreatedBooking.getBookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO responseUpdatedBooking = step("Partial updates a booking", () ->
                    bookingClients.partialUpdatesBooking(bookingPatch, finalId));

            step("Check response partial update a booking", () -> {
                assertThat(responseUpdatedBooking.getFirstname()).isEqualTo(bookingInitial.getFirstname());
                assertThat(responseUpdatedBooking.getLastname()).isEqualTo(bookingInitial.getLastname());
                assertThat(responseUpdatedBooking.getTotalprice()).isEqualTo(bookingPatch.getTotalprice());
                assertThat(responseUpdatedBooking.getDepositpaid()).isEqualTo(bookingInitial.getDepositpaid());
                assertThat(responseUpdatedBooking.getBookingdates().getCheckin()).isEqualTo(bookingPatch.getBookingdates().getCheckin());
                assertThat(responseUpdatedBooking.getBookingdates().getCheckout()).isEqualTo(bookingPatch.getBookingdates().getCheckout());
                assertThat(responseUpdatedBooking.getAdditionalneeds()).isEqualTo(bookingInitial.getAdditionalneeds());
            });

            BookingDTO getResponse = step("Make a request get updated booking by id", () ->
                    bookingClients.getBookingById(finalId));

            step("Check current values response get updated booking by id", () -> {
                assertThat(getResponse.getFirstname()).isEqualTo(bookingInitial.getFirstname());
                assertThat(getResponse.getLastname()).isEqualTo(bookingInitial.getLastname());
                assertThat(getResponse.getTotalprice()).isEqualTo(bookingPatch.getTotalprice());
                assertThat(getResponse.getDepositpaid()).isEqualTo(bookingInitial.getDepositpaid());
                assertThat(getResponse.getBookingdates().getCheckin()).isEqualTo(bookingPatch.getBookingdates().getCheckin());
                assertThat(getResponse.getBookingdates().getCheckout()).isEqualTo(bookingPatch.getBookingdates().getCheckout());
                assertThat(getResponse.getAdditionalneeds()).isEqualTo(bookingInitial.getAdditionalneeds());
            });

        } finally {
            Integer finalId = idCreatedBooking;
            step("Clean up created test data", () ->
                    bookingClients.deleteBooking(finalId)
            );
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
        BookingClients bookingClients = new BookingClients();

        ResponseBookingDTO response = step("Make a request create a new booking", () ->
                bookingClients.createNewBooking(booking));

        step("Check the created booking is exist", () ->
                bookingClients.getBookingById(response.getBookingid())
        );

        step("Deletes a created booking", () ->
                bookingClients.deleteBooking(response.getBookingid()));

        step("Check the deleted booking is not exist", () ->
                bookingClients.notFoundGetBookingById(response.getBookingid())
        );
    }

}
