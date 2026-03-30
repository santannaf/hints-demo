package santannaf.hints.demo.dataprovider.repository.postgres;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.InsertBatchPostsProvider;

import java.util.List;

@Component
public class PostsPostgresProvider implements InsertBatchPostsProvider {

    private static final String INSERT_SQL =
            "INSERT INTO posts.posts (id, title, user_id, body) VALUES (:id, :title, :userId, :body) " +
            "ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, user_id = EXCLUDED.user_id, body = EXCLUDED.body";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PostsPostgresProvider(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int[] insertBatch(List<Post> posts) {
        if (posts.isEmpty()) return new int[0];

        SqlParameterSource[] batchValues = posts.stream()
                .map(post -> new MapSqlParameterSource()
                        .addValue("id", post.id())
                        .addValue("title", post.title())
                        .addValue("userId", post.userId())
                        .addValue("body", post.body()))
                .toArray(SqlParameterSource[]::new);

        return namedParameterJdbcTemplate.batchUpdate(INSERT_SQL, batchValues);
    }
}
