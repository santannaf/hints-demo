package santannaf.hints.demo.entrypoint.sync.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import santannaf.hints.demo.entrypoint.sync.controller.PostsController;
import santannaf.hints.demo.usecase.GetAllPostsUseCase;

import java.net.ConnectException;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostsController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllPostsUseCase getAllPostsUseCase;

    @Test
    void shouldReturn404WhenHttpClientErrorNotFound() throws Exception {
        when(getAllPostsUseCase.getAllPosts())
                .thenThrow(HttpClientErrorException.create(NOT_FOUND, "Not Found", null, null, null));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("External API Client Error"));
    }

    @Test
    void shouldReturn502WhenHttpServerError() throws Exception {
        when(getAllPostsUseCase.getAllPosts())
                .thenThrow(HttpServerErrorException.create(INTERNAL_SERVER_ERROR, "Internal Server Error", null, null, null));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.title").value("External API Server Error"))
                .andExpect(jsonPath("$.detail").value("The external service returned an error"));
    }

    @Test
    void shouldReturn503WhenResourceAccessError() throws Exception {
        when(getAllPostsUseCase.getAllPosts())
                .thenThrow(new ResourceAccessException("Connection refused", new ConnectException("Connection refused")));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("External API Unavailable"))
                .andExpect(jsonPath("$.detail").value("Could not connect to the external service"));
    }

    @Test
    void shouldReturn500WhenUnexpectedError() throws Exception {
        when(getAllPostsUseCase.getAllPosts())
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"));
    }
}
