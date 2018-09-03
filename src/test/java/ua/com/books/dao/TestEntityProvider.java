package ua.com.books.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import ua.com.books.model.Author;
import ua.com.books.model.Book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TestEntityProvider {

    public List<Book> generateBookList() {
        int size = RandomUtils.nextInt(2, 5);
        return Stream.generate(this::generateBook).limit(size).collect(toList());
    }

    public List<Author> generateAuthorList() {
        int size = RandomUtils.nextInt(1, 3);
        return Stream.generate(this::generateAuthor).limit(size).collect(toList());
    }

    public Book generateBook() {
        return Book.builder()
                .title(RandomStringUtils.randomAlphabetic(5, 30))
                .isbn(RandomStringUtils.randomNumeric(10, 13))
                .price(BigDecimal.valueOf(RandomUtils.nextDouble(0.25, 300.25)))
                .releaseDate(LocalDate.ofYearDay(RandomUtils.nextInt(1800, 2020), RandomUtils.nextInt(1, 365)))
                .language(RandomStringUtils.randomAlphabetic(2, 20))
                .weight(RandomUtils.nextFloat(0.20f, 10.50f))
                .authors(generateAuthorList())
                .build();
    }

    public Author generateAuthor() {
        return Author.builder()
                .firstName(RandomStringUtils.randomAlphabetic(2, 15))
                .middleName(RandomStringUtils.randomAlphabetic(2, 15))
                .lastName(RandomStringUtils.randomAlphabetic(2, 15))
                .birthDate(LocalDate.ofYearDay(RandomUtils.nextInt(1800, 2000), RandomUtils.nextInt(1, 365)))
                .email(RandomStringUtils.randomAlphanumeric(1, 15) + "@" + RandomStringUtils.randomAlphabetic(1, 8))
                .build();
    }
}
