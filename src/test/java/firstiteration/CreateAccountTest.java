package firstiteration;

import models.CreateUserRq;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatedCrudRequesters;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRq user = AdminSteps.createUser();

        new ValidatedCrudRequesters<>(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.entityWasCreated(),
                Endpoint.ACCOUNTS)
                .post(null);
    }
}
