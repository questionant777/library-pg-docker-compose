package ru.otus.spring.repository;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;
import static ru.otus.spring.testutil.TestUtil.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.otus.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GenreRepositoryTest {
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

    private static final Long EXISTING_GENRE_ID = 102L;
    private static final String EXISTING_GENRE_NAME = "detective";

    @Autowired
    private GenreRepository repositoryJpa;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveTest() {
        Genre newGenre = new Genre(null, "poema");
        Genre savedGenre = repositoryJpa.save(newGenre);
        Genre actualGenre = repositoryJpa.findById(savedGenre.getId()).orElse(new Genre());
        assertThat(actualGenre).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newGenre);
    }

    @Test
    void findByIdTest() {
        val actualGenreOpt = repositoryJpa.findById(EXISTING_GENRE_ID);
        val expectedGenre = em.find(Genre.class, EXISTING_GENRE_ID);
        assertThat(actualGenreOpt).isPresent().get()
                .usingRecursiveComparison()
                .isEqualTo(expectedGenre);
    }

    @Test
    void findByIdExistingGenreTest() {
        Genre expectedGenre = new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME);
        Genre actualGenre = repositoryJpa.findById(EXISTING_GENRE_ID).orElse(new Genre());
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @Test
    void deleteByIdOkTest() {
        assertThatCode(() -> repositoryJpa.findById(EXISTING_GENRE_ID))
                .doesNotThrowAnyException();

        repositoryJpa.deleteById(EXISTING_GENRE_ID);

        Optional<Genre> genreOpt = repositoryJpa.findById(EXISTING_GENRE_ID);

        assertThat(genreOpt.isPresent()).isFalse();
    }

    @Test
    void getAllTest() {
        Genre expectedGenre = new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME);
        List<Genre> actualGenreList = repositoryJpa.findAll();
        assertThat(actualGenreList.stream().anyMatch(item -> EXISTING_GENRE_NAME.equals(item.getName())));
    }

}
