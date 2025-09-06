package models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionsRs extends BaseModel {
    Integer id;
    Double amount;
    String type;
    String timestamp;
    Integer relatedAccountId;

}
