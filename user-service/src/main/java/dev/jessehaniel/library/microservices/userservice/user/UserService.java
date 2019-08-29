package dev.jessehaniel.library.microservices.userservice.user;

import java.util.List;

interface UserService {
    UserDTO save(UserDTO userDTO);
    void delete(int userId);
    UserDTO update(int userId, UserDTO userDto);
    List<UserDTO> listAll();
    UserDTO getById(Integer userId);
}
