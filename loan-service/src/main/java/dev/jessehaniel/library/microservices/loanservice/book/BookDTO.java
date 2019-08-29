package dev.jessehaniel.library.microservices.loanservice.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookDTO implements Serializable {
 
    private static final long serialVersionUID = -16690848669155241L;
 
    private Integer id;
    private String title;
    private String authorName;
    private String resume;
    private String isbn;
    private Integer yearRelease;
}