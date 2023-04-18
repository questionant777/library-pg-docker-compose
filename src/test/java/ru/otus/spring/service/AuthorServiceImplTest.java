package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.otus.spring.repository.AuthorRepository;
import ru.otus.spring.domain.Author;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.otus.spring.testutil.TestUtil.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {
    public static final long NEW_AUTHOR_ID = 1L;
    public static final String NEW_AUTHOR_NAME = "new author name";
    public static final long EXISTING_AUTHOR_ID = 5L;
    public static final String EXISTING_AUTHOR_NAME = "existing author name";

    @Mock
    AuthorRepository authorDao;

    @InjectMocks
    AuthorServiceImpl service;

    @Test
    void getOrSaveByNameForExistingAuthorTest() {
        Author expectedAuthor = new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_NAME);

        when(authorDao.findByName(EXISTING_AUTHOR_NAME))
                .thenReturn(Optional.of(expectedAuthor));

        Author actualAuthor = service.getOrSaveByName(EXISTING_AUTHOR_NAME);

        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    void getOrSaveByNameSaveAndRetNotExistingAuthorTest() {
        Author newAuthor = new Author(NEW_AUTHOR_ID, NEW_AUTHOR_NAME);

        when(authorDao.save(any()))
                .thenReturn(newAuthor);

        Author actualAuthor = service.getOrSaveByName(NEW_AUTHOR_NAME);

        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(newAuthor);
    }

}