package secondIteration;

import firstiteration.BaseTest;
import generators.EntityGenerator;
import models.CreateUserRq;
import models.UpdateProfileRq;
import models.UpdateProfileRs;
import models.comparison.JsonComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequesters;
import requests.skeleton.requests.ValidatedCrudRequesters;
import requests.steps.AdminSteps;
import requests.steps.ProfileStep;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateProfileTest extends BaseTest {
    private final JsonComparator comparator = new JsonComparator();

    @Test
    public void authUserCanUpdateName() {
        CreateUserRq user = AdminSteps.createUser();

        UpdateProfileRq updateRequest = EntityGenerator.generate(UpdateProfileRq.class);

        UpdateProfileRs response = new ValidatedCrudRequesters<UpdateProfileRs>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.UPDATE_PROFILE)
                .put(updateRequest);

        comparator.assertMatches(updateRequest, response);
        assertThat(response.getMessage()).isEqualTo(ProfileStep.SUCCESS_MESSAGE);
    }

    @Test
    public void unauthUserCannotUpdateProfile() {
        UpdateProfileRq updateRequest = EntityGenerator.generate(UpdateProfileRq.class);

        new CrudRequesters(RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsUnauth(),
                Endpoint.UPDATE_PROFILE)
                .put(updateRequest);
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void authUserCannotUpdateWithInvalidName(String invalidName) {
        CreateUserRq user = AdminSteps.createUser();
        UpdateProfileRq updateRequest = UpdateProfileRq.builder().name(invalidName).build();

        new CrudRequesters(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(ProfileStep.ERROR_MESSAGE),
                Endpoint.UPDATE_PROFILE)
                .put(updateRequest);
    }

    public static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("abcd"),
                Arguments.of("1234"),
                Arguments.of("$£*&&"),
                Arguments.of("Abcd 1234"),
                Arguments.of("Abcd $£*&&"),
                Arguments.of("Abcd Abcd Abcd")
        );
    }
}
