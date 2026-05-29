package api.usefulMethods;

import data.TokenData;
import models.lombok.RequestBookingDTO;
import models.lombok.ResponseBookingDTO;
import models.lombok.ResponseTokenDTO;

import static api.specs.BookingSpecs.deleteBookingRequestSpec;
import static api.specs.BookingSpecs.deleteBookingResponseSpec;
import static api.specs.TokenSpecs.getTokenRequestSpec;
import static api.specs.TokenSpecs.getTokenResponseSpec;
import static io.restassured.RestAssured.given;
import static api.specs.CreateBookingSpecs.createBookingRequestSpec;
import static api.specs.CreateBookingSpecs.createBookingResponseSpec;

public class UsefulMethodsForTests {

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

    public void createNewBooking(RequestBookingDTO booking) {

        given()
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
                .spec(deleteBookingRequestSpec)
                .when()
                .delete("/booking/" + id)
                .then()
                .spec(deleteBookingResponseSpec);
    }
}

