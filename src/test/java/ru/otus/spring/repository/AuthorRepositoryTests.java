package ru.otus.spring.repository;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;
import static ru.otus.spring.testutil.TestUtil.*;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.otus.spring.domain.Author;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorRepositoryTests {
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

    private static final int EXPECTED_AUTHOR_COUNT = 3;
    private static final Long EXISTING_AUTHOR_ID = 101L;
    private static final String EXISTING_AUTHOR_NAME = "Kristi";

    @Autowired
    private AuthorRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @Test
    void findByIdTest() {
        val actualAuthorOpt = repositoryJpa.findById(EXISTING_AUTHOR_ID);
        val expectedAuthor = em.find(Author.class, EXISTING_AUTHOR_ID);
        assertThat(actualAuthorOpt).isPresent().get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    void saveTest() {
        Author newAuthor = new Author(null, "Twain");
        Author savedAuthor = repositoryJpa.save(newAuthor);
        Author actualAuthor = repositoryJpa.findById(savedAuthor.getId()).orElse(new Author());
        assertThat(actualAuthor).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newAuthor);
    }

    @Test
    void getByIdExistingAuthorTest() {
        Author expectedAuthor = new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_NAME);
        Author actualAuthor = repositoryJpa.findById(EXISTING_AUTHOR_ID).orElse(new Author());
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    void deleteByIdOkTest() {
        assertThatCode(() -> repositoryJpa.findById(EXISTING_AUTHOR_ID))
                .doesNotThrowAnyException();

        repositoryJpa.deleteById(EXISTING_AUTHOR_ID);

        Optional<Author> authorOpt = repositoryJpa.findById(EXISTING_AUTHOR_ID);

        assertThat(authorOpt.isPresent()).isFalse();
    }

    @Test
    void getAllTest() {
        Author expectedAuthor = new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_NAME);
        List<Author> actualAuthorList = repositoryJpa.findAll();

        assertThat(actualAuthorList).hasSize(EXPECTED_AUTHOR_COUNT);
        assertThat(actualAuthorList.stream().anyMatch(item -> EXISTING_AUTHOR_NAME.equals(item.getName())));
    }

}
