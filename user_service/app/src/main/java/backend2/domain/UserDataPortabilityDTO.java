package backend2.domain;

import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataPortabilityDTO {
    private Integer id;
    private String username;
    private String email;
    private String address;
    private String phone;
    private Set<Role> roles;
    private LocalDate createdAt;
} 