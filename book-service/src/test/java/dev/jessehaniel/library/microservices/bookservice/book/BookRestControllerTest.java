package dev.jessehaniel.library.microservices.bookservice.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jessehaniel.library.microservices.bookservice.exception.BookAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {
    
    private static final String URL_BOOKS = "/books";
    private static final String URL_BOOKS_ID = URL_BOOKS + "/{bookId}";
    
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService service;
    
    @Test
    @DisplayName("RestController test for BookDTO POST method")
    void save() throws Exception {
        when(service.save(any(BookDTO.class))).thenAnswer(i -> i.getArgument(0));
        
        BookDTO bookDTO = new BookDTO(1, "book1", "author1", "resume book1", "123", 2019);
        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);
        mockMvc.perform(post(URL_BOOKS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(bookJson));
        verify(service, times(1)).save(bookDTO);
    }
    
    @Test
    @DisplayName("RestController test for BookDTO DELETE method")
    void delete() throws Exception {
        int bookId = 1;
        mockMvc.perform(MockMvcRequestBuilders.delete(URL_BOOKS_ID, bookId)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNoContent());
        verify(service, times(1)).delete(bookId);
    }
    
    @Test
    @DisplayName("RestController test for BookDTO PUT method")
    void update() throws Exception {
        BookDTO bookDTO = new BookDTO(1, "book1", "author1", "resume book1", "123", 2019);
        int bookId = bookDTO.getId();
        
        when(service.update(anyInt(), any(BookDTO.class))).thenReturn(bookDTO);
        
        String jsonBook = new ObjectMapper().writeValueAsString(bookDTO);
        mockMvc.perform(put(URL_BOOKS_ID, bookId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonBook))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonBook));
        verify(service, times(1)).update(bookId, bookDTO);
    }
    
    @Test
    @DisplayName("Exception Handler test when Id NOT FOUND for PUT method")
    void exceptionHandlerWhenUpdateNotFound() throws Exception {
        BookDTO bookDTO = new BookDTO();
        int bookId = 5;
        
        when(service.update(anyInt(), any(BookDTO.class))).thenThrow(NoSuchElementException.class);
        
        String jsonBook = new ObjectMapper().writeValueAsString(bookDTO);
        mockMvc.perform(put(URL_BOOKS_ID, bookId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonBook))
                .andExpect(status().isNotFound());
        verify(service, times(1)).update(bookId, bookDTO);
    }
    
    @Test
    @DisplayName("Exception Handler test when Id CONFLICT for POST method")
    void exceptionHandlerWhenSaveAlreadyExists() throws Exception {
        
        when(service.save(any(BookDTO.class))).thenThrow(BookAlreadyExistsException.class);
    
        BookDTO bookDTO = new BookDTO();
        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);
        mockMvc.perform(post(URL_BOOKS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(bookJson))
                .andExpect(status().isConflict());
        verify(service, times(1)).save(bookDTO);
    }
    
    @Test
    @DisplayName("RestController test for BookDTO GET method")
    void listAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_BOOKS)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        verify(service, times(1)).listAll();
    }
    
    @Test
    @DisplayName("RestController test for BookDTO GET/{bookId} method")
    void getBookById() throws Exception {
        int bookId = 1;
        BookDTO bookDTO = BookDTO.builder().id(bookId).build();
        String jsonBook = new ObjectMapper().writeValueAsString(bookDTO);
        
        when(service.getById(anyInt())).thenReturn(bookDTO);
        
        mockMvc.perform(get(URL_BOOKS_ID, bookId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonBook));
        verify(service, times(1)).getById(bookId);
    }
    
    @Test
    @DisplayName("Exception Handler test when Id NOT_FOUND for GET/{bookId} method")
    void handleExceptionWhenGetByIdFails() throws Exception {
        when(service.getById(anyInt())).thenThrow(NoSuchElementException.class);
        
        int bookId = 1;
        mockMvc.perform(get(URL_BOOKS_ID, bookId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
        verify(service, times(1)).getById(bookId);
    }
}