package dev.jessehaniel.library.microservices.userservice.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer age;
    private String phoneNumber;
    
    static User of(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getAge(), userDTO.getPhoneNumber());
    }
    
    static UserDTO parseToDtoMono(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getAge(), user.getPhoneNumber());
    }
    
    static List<UserDTO> parseToDtoList(List<User> userList) {
        return userList.stream()
                .map(User::parseToDtoMono)
                .collect(Collectors.toList());
    }
}