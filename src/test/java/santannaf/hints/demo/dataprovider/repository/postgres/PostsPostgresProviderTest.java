package santannaf.hints.demo.dataprovider.repository.postgres;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import santannaf.hints.demo.entity.Post;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostsPostgresProviderTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    private PostsPostgresProvider postsPostgresProvider;

    @Test
    void shouldInsertBatchOfPosts() {
        var posts = List.of(
                new Post(1L, "Title 1", "1", "Body 1"),
                new Post(2L, "Title 2", "2", "Body 2")
        );
        when(namedParameterJdbcTemplate.batchUpdate(any(String.class), any(SqlParameterSource[].class)))
                .thenReturn(new int[]{1, 1});

        var result = postsPostgresProvider.insertBatch(posts);

        assertArrayEquals(new int[]{1, 1}, result);

        ArgumentCaptor<SqlParameterSource[]> captor = ArgumentCaptor.forClass(SqlParameterSource[].class);
        verify(namedParameterJdbcTemplate).batchUpdate(
                eq("INSERT INTO posts.posts (id, title, user_id, body) VALUES (:id, :title, :userId, :body) " +
                        "ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, user_id = EXCLUDED.user_id, body = EXCLUDED.body"),
                captor.capture()
        );

        var batchValues = captor.getValue();
        assertEquals(2, batchValues.length);
        assertEquals(1L, batchValues[0].getValue("id"));
        assertEquals("Title 1", batchValues[0].getValue("title"));
        assertEquals("1", batchValues[0].getValue("userId"));
        assertEquals("Body 1", batchValues[0].getValue("body"));
    }

    @Test
    void shouldReturnEmptyArrayForEmptyList() {
        var result = postsPostgresProvider.insertBatch(Collections.emptyList());

        assertArrayEquals(new int[0], result);
        verifyNoInteractions(namedParameterJdbcTemplate);
    }
}
