package dev.jessehaniel.library.microservices.bookservice.book;

import dev.jessehaniel.library.microservices.bookservice.exception.BookAlreadyExistsException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for BOOK Service - CRUD operations")
class BookServiceTest {
    
    @Mock
    private BookRepository repository;
    private BookService service;
    private List<BookDTO> bookListMock;
    
    @BeforeEach
    void setUp() {
        service = new BookServiceImpl(repository);
        bookListMock = Arrays.asList(
                new BookDTO(1, "book1", "author1", "resume book1", "123", 2019),
                new BookDTO(2, "book2", "author1", "resume book2", "456", 2019),
                new BookDTO(3, "book3", "author2", "resume book3", "789", 2019)
        );
    }
    
    @Test
    @DisplayName("Successful SAVE call")
    void save() {
        when(repository.save(any(Book.class))).thenAnswer(i -> {
            Book book = (Book) i.getArguments()[0];
            book.setId(bookListMock.size() + 1);
            return book;
        });
        
        BookDTO newBookDto = new BookDTO(null, "book4", "author3", "resume book4", "123456", 2018);
        BookDTO bookSaved = service.save(newBookDto);
        
        verify(repository, times(1)).save(any(Book.class));
        assertThat(bookSaved, CoreMatchers.notNullValue());
        assertThat(bookSaved.getId(), CoreMatchers.notNullValue());
    }
    
    @Test
    @DisplayName("Successful DELETE call")
    void delete() {
        int bookId = 0;
        service.delete(bookId);
        verify(repository, times(1)).deleteById(bookId);
    }
    
    @Test
    @DisplayName("Successful UPDATE call")
    void update() {
        Optional<Book> bookMock = bookListMock.stream().map(Book::of).findAny();
        when(repository.findById(anyInt())).thenReturn(bookMock);
        when(repository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        
        Book book = bookMock.orElse(new Book());
        int bookId = book.getId();
        BookDTO bookToChange = Book.parseToDtoMono(book);
        bookToChange.setTitle(bookToChange.getTitle().toUpperCase());
        BookDTO bookDtoUpdated = service.update(bookId, bookToChange);
        
        verify(repository, times(1)).findById(bookId);
        verify(repository, times(1)).save(any(Book.class));
        assertThat(bookDtoUpdated.getId(), equalTo(bookId));
        assertThat(bookDtoUpdated.getTitle(), equalTo(book.getTitle().toUpperCase()));
    }
    
    @Test
    @DisplayName("Exception throws assertion on UPDATE when Book id not found")
    void updateWithNotFoundException() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        BookDTO bookDTO = new BookDTO(4, "book4", "author3", "resume book4", "123456", 2018);
        
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.update(bookDTO.getId(), bookDTO));
        assertEquals("Book ID not found", exception.getMessage());
    }
    
    @Test
    @DisplayName("Exception throws assertion on SAVE when Book id already exists")
    void saveAlreadyExists(){
        Optional<Book> optionalBook = Optional.of(Book.builder().id(0).build());
        when(repository.findById(anyInt())).thenReturn(optionalBook);
        BookAlreadyExistsException exception = assertThrows(BookAlreadyExistsException.class, () -> {
            BookDTO bookDTO = BookDTO.builder().id(0).build();
            service.save(bookDTO);
        });
        assertEquals("Book ID already exists", exception.getMessage());
    }
    
    @Test
    @DisplayName("List all call")
    void listAll() {
        when(repository.findAll()).thenReturn(Book.ofList(bookListMock));
        List<BookDTO> bookListActual = service.listAll();
        assertThat(bookListActual, CoreMatchers.is(bookListMock));
    }
    
    
    @Test
    void getUserById(){
        Optional<Book> book = Optional.of(bookListMock.stream().findAny().map(Book::of).orElseThrow(NoSuchElementException::new));
        when(repository.findById(anyInt())).thenReturn(book);
        
        Integer bookId = book.get().getId();
        BookDTO bookDTO = service.getById(bookId);
        
        verify(repository, times(1)).findById(bookId);
        assertThat(bookDTO, is(Book.parseToDtoMono(book.get())));
        assertEquals(bookId, bookDTO.getId());
    }
    
    @Test
    void throwsExceptionWhenGetBookByIdNotFound(){
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        int bookId = 0;
        
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.getById(bookId));
        assertEquals("Book ID not found", exception.getMessage());
        verify(repository, times(1)).findById(bookId);
    }
}