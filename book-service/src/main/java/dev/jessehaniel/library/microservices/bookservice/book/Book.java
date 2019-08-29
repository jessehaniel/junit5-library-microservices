package dev.jessehaniel.library.microservices.bookservice.book;

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
class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String authorName;
    private String resume;
    private String isbn;
    private int yearRelease;
    
    static Book of(BookDTO bookDTO) {
        return new Book(bookDTO.getId(), bookDTO.getTitle(), bookDTO.getAuthorName(), bookDTO.getResume(), bookDTO.getIsbn(),
                bookDTO.getYearRelease());
    }
    
    static List<BookDTO> parseToDtoList(List<Book> bookList) {
        return bookList.stream()
                .map(Book::parseToDtoMono)
                .collect(Collectors.toList());
    }
    
    static BookDTO parseToDtoMono(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthorName(), book.getResume(), book.getIsbn(), book.getYearRelease());
    }
    
    static List<Book> ofList(List<BookDTO> bookList) {
        return bookList.stream().map(Book::of).collect(Collectors.toList());
    }
}

