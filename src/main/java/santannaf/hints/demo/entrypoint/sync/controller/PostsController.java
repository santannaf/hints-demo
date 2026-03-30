package santannaf.hints.demo.entrypoint.sync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import santannaf.hints.demo.entity.Post;
import santannaf.hints.demo.usecase.GetAllPostsUseCase;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostsController {

    private final GetAllPostsUseCase getAllPostsUseCase;

    public PostsController(GetAllPostsUseCase getAllPostsUseCase) {
        this.getAllPostsUseCase = getAllPostsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(getAllPostsUseCase.getAllPosts());
    }
}
