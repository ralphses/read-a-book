package online.contactraphael.readabook.model.dtos;

import javax.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String email,

        @NotBlank
        String password) {
}
