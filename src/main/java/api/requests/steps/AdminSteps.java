package api.requests.steps;

import api.generators.EntityGenerator;
import api.models.CreateUserRq;
import api.models.CreateUserRs;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.sql.SQLOutput;
import java.util.List;

public class AdminSteps {

    public static CreateUserRq createUser() {
        CreateUserRq userRequest = EntityGenerator.generate(CreateUserRq.class);

        return StepLogger.log("Админ создаёт пользователя " + userRequest.getUsername(), () -> {
            new ValidatedCrudRequesters<CreateUserRs>(
                    RequestSpecs.adminSpec(),
                    ResponseSpecs.entityWasCreated(),
                    Endpoint.ADMIN_USER)
                    .post(userRequest);

            return userRequest;
        });
    }

    public static List<CreateUserRs> getAllUsers() {
        return StepLogger.log("Админ получает всех пользователей", () -> {
            var response = new ValidatedCrudRequesters<CreateUserRs>(RequestSpecs.adminSpec(),
                    ResponseSpecs.requestReturnsOK(),
                    Endpoint.ADMIN_USER).getAll(CreateUserRs[].class);
            return response;
        });
    }
}
