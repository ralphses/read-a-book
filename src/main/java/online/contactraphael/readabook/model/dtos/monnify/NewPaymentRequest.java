package online.contactraphael.readabook.model.dtos.monnify;

import lombok.Data;

import javax.validation.constraints.NotBlank;

public record NewPaymentRequest(

        @NotBlank(message = "book ID required")
        String bookId,

//        @NotBlank(message = "Amount required")
        double amount,
        String userEmail,
        String customerName
) {
}
