package dev.jessehaniel.library.microservices.bookservice.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookRestController {
    
    private final BookService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO save(@RequestBody BookDTO book){
        return service.save(book);
    }
    
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer bookId) {
        service.delete(bookId);
    }
    
    @PutMapping("/{bookId}")
    public BookDTO update(@PathVariable int bookId, @RequestBody BookDTO bookDTO) {
        return service.update(bookId, bookDTO);
    }
    
    @GetMapping
    public List<BookDTO> listAll(){
        return service.listAll();
    }
    
    @GetMapping("/{bookId}")
    public BookDTO getById(@PathVariable Integer bookId){
        return service.getById(bookId);
    }
}
