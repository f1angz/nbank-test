package models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRq extends BaseModel {
    Double amount;
    Long receiverAccountId;
    Long senderAccountId;
}
