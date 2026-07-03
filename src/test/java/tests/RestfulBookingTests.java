package tests;

import api.clients.BookingClients;
import data.BookingDataGenerator;
import io.qameta.allure.Description;
import models.BookingDTO;
import models.ResponseBookingDTO;
import models.ResponseBookingsIdsDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;

import static org.assertj.core.api.Assertions.assertThat;

public class RestfulBookingTests extends TestBase {

    @Test
    @DisplayName("Gets health check endpoint")
    @Description("A simple health check endpoint to confirm whether the API is up and running.")
    public void shouldReturnHealthCheckEndpoint() {
        BookingClients bookingClients = new BookingClients();

        step("Make a request get a health check endpoint and check response", bookingClients::getHealthCheckEndpoint);
    }

    @Test
    @Tag("PositiveTests")
    @DisplayName("Creates a new auth token with valid credentials")
    @Description("Creates a new auth token to use for access to the PUT and DELETE /booking")
    public void shouldReturnCreatedToken() {
        BookingClients bookingClients = new BookingClients();

        String response = step("Make a request creates a new token", bookingClients::createToken);

        step("Check a response creates a new token", () ->
                assertThat(response).isNotNull());
    }

    @Test
    @Tag("NegativeTests")
    @DisplayName("Gets bad credentials error when trying to create a token with an incorrect password")
    @Description("Gets bad credentials error when trying to create a token with an incorrect password")
    public void shouldReturnBadCredentialsWhenTryCreateTokenWithIncorrectPassword() {
        BookingClients bookingClients = new BookingClients();

        String response = step("Make a request creates a new token with wrong password",
                bookingClients::tryCreateTokenWithWrongPassword);

        step("Check a response creates a new token with wrong password", () ->
                assertThat(response).isEqualTo("Bad credentials"));

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
                assertThat(response[0].bookingid()).isNotNull());
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
                    bookingClients.getBookingsByFullName(bookingsOneUser.getFirst().firstname(),
                            bookingsOneUser.getFirst().lastname()));

            step("Check a response get bookings by first name", () -> {
                assertThat(response.length).isEqualTo(numbersOfBookings);
                for (ResponseBookingsIdsDTO element : response) {
                    assertThat(element.bookingid()).isGreaterThan(0);
                }
            });

            step("Save booking ids for deleting created bookings", () -> {
                for (ResponseBookingsIdsDTO element : response) {
                    ids.add(element.bookingid());
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
                bookingClients.getBookingsByFullName(booking.firstname(),
                        booking.lastname()));

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

            idCreatedBooking = responseCreatedBooking.bookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO response = step("Make a request get booking by id", () ->
                    bookingClients.getBookingById(finalId));

            step("Check a response get booking by id", () -> {
                assertThat(response.firstname()).isEqualTo(booking.firstname());
                assertThat(response.lastname()).isEqualTo(booking.lastname());
                assertThat(response.totalprice()).isEqualTo(booking.totalprice());
                assertThat(response.depositpaid()).isEqualTo(booking.depositpaid());
                assertThat(response.bookingdates().checkin()).isEqualTo(booking.bookingdates().checkin());
                assertThat(response.bookingdates().checkout()).isEqualTo(booking.bookingdates().checkout());
                assertThat(response.additionalneeds()).isEqualTo(booking.additionalneeds());
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
                assertThat(response.bookingid()).isNotNull();
                assertThat(response.booking().firstname()).isEqualTo(booking.firstname());
                assertThat(response.booking().lastname()).isEqualTo(booking.lastname());
                assertThat(response.booking().totalprice()).isEqualTo(booking.totalprice());
                assertThat(response.booking().depositpaid()).isEqualTo(booking.depositpaid());
                assertThat(response.booking().bookingdates().checkin()).isEqualTo(booking.bookingdates().checkin());
                assertThat(response.booking().bookingdates().checkout()).isEqualTo(booking.bookingdates().checkout());
                assertThat(response.booking().additionalneeds()).isEqualTo(booking.additionalneeds());
            });

            step("Check the created booking is exist", () ->
                    bookingClients.getBookingById(response.bookingid())
            );

            idCreatedBooking = response.bookingid();
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

            idCreatedBooking = responseCreatedBooking.bookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO responseUpdatedBooking = step("Full updates a booking", () ->
                    bookingClients.fullUpdatesBooking(bookingUpdated, finalId));

            step("Check response full update a booking", () -> {
                assertThat(responseUpdatedBooking.firstname()).isEqualTo(bookingUpdated.firstname());
                assertThat(responseUpdatedBooking.lastname()).isEqualTo(bookingUpdated.lastname());
                assertThat(responseUpdatedBooking.totalprice()).isEqualTo(bookingUpdated.totalprice());
                assertThat(responseUpdatedBooking.depositpaid()).isEqualTo(bookingUpdated.depositpaid());
                assertThat(responseUpdatedBooking.bookingdates().checkin()).isEqualTo(bookingUpdated.bookingdates().checkin());
                assertThat(responseUpdatedBooking.bookingdates().checkout()).isEqualTo(bookingUpdated.bookingdates().checkout());
                assertThat(responseUpdatedBooking.additionalneeds()).isEqualTo(bookingUpdated.additionalneeds());
            });

            BookingDTO getResponse = step("Make a request get updated booking by id", () ->
                    bookingClients.getBookingById(finalId));

            step("Check current values response get updated booking by id", () -> {
                assertThat(getResponse.firstname()).isEqualTo(bookingUpdated.firstname());
                assertThat(getResponse.lastname()).isEqualTo(bookingUpdated.lastname());
                assertThat(getResponse.totalprice()).isEqualTo(bookingUpdated.totalprice());
                assertThat(getResponse.depositpaid()).isEqualTo(bookingUpdated.depositpaid());
                assertThat(getResponse.bookingdates().checkin()).isEqualTo(bookingUpdated.bookingdates().checkin());
                assertThat(getResponse.bookingdates().checkout()).isEqualTo(bookingUpdated.bookingdates().checkout());
                assertThat(getResponse.additionalneeds()).isEqualTo(bookingUpdated.additionalneeds());
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

            idCreatedBooking = responseCreatedBooking.bookingid();
            Integer finalId = idCreatedBooking;

            BookingDTO responseUpdatedBooking = step("Partial updates a booking", () ->
                    bookingClients.partialUpdatesBooking(bookingPatch, finalId));

            step("Check response partial update a booking", () -> {
                assertThat(responseUpdatedBooking.firstname()).isEqualTo(bookingInitial.firstname());
                assertThat(responseUpdatedBooking.lastname()).isEqualTo(bookingInitial.lastname());
                assertThat(responseUpdatedBooking.totalprice()).isEqualTo(bookingPatch.totalprice());
                assertThat(responseUpdatedBooking.depositpaid()).isEqualTo(bookingInitial.depositpaid());
                assertThat(responseUpdatedBooking.bookingdates().checkin()).isEqualTo(bookingPatch.bookingdates().checkin());
                assertThat(responseUpdatedBooking.bookingdates().checkout()).isEqualTo(bookingPatch.bookingdates().checkout());
                assertThat(responseUpdatedBooking.additionalneeds()).isEqualTo(bookingInitial.additionalneeds());
            });

            BookingDTO getResponse = step("Make a request get updated booking by id", () ->
                    bookingClients.getBookingById(finalId));

            step("Check current values response get updated booking by id", () -> {
                assertThat(getResponse.firstname()).isEqualTo(bookingInitial.firstname());
                assertThat(getResponse.lastname()).isEqualTo(bookingInitial.lastname());
                assertThat(getResponse.totalprice()).isEqualTo(bookingPatch.totalprice());
                assertThat(getResponse.depositpaid()).isEqualTo(bookingInitial.depositpaid());
                assertThat(getResponse.bookingdates().checkin()).isEqualTo(bookingPatch.bookingdates().checkin());
                assertThat(getResponse.bookingdates().checkout()).isEqualTo(bookingPatch.bookingdates().checkout());
                assertThat(getResponse.additionalneeds()).isEqualTo(bookingInitial.additionalneeds());
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
                bookingClients.getBookingById(response.bookingid())
        );

        step("Deletes a created booking", () ->
                bookingClients.deleteBooking(response.bookingid()));

        step("Check the deleted booking is not exist", () ->
                bookingClients.notFoundGetBookingById(response.bookingid())
        );
    }

    @Test
    @Tag("DeleteBookings")
    @Tag("NegativeTests")
    @DisplayName("Try to delete bookings without an auth token")
    @Description("Try to delete bookings without an auth token")
    public void shouldForbidDeleteBookingWithoutAuthToken() {

        BookingDataGenerator bookingDataGenerator = new BookingDataGenerator();
        BookingDTO booking = bookingDataGenerator.generateDataForBooking();
        BookingClients bookingClients = new BookingClients();
        Integer idCreatedBooking = 0;

        try {
            ResponseBookingDTO response = step("Make a request create a new booking", () ->
                    bookingClients.createNewBooking(booking));

            idCreatedBooking = response.bookingid();

            step("Check the created booking is exist", () ->
                    bookingClients.getBookingById(response.bookingid())
            );

            step("Try to delete a created booking without a necessary auth token", () ->
                    bookingClients.deleteBookingWithoutAuthToken(response.bookingid()));

        } finally {
            Integer finalId = idCreatedBooking;
            step("Clean up created test data", () ->
                    bookingClients.deleteBooking(finalId)
            );
        }
    }

}
