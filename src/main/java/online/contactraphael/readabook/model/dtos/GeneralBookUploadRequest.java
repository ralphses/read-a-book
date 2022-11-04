package online.contactraphael.readabook.model.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record GeneralBookUploadRequest(

        int noOfPages,

        String licence,

        @NotBlank(message = "Year published of pages required")
        String yearPublished,

        @NotBlank(message = "Author required")
        String author,

        @NotBlank(message = "Title required")
        String title,

        @NotBlank(message = "Book summary required")
        String summary,
        String edition,

        @NotBlank(message = "full name required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        String email,

        double amount

) {
}
