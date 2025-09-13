package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.util.List;

public class ResponseSpecs {
    private ResponseSpecs() {}

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsUnauth() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, List<String> errorText) {
        return defaultResponseBuilder().
                expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey,  Matchers.containsInAnyOrder(errorText.toArray(new String[0])))
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey) {
        return defaultResponseBuilder().
                expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.equalTo(errorKey))
                .build();
    }

    public static ResponseSpecification requestReturnsForbidden(String errorKey) {
        return defaultResponseBuilder().
                expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.equalTo(errorKey))
                .build();
    }

}
