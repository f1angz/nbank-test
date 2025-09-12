package firstiteration.api;

import models.CreateUserRq;
import models.CreateUserRs;
import models.LoginUserRq;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import requests.skeleton.requests.ValidatedCrudRequesters;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

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
