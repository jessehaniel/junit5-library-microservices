package dev.jessehaniel.library.microservices.loanservice.loan;

import dev.jessehaniel.library.microservices.loanservice.book.BookClientRepository;
import dev.jessehaniel.library.microservices.loanservice.book.BookDTO;
import dev.jessehaniel.library.microservices.loanservice.exception.FailedDependencyException;
import dev.jessehaniel.library.microservices.loanservice.user.UserClientRepository;
import dev.jessehaniel.library.microservices.loanservice.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LoanServiceImpl implements LoanService {
    
    private final LoanRepository repository;
    private final BookClientRepository bookClientRepository;
    private final UserClientRepository userClientRepository;
    
    @Override
    public LoanDTO save(LoanDTO loanDTO) {
        Loan loan = ConverterLoanDTO.parseToLoanMono(loanDTO);
        loan.setId(null);
        buildDTO(loan);
        return ConverterLoanDTO.of(repository.save(loan));
    }
    
    @Override
    public void delete(int loanId) {
        repository.deleteById(loanId);
    }
    
    @Override
    public LoanDTO update(int loanId, LoanDTO loanDTO) {
        if (repository.findById(loanId).isPresent()) {
            Loan loan = ConverterLoanDTO.parseToLoanMono(loanDTO);
            loan.setId(loanId);
            buildDTO(loan);
            return ConverterLoanDTO.of(repository.save(loan));
        } else {
            throw new NoSuchElementException("Loan ID not found");
        }
    }
    
    @Override
    public List<LoanDTO> listAll() {
        List<Loan> loanList = repository.findAll();
        return loanList.stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Builds the LoanDTO validating both userID and bookIdList whether they exist on respective microservices
     * @param loan the database Entity
     * @return the LoanDTO according the API contract, containing the full UserDTO and BookDTO list
     */
    private LoanDTO buildDTO(Loan loan) {
        return LoanDTO.builder()
                .id(loan.getId())
                .durationDays(loan.getDurationDays())
                .user(getUserById(loan.getUserId()))
                .bookList(getBookDTOList(loan.getBookIdList()))
                .build();
    }
    
    private UserDTO getUserById(Integer userId) {
        return Optional.ofNullable(userClientRepository.getById(userId))
                .orElseThrow(() -> new FailedDependencyException("User ID not found"));
    }
    
    private List<BookDTO> getBookDTOList(List<Integer> bookIdList) {
        return bookIdList.stream()
                .map(this::validateBookId)
                .collect(Collectors.toList());
    }
    
    private BookDTO validateBookId(Integer bookId) {
        return Optional.ofNullable(bookClientRepository.getById(bookId))
                .orElseThrow(() -> new FailedDependencyException("Book ID not found"));
    }
}
