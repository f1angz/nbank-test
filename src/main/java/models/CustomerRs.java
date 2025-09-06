package models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRs {
    Long id;
    String username;
    String password;
    String name;
    UserRole role;
    List<DepositMoneyRs> accounts;
}
