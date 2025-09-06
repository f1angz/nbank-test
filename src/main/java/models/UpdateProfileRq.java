package models;

import generators.GeneratingRule;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRq extends BaseModel {
    @GeneratingRule(regex = "[A-Z][a-z]+ [A-Z][a-z]+")
    String name;
}
