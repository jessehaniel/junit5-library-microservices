package dev.jessehaniel.library.microservices.loanservice.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/loans")
class LoanRestController {
    
    private final LoanService service;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO save(@RequestBody LoanDTO loanDTO){
        return service.save(loanDTO);
    }
    
    @DeleteMapping("/{loanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer loanId) {
        service.delete(loanId);
    }
    
    @PutMapping("/{loanId}")
    public LoanDTO update(@PathVariable int loanId, @RequestBody LoanDTO loanDTO) {
        return service.update(loanId, loanDTO);
    }
    
    @GetMapping
    public List<LoanDTO> listAll(){
        return service.listAll();
    }
}
