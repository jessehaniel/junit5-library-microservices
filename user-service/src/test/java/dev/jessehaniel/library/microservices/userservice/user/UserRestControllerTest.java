package dev.jessehaniel.library.microservices.userservice.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jessehaniel.library.microservices.userservice.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {
    
    private static final String URL_USERS = "/users";
    
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    
    @Test
    @DisplayName("RestController test for UserDTO POST method")
    void save() throws Exception {
        when(service.save(any(UserDTO.class))).thenAnswer(i -> i.getArgument(0));
        
        UserDTO userDTO = new UserDTO(1, "user1", 11, "1234");
        String userJson = new ObjectMapper().writeValueAsString(userDTO);
        mockMvc.perform(post(URL_USERS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(userJson));
        verify(service, times(1)).save(userDTO);
    }
    
    @Test
    @DisplayName("RestController test for UserDTO DELETE method")
    void delete() throws Exception {
        int userId = 1;
        String urlDelete = URL_USERS + "/{userId}";
        mockMvc.perform(MockMvcRequestBuilders.delete(urlDelete, userId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent());
        verify(service, times(1)).delete(userId);
    }
    
    @Test
    @DisplayName("RestController test for UserDTO PUT method")
    void update() throws Exception {
        UserDTO userDTO = new UserDTO(1, "user1", 11, "1234");
        int userId = userDTO.getId();
        
        when(service.update(anyInt(), any(UserDTO.class))).thenReturn(userDTO);
        
        String urlUpdate = URL_USERS + "/{userId}";
        String jsonUser = new ObjectMapper().writeValueAsString(userDTO);
        mockMvc.perform(put(urlUpdate, userId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUser));
        verify(service, times(1)).update(userId, userDTO);
    }
    
    @Test
    @DisplayName("Exception Handler test when Id NOT FOUND for PUT method")
    void exceptionHandlerWhenUpdateNotFound() throws Exception {
        UserDTO userDTO = new UserDTO();
        int userId = 5;
        
        when(service.update(anyInt(), any(UserDTO.class))).thenThrow(NoSuchElementException.class);
        
        String urlUpdate = URL_USERS + "/{userId}";
        String jsonUser = new ObjectMapper().writeValueAsString(userDTO);
        mockMvc.perform(put(urlUpdate, userId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonUser))
                .andExpect(status().isNotFound());
        verify(service, times(1)).update(userId, userDTO);
    }
    
    @Test
    @DisplayName("Exception Handler test when Id CONFLICT for POST method")
    void exceptionHandlerWhenSaveAlreadyExists() throws Exception {
        
        when(service.save(any(UserDTO.class))).thenThrow(UserAlreadyExistsException.class);
        
        UserDTO userDTO = new UserDTO();
        String userJson = new ObjectMapper().writeValueAsString(userDTO);
        mockMvc.perform(post(URL_USERS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(userJson))
                .andExpect(status().isConflict());
        verify(service, times(1)).save(userDTO);
    }
    
    @Test
    @DisplayName("RestController test for UserDTO GET method")
    void listAll() throws Exception {
        mockMvc.perform(get(URL_USERS)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(service, times(1)).listAll();
    }
    
    @Test
    @DisplayName("RestController test for UserDTO GET/{userId} method")
    void getUserById() throws Exception {
        int userId = 1;
        UserDTO userDTO = UserDTO.builder().id(userId).build();
        String jsonUser = new ObjectMapper().writeValueAsString(userDTO);
        
        when(service.getById(anyInt())).thenReturn(userDTO);
        
        String urlGetById = URL_USERS + "/{userId}";
        mockMvc.perform(get(urlGetById, userId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUser));
        verify(service, times(1)).getById(userId);
    }
    
    @Test
    @DisplayName("Exception Handler test when Id NOT_FOUND for GET/{userId} method")
    void handleExceptionWhenGetByIdFails() throws Exception {
        when(service.getById(anyInt())).thenThrow(NoSuchElementException.class);
        
        int userId = 1;
        String urlGetById = URL_USERS + "/{userId}";
        mockMvc.perform(get(urlGetById, userId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
        verify(service, times(1)).getById(userId);
    }
}