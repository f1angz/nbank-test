package requests.steps;

import generators.EntityGenerator;
import models.CreateUserRq;
import models.CreateUserRs;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatedCrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {

    public static CreateUserRq createUser() {
        CreateUserRq userRequest = EntityGenerator.generate(CreateUserRq.class);

        new ValidatedCrudRequesters<CreateUserRs>(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated(),
                Endpoint.ADMIN_USER)
                .post(userRequest);

        return userRequest;
    }
}
