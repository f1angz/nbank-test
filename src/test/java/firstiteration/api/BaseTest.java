package firstiteration.api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions softly;
    protected static final String UNAUTH_ACCESS = "Unauthorized access to account";
    protected static final String INVALID_ACCOUNT = "Invalid account or amount";
    protected static final String INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    protected static final String EDIT_NAME_ERROR = "Name must contain two words with letters only";
    protected static final String SUCCESS_EDIT = "Profile updated successfully";
    protected static final String SUCCESS_TRANSFER = "Transfer successful";

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
    }
}
