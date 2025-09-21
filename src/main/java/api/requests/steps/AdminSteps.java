package api.requests.steps;

import api.generators.EntityGenerator;
import api.models.CreateUserRq;
import api.models.CreateUserRs;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.sql.SQLOutput;
import java.util.List;

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

    public static List<CreateUserRs> getAllUsers() {
        var response = new ValidatedCrudRequesters<CreateUserRs>(RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.ADMIN_USER).getAll(CreateUserRs[].class);
        return response;
    }
}
