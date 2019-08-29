package dev.jessehaniel.library.microservices.userservice.user;

import dev.jessehaniel.library.microservices.userservice.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
class UserServiceImpl implements UserService {
    
    private final UserRepository repository;
    
    @Override
    public UserDTO save(UserDTO userDTO) {
        if (repository.findById(Objects.requireNonNull(userDTO).getId()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = User.of(userDTO);
        user.setId(null);
        return User.parseToDtoMono(repository.save(user));
    }
    
    @Override
    public void delete(int userId) {
        repository.deleteById(userId);
    }
    
    @Override
    public UserDTO update(int userId, UserDTO userDto) {
        if (repository.findById(userId).isPresent()) {
            userDto.setId(userId);
            User user = User.of(userDto);
            return User.parseToDtoMono(repository.save(user));
        } else {
            throw new NoSuchElementException("User ID not found");
        }
    }
    
    @Override
    public List<UserDTO> listAll() {
        return User.parseToDtoList(repository.findAll());
    }
    
    @Override
    public UserDTO getById(Integer userId) {
        return User.parseToDtoMono(
                repository.findById(userId)
                        .orElseThrow(() -> new NoSuchElementException("User ID not found"))
        );
    }
}
