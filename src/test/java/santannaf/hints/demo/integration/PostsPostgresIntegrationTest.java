package santannaf.hints.demo.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import santannaf.hints.demo.dataprovider.repository.postgres.PostsPostgresProvider;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.SendEventPostsProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(ContainerConfig.class)
@ActiveProfiles("integration")
class PostsPostgresIntegrationTest {

    @MockitoBean
    private SendEventPostsProvider sendEventPostsProvider;

    @Autowired
    private PostsPostgresProvider postsPostgresProvider;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM posts.posts", Map.of());
    }

    @Test
    void shouldInsertBatchAndPersistInPostgres() {
        var posts = List.of(
                new Post(1L, "Title 1", "1", "Body 1"),
                new Post(2L, "Title 2", "2", "Body 2"),
                new Post(3L, "Title 3", "1", "Body 3")
        );

        var result = postsPostgresProvider.insertBatch(posts);

        assertArrayEquals(new int[]{1, 1, 1}, result);

        var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts.posts", Map.of(), Long.class);
        assertEquals(3L, count);
    }

    @Test
    void shouldReturnEmptyArrayForEmptyList() {
        var result = postsPostgresProvider.insertBatch(Collections.emptyList());

        assertArrayEquals(new int[0], result);

        var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts.posts", Map.of(), Long.class);
        assertEquals(0L, count);
    }
}
