package santannaf.hints.demo.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheErrorConfigTest {

    private final CacheErrorConfig cacheErrorConfig = new CacheErrorConfig();

    @Test
    void shouldNotThrowOnCacheGetError() {
        var handler = cacheErrorConfig.errorHandler();
        var cache = mock(Cache.class);
        when(cache.getName()).thenReturn("posts");

        assertDoesNotThrow(() ->
                handler.handleCacheGetError(new RuntimeException("Redis down"), cache, "all-posts"));
    }

    @Test
    void shouldNotThrowOnCachePutError() {
        var handler = cacheErrorConfig.errorHandler();
        var cache = mock(Cache.class);
        when(cache.getName()).thenReturn("posts");

        assertDoesNotThrow(() ->
                handler.handleCachePutError(new RuntimeException("Redis down"), cache, "all-posts", "value"));
    }

    @Test
    void shouldNotThrowOnCacheEvictError() {
        var handler = cacheErrorConfig.errorHandler();
        var cache = mock(Cache.class);
        when(cache.getName()).thenReturn("posts");

        assertDoesNotThrow(() ->
                handler.handleCacheEvictError(new RuntimeException("Redis down"), cache, "all-posts"));
    }

    @Test
    void shouldNotThrowOnCacheClearError() {
        var handler = cacheErrorConfig.errorHandler();
        var cache = mock(Cache.class);
        when(cache.getName()).thenReturn("posts");

        assertDoesNotThrow(() ->
                handler.handleCacheClearError(new RuntimeException("Redis down"), cache));
    }
}
