package api.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepositMoneyRs extends BaseModel {
    Long id;
    String accountNumber;
    Double balance;
    List<TransactionsRs> transactions;
}
