package dev.jessehaniel.library.microservices.loanservice.loan;

import dev.jessehaniel.library.microservices.loanservice.book.BookClientRepository;
import dev.jessehaniel.library.microservices.loanservice.book.BookDTO;
import dev.jessehaniel.library.microservices.loanservice.exception.FailedDependencyException;
import dev.jessehaniel.library.microservices.loanservice.user.UserClientRepository;
import dev.jessehaniel.library.microservices.loanservice.user.UserDTO;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    
    @Mock
    private LoanRepository repository;
    @Mock
    private BookClientRepository bookClientRepository;
    @Mock
    private UserClientRepository userClientRepository;
    private LoanService service;
    private List<LoanDTO> loanDTOListMock;
    
    @BeforeEach
    void setUp() {
        service = new LoanServiceImpl(repository, bookClientRepository, userClientRepository);
        loanDTOListMock = Arrays.asList(
                new LoanDTO(1, 1, new UserDTO(1, "user1", 1, "123"),
                        singletonList(new BookDTO(1, "book1","author1", "resume book1", "123", 2019))),
                new LoanDTO(2, 2, new UserDTO(1, "user1", 1, "123"),
                        Arrays.asList(new BookDTO(1, "book1","author1", "resume book1", "123", 2019),
                                new BookDTO(1, "book1","author1", "resume book1", "123", 2019))),
                new LoanDTO(3, 3, new UserDTO(1, "user1", 1, "123"),
                        singletonList(new BookDTO(1, "book1","author1", "resume book1", "123", 2019)))
        );
    }
    
    @Test
    void save() {
        when(repository.save(any(Loan.class))).thenAnswer(i -> {
            Loan loan = i.getArgument(0);
            loan.setId(loanDTOListMock.size() + 1);
            return loan;
        });
        when(userClientRepository.getById(anyInt())).thenAnswer(i -> mockUserClientRepositoryGetById(i.getArgument(0)));
        when(bookClientRepository.getById(anyInt())).thenAnswer(i -> mockBookClientRepositoryGetById(i.getArgument(0)));
        
        LoanDTO loanDTO = loanDTOListMock.stream().findAny().orElseGet(LoanDTO::new);
        LoanDTO loanSaved = service.save(loanDTO);
        
        verify(repository, times(1)).save(any(Loan.class));
        verify(userClientRepository, times(1)).getById(anyInt());
        verify(bookClientRepository, times(loanDTO.getBookList().size())).getById(anyInt());
        assertThat(loanSaved, notNullValue());
        assertThat(loanSaved.getId(), notNullValue());
    }
    
    @Test
    void delete() {
        int loanId = 0;
        service.delete(loanId);
        verify(repository, times(1)).deleteById(loanId);
    }
    
    @Test
    void update() {
        Optional<Loan> optionalLoan = loanDTOListMock.stream().map(ConverterLoanDTO::parseToLoanMono).findAny();
        when(repository.findById(anyInt())).thenReturn(optionalLoan);
        when(repository.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));
        when(userClientRepository.getById(anyInt())).thenAnswer(i -> mockUserClientRepositoryGetById(i.getArgument(0)));
        when(bookClientRepository.getById(anyInt())).thenAnswer(i -> mockBookClientRepositoryGetById(i.getArgument(0)));
        
        Loan loan = optionalLoan.orElseGet(Loan::new);
        LoanDTO loanDTO = ConverterLoanDTO.of(loan);
        int loanId = loanDTO.getId();
        loanDTO.setDurationDays(loanDTO.getDurationDays() + 1);
        LoanDTO loanUpdated = service.update(loanId, loanDTO);
        
        verify(repository, times(1)).findById(loanId);
        verify(repository, times(1)).save(ConverterLoanDTO.parseToLoanMono(loanDTO));
        verify(userClientRepository, times(1)).getById(anyInt());
        verify(bookClientRepository, times(loanDTO.getBookList().size())).getById(anyInt());
        assertThat(loanUpdated.getId(), equalTo(loanDTO.getId()));
        assertThat(loanUpdated.getDurationDays(), greaterThan(ConverterLoanDTO.of(loan).getDurationDays()));
    }
    
    @Test
    void listAll() {
        List<Loan> loanList = ConverterLoanDTO.parseToLoanList(loanDTOListMock);
        when(repository.findAll()).thenReturn(loanList);
        when(userClientRepository.getById(anyInt())).thenAnswer(i -> mockUserClientRepositoryGetById(i.getArgument(0)));
        when(bookClientRepository.getById(anyInt())).thenAnswer(i -> mockBookClientRepositoryGetById(i.getArgument(0)));
        
        List<LoanDTO> loanDTOList = service.listAll();
    
        Integer bookListSize = loanDTOListMock.stream().map(dto -> dto.getBookList().size()).reduce(0, Integer::sum);
        verify(repository, times(1)).findAll();
        verify(userClientRepository, times(loanDTOListMock.size())).getById(anyInt());
        verify(bookClientRepository, times(bookListSize)).getById(anyInt());
        assertThat(loanDTOList, CoreMatchers.is(loanDTOListMock));
    }
    
    @Test
    void updateWithNotFoundException() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        
        LoanDTO loanDTO = loanDTOListMock.stream().findAny().orElseGet(LoanDTO::new);
        
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.update(loanDTO.getId(), loanDTO));
        assertEquals("Loan ID not found", exception.getMessage());
    }
    
    @Test
    void saveWithDependencyFailedException() {
        when(userClientRepository.getById(anyInt())).thenReturn(null);
        
        LoanDTO loanDTO = loanDTOListMock.stream().findAny().orElseGet(LoanDTO::new);
    
        FailedDependencyException exception = assertThrows(FailedDependencyException.class, () -> service.save(loanDTO));
        assertEquals("User ID not found", exception.getMessage());
    }
    
    private BookDTO mockBookClientRepositoryGetById(int argument) {
        return loanDTOListMock.stream()
                .map(LoanDTO::getBookList)
                .map(bookDTOList -> filterListByBookId(bookDTOList, argument))
                .findFirst().orElseGet(BookDTO::new);
    }
    
    private BookDTO filterListByBookId(List<BookDTO> bookDTOList, int argument) {
        return bookDTOList.stream()
                .filter(dto -> dto.getId().equals(argument))
                .findFirst()
                .orElseGet(BookDTO::new);
    }
    
    private UserDTO mockUserClientRepositoryGetById(int argument) {
        return loanDTOListMock.stream()
                .filter(loanDTO -> loanDTO.getUser().getId().equals(argument))
                .map(LoanDTO::getUser)
                .findFirst().orElseGet(UserDTO::new);
    }
}