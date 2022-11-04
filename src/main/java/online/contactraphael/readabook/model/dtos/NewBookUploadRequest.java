package online.contactraphael.readabook.model.dtos;


import javax.validation.constraints.NotBlank;

public record NewBookUploadRequest(

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
        double price
) {
}
