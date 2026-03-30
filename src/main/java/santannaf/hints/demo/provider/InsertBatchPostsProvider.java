package santannaf.hints.demo.provider;

import santannaf.hints.demo.entity.Post;

import java.util.List;

public interface InsertBatchPostsProvider {
    int[] insertBatch(List<Post> posts);
}
