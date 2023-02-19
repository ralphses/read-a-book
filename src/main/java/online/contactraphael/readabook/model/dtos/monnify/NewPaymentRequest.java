package online.contactraphael.readabook.model.dtos.monnify;

import lombok.Data;

import javax.validation.constraints.NotBlank;

public record NewPaymentRequest(

        @NotBlank(message = "book ID required")
        String bookId,

        double amount,
        String userEmail,
        String customerName
) {
}
