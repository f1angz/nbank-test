package generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomData {
    private RandomData() {}

    public static String getUsername() {
        return RandomStringUtils.secure().nextAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.secure().nextAlphabetic(3).toUpperCase() +
                RandomStringUtils.secure().nextNumeric(5).toLowerCase() +
                RandomStringUtils.secure().nextAlphanumeric(3).toLowerCase() + "!%$";
    }
}
