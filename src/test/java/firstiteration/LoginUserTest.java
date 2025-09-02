package firstiteration;

import generators.EntityGenerator;
import generators.RandomData;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import models.CreateUserRq;
import models.CreateUserRs;
import models.LoginUserRq;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import requests.skeleton.requests.ValidatedCrudRequesters;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class LoginUserTest extends BaseTest {

    @BeforeAll
    public static void setUpRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRq userRequest = LoginUserRq.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatedCrudRequesters<CreateUserRs>(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.LOGIN);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRq userRequest = AdminSteps.createUser();

        new CrudRequesters(RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.LOGIN)
                .post(LoginUserRq.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build())
                .header("Authorization", Matchers.notNullValue());
    }
}
