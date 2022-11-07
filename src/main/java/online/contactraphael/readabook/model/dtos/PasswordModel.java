package online.contactraphael.readabook.model.dtos;

import javax.validation.constraints.NotBlank;

public record PasswordModel(
        @NotBlank(message = "password required")
        String password,

        @NotBlank(message = "Confirm password required")
        String confirmPassword) {
}
