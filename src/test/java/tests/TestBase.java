package tests;

import api.clients.BookingClients;
import config.AuthConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    protected static final AuthConfig config = ConfigFactory.create(AuthConfig.class);
    private static String token = null;

    @BeforeAll
    protected static void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    protected static synchronized String getToken() {
        if (token == null) {
            token = new BookingClients().createToken();
        }

        return token;
    }
}
