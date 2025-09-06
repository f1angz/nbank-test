package requests.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRq.class,
            CreateUserRs.class
    ),
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountRs.class
    ),
    LOGIN(
            "/auth/login",
            LoginUserRq.class,
            LoginUserRs.class
    ),
    DEPOSIT(
            "/accounts/deposit",
            DepositMoneyRq.class,
            DepositMoneyRs.class
    ),
    TRANSFER(
            "accounts/transfer",
            TransferRq.class,
            TransferRs.class
    ),
    UPDATE_PROFILE(
            "customer/profile",
            UpdateProfileRq.class,
            UpdateProfileRs.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestDto;
    private final Class<? extends BaseModel> responseModel;
}
