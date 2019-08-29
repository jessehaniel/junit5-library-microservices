package dev.jessehaniel.library.microservices.bookservice.book;

import dev.jessehaniel.library.microservices.bookservice.exception.BookAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    
    private final BookRepository repository;
    
    @Override
    public BookDTO save(BookDTO bookDTO) {
        if(repository.findById(Objects.requireNonNull(bookDTO).getId()).isPresent()) {
            throw new BookAlreadyExistsException();
        }
        Book book = Book.of(bookDTO);
        book.setId(null);
        return Book.parseToDtoMono(repository.save(book));
    }
    
    @Override
    public void delete(int bookId) {
        repository.deleteById(bookId);
    }
    
    @Override
    public BookDTO update(int bookId, BookDTO bookDTO) {
        if (repository.findById(bookId).isPresent()) {
            Book book = Book.of(bookDTO);
            book.setId(bookId);
            return Book.parseToDtoMono(repository.save(book));
        } else {
            throw new NoSuchElementException("Book ID not found");
        }
    }
    
    @Override
    public List<BookDTO> listAll() {
        return Book.parseToDtoList(repository.findAll());
    }
    
    @Override
    public BookDTO getById(Integer bookId) {
        return Book.parseToDtoMono(
                repository.findById(bookId)
                        .orElseThrow(() -> new NoSuchElementException("Book ID not found"))
        );
    }
}
