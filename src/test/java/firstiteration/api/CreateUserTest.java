package firstiteration.api;

import generators.EntityGenerator;
import models.CreateUserRq;
import models.CreateUserRs;
import models.comparison.JsonComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import requests.skeleton.requests.ValidatedCrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;
import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectData() {
        CreateUserRq createUserRq = EntityGenerator.generate(CreateUserRq.class);

        CreateUserRs createUserRs = new ValidatedCrudRequesters<CreateUserRs>(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated(),
                Endpoint.ADMIN_USER)
                .post(createUserRq);

        JsonComparator comparator = new JsonComparator();
        comparator.assertMatches(createUserRq, createUserRs);
    }

    @ParameterizedTest
    @MethodSource("userInvalidDataProvider")
    public void adminCanCreateUserWithInvalidData(String username, String password, String role, String errorKey, List<String> errorText) {
        CreateUserRq createUserRq = CreateUserRq.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new CrudRequesters(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorText),
                Endpoint.ADMIN_USER)
                .post(createUserRq);
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
