package restfulBooker;

import io.qameta.allure.Description;
import models.lombok.RequestBodyCreateBooking;
import models.lombok.ResponseBodyBookings;
import models.lombok.ResponseBodyCreateBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.BookingSpecs.getBookingRequestSpec;
import static specs.BookingSpecs.getBookingResponseSpec;
import static specs.CreateBookingSpecs.createBookingRequestSpec;
import static specs.CreateBookingSpecs.createBookingResponseSpec;

public class RestfulBookingTests extends TestBase {

    @Test
    @DisplayName("Get all bookings ids")
    @Description("Returns the ids of all the bookings that exist within the API")
    public void checkNotNullValueBodyResponseWhenMakeGetAllBookingsRequest() {

        ResponseBodyBookings[] response = step("Make request", () ->
                given()
                        .spec(getBookingRequestSpec)
                        .when()
                        .get("/booking")
                        .then()
                        .spec(getBookingResponseSpec)
                        .extract().as(ResponseBodyBookings[].class));

        step("Check response", () ->
                assertThat(response[0].getBookingid()).isNotNull());
    }


    @Test
    @DisplayName("Creates a new booking")
    @Description("Creates a new booking in the API")
    public void checkValuesWhenCreatedBooking() {

        RequestBodyCreateBooking newBooking = RequestBodyCreateBooking.builder()
                .firstname("Test")
                .lastname("Test")
                .totalprice(123)
                .depositpaid(true)
                .additionalneeds("Breakfast")
                .bookingdates(RequestBodyCreateBooking.Bookingdates.builder()
                        .checkin("2030-05-30")
                        .checkout("2030-06-03")
                        .build())
                .build();

        ResponseBodyCreateBooking response = step("Make request", () ->
                given()
                        .spec(createBookingRequestSpec)
                        .body(newBooking)
                        .when()
                        .post("/booking")
                        .then()
                        .spec(createBookingResponseSpec)
                        .extract()
                        .as(ResponseBodyCreateBooking.class));

        step("Check response", () -> {
            assertThat(response.getBookingid()).isNotNull();
            assertEquals("Test", response.getBooking().getFirstname());
            assertEquals("Test", response.getBooking().getLastname());
            assertEquals(123, response.getBooking().getTotalprice());
            assertEquals(true, response.getBooking().getDepositpaid());
            assertEquals("2030-05-30", response.getBooking().getBookingdates().getCheckin());
            assertEquals("2030-06-03", response.getBooking().getBookingdates().getCheckout());
            assertEquals("Breakfast", response.getBooking().getAdditionalneeds());
        });
    }

}
