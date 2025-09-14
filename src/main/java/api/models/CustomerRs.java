package api.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRs extends BaseModel {
    Long id;
    String username;
    String password;
    String name;
    UserRole role;
    List<DepositMoneyRs> accounts;
}
