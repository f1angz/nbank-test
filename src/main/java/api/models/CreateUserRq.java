package api.models;

import api.configs.Config;
import api.generators.GeneratingRule;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRq extends BaseModel {
    @GeneratingRule(regex = "[A-Za-z0-9]{3,15}")
    String username;
    @GeneratingRule(regex = "[A-Z]{3}[a-z]{4}[0-9]{3}[%$&]{2}")
    String password;
    @GeneratingRule(regex = "USER")
    String role;

    public static CreateUserRq getAdmin() {
        return CreateUserRq.builder()
                .username(Config.getProperty("admin.username"))
                .password(Config.getProperty("admin.password"))
                .build();
    }
}
