package santannaf.hints.demo.usecase;

import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.provider.GetAllPostsProvider;
import santannaf.hints.demo.provider.InsertBatchPostsProvider;
import santannaf.hints.demo.provider.SendEventPostsProvider;

import java.util.List;

@Named
public class GetAllPostsUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllPostsUseCase.class);

    private final GetAllPostsProvider getAllPostsProvider;
    private final InsertBatchPostsProvider insertBatchPostsProvider;
    private final SendEventPostsProvider sendEventPostsProvider;

    public GetAllPostsUseCase(GetAllPostsProvider getAllPostsProvider,
                              InsertBatchPostsProvider insertBatchPostsProvider,
                              SendEventPostsProvider sendEventPostsProvider) {
        this.getAllPostsProvider = getAllPostsProvider;
        this.insertBatchPostsProvider = insertBatchPostsProvider;
        this.sendEventPostsProvider = sendEventPostsProvider;
    }

    public List<Post> getAllPosts() {
        LOGGER.info("[getAllPosts] - Getting all posts");
        var posts = getAllPostsProvider.getAllPosts();
        LOGGER.info("[getAllPosts] - Inserting {} posts in batch", posts.size());
        insertBatchPostsProvider.insertBatch(posts);
        LOGGER.info("[getAllPosts] - Sending {} events to Kafka", posts.size());
        posts.forEach(sendEventPostsProvider::sendEvent);
        return posts;
    }
}
