package dev.jessehaniel.library.microservices.userservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
class UserRestController {
    
    private final UserService service;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO save(@RequestBody UserDTO userDTO){
        return service.save(userDTO);
    }
    
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer userId) {
        service.delete(userId);
    }
    
    @PutMapping("/{userId}")
    public UserDTO update(@PathVariable int userId, @RequestBody UserDTO userDTO) {
        return service.update(userId, userDTO);
    }
    
    @GetMapping
    public List<UserDTO> listAll(){
        return service.listAll();
    }
    
    @GetMapping("/{userId}")
    public UserDTO getById(@PathVariable Integer userId){
        return service.getById(userId);
    }
}