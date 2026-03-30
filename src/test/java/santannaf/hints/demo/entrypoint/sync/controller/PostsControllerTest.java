package santannaf.hints.demo.entrypoint.sync.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.usecase.GetAllPostsUseCase;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostsController.class)
class PostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllPostsUseCase getAllPostsUseCase;

    @Test
    void shouldReturnPostsList() throws Exception {
        var posts = List.of(
                new Post(1L, "Title 1", "1", "Body 1"),
                new Post(2L, "Title 2", "2", "Body 2")
        );
        when(getAllPostsUseCase.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].userId").value("1"))
                .andExpect(jsonPath("$[0].body").value("Body 1"));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        when(getAllPostsUseCase.getAllPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
