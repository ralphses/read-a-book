package online.contactraphael.readabook.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String transactionReference;
    private String paymentReference;

    private String bookId;
    private String ownerEmail;
    private String paymentMethod;
    private String purpose;

    private Double amountPaid;

    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
