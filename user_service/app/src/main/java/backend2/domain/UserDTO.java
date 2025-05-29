package backend2.domain;

import lombok.*;
import jakarta.validation.constraints.*;
import backend2.security.validation.ValidPassword;
import java.util.Set;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @ValidPassword
    private String pwd;  // Changed from char[] to String for BCrypt

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,4}[-\\s.]?[0-9]{1,4}$", message = "Invalid phone number")
    private String phone;

    @NotNull
    private Set<Role> roles;

    private boolean deleted;
}