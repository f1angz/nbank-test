package firstiteration;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest {

    @BeforeAll
    public static void setUpRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @Test
    public void adminCanCreateUserWithCorrectData() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                            "username": "Semyon1234",
                            "password": "Qwerty123!",
                            "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("Semyon1234"))
                .body("password", Matchers.not(Matchers.equalTo("Qwerty123!")))
                .body("role", Matchers.equalTo("USER"));
    }

    @ParameterizedTest
    @MethodSource("userInvalidDataProvider")
    public void adminCanCreateUserWithInvalidData(String username, String password, String role, String errorKey, List<String> errorText) {
        String requestBody = String.format(
                """
                        {
                            "username": "%s",
                            "password": "%s",
                            "role": "%s"
                        }
                """, username, password, role
        );
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(errorKey, Matchers.containsInAnyOrder(errorText.toArray(new String[0])));
    }

    public static Stream<Arguments> userInvalidDataProvider() {
        return Stream.of(
                //Username validation
                Arguments.of(" ", "Password33$", "USER", "username", List.of("Username must contain only letters, digits, dashes, underscores, and dots", "Username must be between 3 and 15 characters",  "Username cannot be blank")),
                Arguments.of("Se", "Password33$", "USER", "username", List.of("Username must be between 3 and 15 characters")),
                Arguments.of("Abc$", "Password33$", "USER", "username", List.of("Username must contain only letters, digits, dashes, underscores, and dots")),
                Arguments.of("A bc", "Password33$", "USER", "username", List.of("Username must contain only letters, digits, dashes, underscores, and dots"))
                //Password validation
                
        );
    }
}
