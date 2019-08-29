package dev.jessehaniel.library.microservices.loanservice.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("book-service")
public interface BookClientRepository {
    
    @RequestMapping("/books/{bookId}")
    BookDTO getById(@PathVariable("bookId") Integer bookId);
    
}
