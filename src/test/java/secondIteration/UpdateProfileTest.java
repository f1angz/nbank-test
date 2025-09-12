package secondIteration;

import firstiteration.api.BaseTest;
import api.generators.EntityGenerator;
import api.models.CreateUserRq;
import api.models.UpdateProfileRq;
import api.models.UpdateProfileRs;
import api.models.comparison.JsonComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequesters;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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
        assertThat(response.getMessage()).isEqualTo(SUCCESS_EDIT);
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
                ResponseSpecs.requestReturnsBadRequest(EDIT_NAME_ERROR),
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
