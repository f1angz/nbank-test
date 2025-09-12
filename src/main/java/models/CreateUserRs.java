package models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRs extends BaseModel {
    Long id;
    String username;
    String password;
    String name;
    String role;
    List<TransactionsRs> accounts;

}
