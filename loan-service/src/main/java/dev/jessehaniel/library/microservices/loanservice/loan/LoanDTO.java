package dev.jessehaniel.library.microservices.loanservice.loan;

import dev.jessehaniel.library.microservices.loanservice.book.BookDTO;
import dev.jessehaniel.library.microservices.loanservice.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
class LoanDTO implements Serializable {
    
    private static final long serialVersionUID = 3891305383146310712L;
    
    private Integer id;
    private int durationDays;
    private UserDTO user;
    private List<BookDTO> bookList;
    
    static List<Integer> convertBookDTOListToBookIdList(List<BookDTO> bookList) {
        return bookList.stream()
                .map(BookDTO::getId)
                .collect(Collectors.toList());
    }
    
    static List<BookDTO> convertBookIdListToBookDTOList(List<Integer> bookIdList) {
        return bookIdList.stream()
                .map(bookId ->
                        BookDTO.builder()
                                .id(bookId)
                                .build()
                ).collect(Collectors.toList());
    }
}
