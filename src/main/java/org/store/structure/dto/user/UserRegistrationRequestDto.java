package org.store.structure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.store.structure.validation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "repeatPassword", message = "passwords do not match!")
@Accessors(chain = true)
public class UserRegistrationRequestDto {
    @NotBlank
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
    private String repeatPassword;
    @Size(max = 300)
    private String shippingAddress;
}
