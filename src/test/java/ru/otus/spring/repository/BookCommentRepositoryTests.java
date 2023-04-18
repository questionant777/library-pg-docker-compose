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
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static ru.otus.spring.testutil.TestUtil.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookCommentRepositoryTests {
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
    private BookCommentRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    private static final Long EXISTING_BOOK_COMMENT_ID = 1L;
    private static final String EXISTING_BOOK_COMMENT_NAME = "comment11";

    @Test
    void findByIdTest() {
        val actualBookComOpt = repositoryJpa.findById(EXISTING_BOOK_COMMENT_ID);
        val expectedAuthor = em.find(BookComment.class, EXISTING_BOOK_COMMENT_ID);

        assertThat(actualBookComOpt).isPresent().get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    void saveTest() {
        BookComment bookComment = new BookComment(null, "comment", Book.builder().id(101L).build());
        BookComment savedBookComment = repositoryJpa.save(bookComment);

        BookComment actualBookComment = repositoryJpa.findById(savedBookComment.getId()).orElse(new BookComment());
        assertThat(actualBookComment).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookComment);
    }

    @Test
    void getByIdExistingCommentTest() {
        BookComment actualBookComment = repositoryJpa.findById(EXISTING_BOOK_COMMENT_ID).orElse(new BookComment());
        assertThat(actualBookComment.getComment()).isEqualTo(EXISTING_BOOK_COMMENT_NAME);
    }

    @Test
    void deleteByIdOkTest() {
        assertThatCode(() -> repositoryJpa.findById(EXISTING_BOOK_COMMENT_ID))
                .doesNotThrowAnyException();

        repositoryJpa.deleteById(EXISTING_BOOK_COMMENT_ID);

        Optional<BookComment> bookCommentOpt = repositoryJpa.findById(EXISTING_BOOK_COMMENT_ID);

        assertThat(bookCommentOpt.isPresent()).isFalse();
    }

    @Test
    void getAllTest() {
        List<BookComment> actualBookCommentList = repositoryJpa.findAll();

        assertThat(actualBookCommentList.stream().anyMatch(item -> EXISTING_BOOK_COMMENT_NAME.equals(item.getComment())));
    }

}
