package dev.jessehaniel.library.microservices.loanservice.loan;

import dev.jessehaniel.library.microservices.loanservice.user.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

class ConverterLoanDTO {
    
    private ConverterLoanDTO() {
        super();
    }
    
    static Loan parseToLoanMono(LoanDTO loanDTO) {
        return Loan.builder()
                .id(loanDTO.getId())
                .durationDays(loanDTO.getDurationDays())
                .userId(loanDTO.getUser().getId())
                .bookIdList(LoanDTO.convertBookDTOListToBookIdList(loanDTO.getBookList()))
                .build();
    }
    
    static List<Loan> parseToLoanList(List<LoanDTO> loanDTOListMock) {
        return loanDTOListMock.stream()
                .map(ConverterLoanDTO::parseToLoanMono)
                .collect(Collectors.toList());
    }
    
    
    static LoanDTO of(Loan loan) {
        return LoanDTO.builder()
                .id(loan.getId())
                .durationDays(loan.getDurationDays())
                .user(UserDTO.builder().id(loan.getId()).build())
                .bookList(LoanDTO.convertBookIdListToBookDTOList(loan.getBookIdList()))
                .build();
    }
}
