package dev.jessehaniel.library.microservices.bookservice.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
class BookDTO implements Serializable {
    private static final long serialVersionUID = -2843839062908896824L;
    private Integer id;
    private String title;
    private String authorName;
    private String resume;
    private String isbn;
    private Integer yearRelease;
}