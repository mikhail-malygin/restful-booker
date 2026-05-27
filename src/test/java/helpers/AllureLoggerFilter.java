package helpers;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class AllureLoggerFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        Response response = ctx.next(requestSpec, responseSpec);

        String requestLog = String.format("METHOD: %s\nURL: %s\nBODY:\n%s",
                requestSpec.getMethod(), requestSpec.getURI(), requestSpec.getBody());

        String responseLog = String.format("STATUS: %d\nBODY:\n%s",
                response.getStatusCode(), response.asPrettyString());

        Allure.addAttachment("HTTP Request", "text/plain", requestLog, ".txt");
        Allure.addAttachment("HTTP Response", "text/plain", responseLog, ".txt");

        return response;
    }
}
