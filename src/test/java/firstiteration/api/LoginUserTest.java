package firstiteration.api;

import api.models.CreateUserRq;
import api.models.CreateUserRs;
import api.models.LoginUserRq;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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
