package firstiteration;

import generators.EntityGenerator;
import models.CreateUserRq;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatedCrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRq userRequest = EntityGenerator.generate(CreateUserRq.class);

        new ValidatedCrudRequesters<>(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated(),
                Endpoint.ADMIN_USER)
                .post(userRequest);

        new ValidatedCrudRequesters<>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated(),
                Endpoint.ACCOUNTS)
                .post(null);
    }
}
