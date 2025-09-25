package api.specs;

import api.configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.LoginUserRq;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private static Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("api.BaseUrl") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        String token = getUserAuthHeader(username, password);
        return defaultRequestBuilder()
                .addHeader("Authorization", getUserAuthHeader(username, password))
                .build();
    }

    public static String getUserAuthHeader(String username, String password) {
        String authToken;
        if(!authHeaders.containsKey(username)) {
            authToken = new CrudRequesters(
                    RequestSpecs.unauthSpec(),
                    ResponseSpecs.requestReturnsOK(),
                    Endpoint.LOGIN)
                    .post(LoginUserRq.builder().username(username).password(password).build())
                    .extract()
                    .header("Authorization");
            authHeaders.put(username, authToken);
        } else {
            authToken = authHeaders.get(username);
        }
        return authToken;
    }
}
