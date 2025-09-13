package api.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRs extends BaseModel {
    Double amount;
    Long receiverAccountId;
    Long senderAccountId;
    String message;
}
