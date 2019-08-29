package dev.jessehaniel.library.microservices.loanservice.loan;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jessehaniel.library.microservices.loanservice.book.BookDTO;
import dev.jessehaniel.library.microservices.loanservice.exception.ExceptionResponse;
import dev.jessehaniel.library.microservices.loanservice.exception.FailedDependencyException;
import dev.jessehaniel.library.microservices.loanservice.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.NoSuchElementException;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanRestController.class)
class LoanRestControllerTest {
    
    private static final String URL_LOANS = "/loans";
    private static final String URL_LOANS_ID = URL_LOANS + "/{loanId}";
    
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoanService service;
    private LoanDTO loanDTO;
    
    @BeforeEach
    void setUp() {
        loanDTO = new LoanDTO(1, 1, new UserDTO(1, "user1", 1, "123"),
                singletonList(new BookDTO(1, "book1","author1", "resume book1", "123", 2019)));
    }
    
    @Test
    void save() throws Exception {
        when(service.save(any(LoanDTO.class))).thenAnswer(i -> i.getArgument(0));
    
        String loanJson = new ObjectMapper().writeValueAsString(loanDTO);
        mockMvc.perform(post(URL_LOANS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(loanJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(loanJson));
        verify(service, times(1)).save(loanDTO);
    }
    
    @Test
    void delete() throws Exception {
        int loanId = 1;
        mockMvc.perform(MockMvcRequestBuilders.delete(URL_LOANS_ID, loanId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent());
        verify(service, times(1)).delete(loanId);
    }
    
    @Test
    void update() throws Exception {
        when(service.update(anyInt(), any(LoanDTO.class))).thenReturn(loanDTO);
    
        String loanJson = new ObjectMapper().writeValueAsString(loanDTO);
        mockMvc.perform(put(URL_LOANS_ID, loanDTO.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(loanJson))
                .andExpect(status().isOk())
                .andExpect(content().json(loanJson));
        verify(service, times(1)).update(loanDTO.getId(), loanDTO);
    }
    
    @Test
    void exceptionHandlerWhenUpdateNotFound() throws Exception {
        when(service.update(anyInt(), any(LoanDTO.class))).thenThrow(NoSuchElementException.class);
        
        String loanJson = new ObjectMapper().writeValueAsString(loanDTO);
        mockMvc.perform(put(URL_LOANS_ID, loanDTO.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(loanJson))
                .andExpect(status().isNotFound());
        verify(service, times(1)).update(loanDTO.getId(), loanDTO);
    }
    
    @Test
    void exceptionHandlerWhenDependenceFailed() throws Exception {
        when(service.save(any(LoanDTO.class))).thenThrow(FailedDependencyException.class);
        
        String loanJson = new ObjectMapper().writeValueAsString(loanDTO);
        mockMvc.perform(post(URL_LOANS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(loanJson))
                .andExpect(status().isFailedDependency());
        verify(service, times(1)).save(loanDTO);
    }
    
    @Test
    void listAll() throws Exception {
        mockMvc.perform(get(URL_LOANS)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(service, times(1)).listAll();
    }
}