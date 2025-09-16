package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlerts {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully", false),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("username: Username must be between 3 and 15 characters", false),
    NEW_ACCOUNT_CREATED("^✅ New Account Created! Account Number: ACC\\d+$", true),
    DEPOSIT_SUCCESSFULLY("^✅ Successfully deposited \\$\\d+(\\.\\d+)? to account ACC\\d+!$", true),
    ENTER_VALID_AMOUNT("❌ Please enter a valid amount.", false),
    TRANSFER_SUCCESSFULLY("^✅ Successfully transferred \\$\\d+(\\.\\d+)? to account ACC\\d+!$", true),
    TRANSFER_ERROR("❌ Error: Invalid transfer: insufficient funds or invalid accounts", false),
    UPDATE_NAME("✅ Name updated successfully!", false),
    UPDATE_NAME_ERROR("Name must contain two words with letters only", false);

    private final String message;
    private final boolean regex;

    BankAlerts(String message, boolean regex) {
        this.message = message;
        this.regex = regex;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRegex() {
        return regex;
    }
}
