package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    ),
    GET_TRANSACTIONS(
            "accounts/{id}/transactions",
            BaseModel.class,
            TransferRs.class
    ),
    CUSTOMER_ACCOUNTS(
            "customer/accounts",
            BaseModel.class,
            CreateAccountRs.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestDto;
    private final Class<? extends BaseModel> responseModel;
}
