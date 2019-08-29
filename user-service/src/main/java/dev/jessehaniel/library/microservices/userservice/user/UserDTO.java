package dev.jessehaniel.library.microservices.userservice.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
class UserDTO implements Serializable {
    private static final long serialVersionUID = 3304936624683755096L;
    private Integer id;
    private String name;
    private Integer age;
    private String phoneNumber;
}
