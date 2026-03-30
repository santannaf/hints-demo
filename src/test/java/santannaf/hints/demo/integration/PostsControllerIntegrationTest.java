package santannaf.hints.demo.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.GetAllPostsProvider;
import santannaf.hints.demo.provider.SendEventPostsProvider;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(ContainerConfig.class)
@ActiveProfiles("integration")
class PostsControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @MockitoBean
    private GetAllPostsProvider getAllPostsProvider;

    @MockitoBean
    private SendEventPostsProvider sendEventPostsProvider;

    private final List<Post> fakePosts = List.of(
            new Post(1L, "Title 1", "1", "Body 1"),
            new Post(2L, "Title 2", "2", "Body 2")
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jdbcTemplate.update("DELETE FROM posts.posts", Map.of());
        var cache = cacheManager.getCache("posts");
        if (cache != null) {
            cache.clear();
        }
        when(getAllPostsProvider.getAllPosts()).thenReturn(fakePosts);
    }

    @Test
    void shouldReturnPostsAndPersistInDatabase() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].body").value("Body 1"));

        var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts.posts", Map.of(), Long.class);
        assertTrue(count > 0, "Posts should be persisted in the database after GET /posts");
    }
}
