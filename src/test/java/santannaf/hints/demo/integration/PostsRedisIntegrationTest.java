package santannaf.hints.demo.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import santannaf.hints.demo.provider.GetAllPostsProvider;
import santannaf.hints.demo.provider.SendEventPostsProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Import(ContainerConfig.class)
@ActiveProfiles("integration")
class PostsRedisIntegrationTest {

    @MockitoBean
    private GetAllPostsProvider getAllPostsProvider;

    @MockitoBean
    private SendEventPostsProvider sendEventPostsProvider;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void shouldStoreAndRetrieveStringFromRedis() {
        stringRedisTemplate.opsForValue().set("test-key", "test-value");

        var value = stringRedisTemplate.opsForValue().get("test-key");
        assertNotNull(value);
        assertEquals("test-value", value);
    }

    @Test
    void shouldReturnNullForMissingKey() {
        var value = stringRedisTemplate.opsForValue().get("non-existent-key");
        assertNull(value);
    }

    @Test
    void shouldDeleteKey() {
        stringRedisTemplate.opsForValue().set("delete-me", "value");
        assertNotNull(stringRedisTemplate.opsForValue().get("delete-me"));

        stringRedisTemplate.delete("delete-me");
        assertNull(stringRedisTemplate.opsForValue().get("delete-me"));
    }
}
