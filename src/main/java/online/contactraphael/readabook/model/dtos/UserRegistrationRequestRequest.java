package online.contactraphael.readabook.model.dtos;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record UserRegistrationRequestRequest(

//        @Pattern(regexp = "\\[]", message = "Enter a valid name")
        @NotBlank(message = "Full name required")
        String fullName,

        @Email(message = "Enter a valid email")
        String email,

        @Length(min = 8, message = "password must not be less than 8 characters")
        String password,
        String confirmPassword

        ) {
}
