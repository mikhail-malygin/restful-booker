package tests;

import config.AuthConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    protected static final AuthConfig config = ConfigFactory.create(AuthConfig.class);

    @BeforeAll
    protected static void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }
}
