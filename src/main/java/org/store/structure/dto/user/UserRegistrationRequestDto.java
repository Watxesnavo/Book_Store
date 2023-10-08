package org.store.structure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.store.structure.validation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "repeatPassword", message = "passwords do not match!")
public class UserRegistrationRequestDto {
    @NotBlank
    @Size(min = 4, max = 50)
    @Email
    private String email;
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;
    @NotBlank
    @Size(min = 4, max = 100)
    private String password;
    @NotBlank
    @Size(min = 4, max = 100)
    private String repeatPassword;
    @Size(max = 300)
    private String shippingAddress;
}
