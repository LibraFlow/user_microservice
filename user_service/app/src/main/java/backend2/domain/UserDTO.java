package backend2.domain;

import lombok.*;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private char[] pwd;
    private String email;
    private String address;
    private String phone;
    private Set<Role> roles;
}