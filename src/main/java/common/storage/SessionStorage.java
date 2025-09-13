package common.storage;

import api.models.CreateUserRq;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
    private static final SessionStorage INSTANCE =  new SessionStorage();
    private final LinkedHashMap<CreateUserRq, UserSteps> userStepsMap = new LinkedHashMap<>();

    private SessionStorage() {}

    public static void addUsers(List<CreateUserRq> users) {
        for (CreateUserRq user : users) {
            INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getUsername()));
        }
    }

    public static CreateUserRq getUser(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet()).get(number - 1);
    }

    public static CreateUserRq getUser() {
        return getUser(1);
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.values()).get(number - 1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear() {
        INSTANCE.userStepsMap.clear();
    }
}
