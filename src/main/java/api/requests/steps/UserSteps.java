package api.requests.steps;

import api.models.CreateAccountRs;
import api.models.CreateUserRs;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatedCrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<CreateAccountRs> getAllAccounts() {
        return new ValidatedCrudRequesters<CreateAccountRs>(
                RequestSpecs.authAsUser(username, password),
                ResponseSpecs.requestReturnsOK(),
                Endpoint.CUSTOMER_ACCOUNTS
        ).getAll(CreateAccountRs[].class);
    }
}
