package santannaf.hints.demo.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.GetAllPostsProvider;
import santannaf.hints.demo.provider.InsertBatchPostsProvider;
import santannaf.hints.demo.provider.SendEventPostsProvider;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllPostsUseCaseTest {

    @Mock
    private GetAllPostsProvider getAllPostsProvider;

    @Mock
    private InsertBatchPostsProvider insertBatchPostsProvider;

    @Mock
    private SendEventPostsProvider sendEventPostsProvider;

    @InjectMocks
    private GetAllPostsUseCase getAllPostsUseCase;

    @Test
    void shouldGetAllPostsInsertBatchAndSendEvents() {
        var posts = List.of(
                new Post(1L, "Title 1", "1", "Body 1"),
                new Post(2L, "Title 2", "2", "Body 2")
        );
        when(getAllPostsProvider.getAllPosts()).thenReturn(posts);

        var result = getAllPostsUseCase.getAllPosts();

        assertEquals(2, result.size());
        assertEquals(posts, result);
        verify(getAllPostsProvider).getAllPosts();
        verify(insertBatchPostsProvider).insertBatch(posts);
        verify(sendEventPostsProvider).sendEvent(posts.get(0));
        verify(sendEventPostsProvider).sendEvent(posts.get(1));
    }

    @Test
    void shouldHandleEmptyList() {
        when(getAllPostsProvider.getAllPosts()).thenReturn(Collections.emptyList());

        var result = getAllPostsUseCase.getAllPosts();

        assertTrue(result.isEmpty());
        verify(insertBatchPostsProvider).insertBatch(Collections.emptyList());
    }
}
