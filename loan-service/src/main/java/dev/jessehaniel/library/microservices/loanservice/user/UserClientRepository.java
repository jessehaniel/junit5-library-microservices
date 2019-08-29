package dev.jessehaniel.library.microservices.loanservice.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-service")
public interface UserClientRepository {
    
    @GetMapping("/users/{userId}")
    UserDTO getById(@PathVariable("userId") Integer userId);
    
}
