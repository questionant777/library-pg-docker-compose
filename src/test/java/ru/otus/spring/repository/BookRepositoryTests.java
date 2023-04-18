package ru.otus.spring.repository;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static ru.otus.spring.testutil.TestUtil.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTests {
    @Container
    public static PostgreSQLContainer pgSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(
            DockerImageName.parse(PG_DOCKER_IMG_NAME))
            .withPassword(PG_USERNAME)
            .withUsername(PG_PASSWORD)
            .withReuse(true);

    @DynamicPropertySource
    public static void registerPgProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.username", pgSQLContainer::getUsername);
        propertyRegistry.add("spring.datasource.password", pgSQLContainer::getPassword);
        propertyRegistry.add("spring.datasource.url",  pgSQLContainer::getJdbcUrl);
    }

    @Autowired
    private BookRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    private static final Long EXISTING_BOOK_ID = 1L;
    private static final String EXISTING_BOOK_NAME = "Puaro";
    private static final Long EXISTING_AUTHOR_ID = 101L;
    private static final String EXISTING_AUTHOR_NAME = "Kristi";
    private static final Long EXISTING_GENRE_ID = 102L;
    private static final String EXISTING_GENRE_NAME = "detective";

    @Test
    void findByIdTest() {
        val actualBookOpt = repositoryJpa.findById(EXISTING_BOOK_ID);
        val expectedBook = em.find(Book.class, EXISTING_BOOK_ID);
        assertThat(actualBookOpt).isPresent().get()
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @Test
    void saveTest() {
        Book newBook = new Book(null,
                "newBookName",
                new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_NAME),
                new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME),
                null);

        Book savedBook = repositoryJpa.save(newBook);
        Book actualBook = repositoryJpa.findById(savedBook.getId()).orElse(new Book());

        assertThat(actualBook).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newBook);
    }

    @Test
    void getByIdExistingBookTest() {
        Book expectedBook = new Book(
                EXISTING_BOOK_ID,
                EXISTING_BOOK_NAME,
                new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_NAME),
                new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME),
                null);

        Book actualBook = repositoryJpa.findById(EXISTING_BOOK_ID).orElse(new Book());

        assertThat(actualBook)
                .usingRecursiveComparison()
                .ignoringFields("bookCommentList")
                .isEqualTo(expectedBook);
    }

    @Test
    void deleteByIdOkTest() {
        assertThatCode(() -> repositoryJpa.findById(EXISTING_BOOK_ID))
                .doesNotThrowAnyException();

        repositoryJpa.deleteById(EXISTING_BOOK_ID);

        Optional<Book> BookOpt = repositoryJpa.findById(EXISTING_BOOK_ID);

        assertThat(BookOpt.isPresent()).isFalse();
    }

    @Test
    void getAllTest() {
        List<Book> actualBookList = repositoryJpa.findAll();

        assertThat(actualBookList.stream().anyMatch(item -> EXISTING_BOOK_NAME.equals(item.getName())));
    }

}
