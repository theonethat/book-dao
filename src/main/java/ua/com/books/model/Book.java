package ua.com.books.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Book {
    private Long id;
    private String title;
    private String isbn;
    private BigDecimal price;
    private LocalDate releaseDate;
    private String language;
    private Float weight;
    private List<Author> authors;
}
