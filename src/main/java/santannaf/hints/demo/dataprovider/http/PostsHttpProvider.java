package santannaf.hints.demo.dataprovider.http;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.GetAllPostsProvider;

import java.util.List;

@Component
public class PostsHttpProvider implements GetAllPostsProvider {

    private final RestClient restClient;

    public PostsHttpProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Cacheable(value = "posts", key = "'all-posts'")
    @Override
    public List<Post> getAllPosts() {
        return restClient.get()
                .uri("/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
