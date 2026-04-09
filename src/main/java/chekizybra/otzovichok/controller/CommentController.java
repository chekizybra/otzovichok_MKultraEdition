package chekizybra.otzovichok.controller;

import chekizybra.otzovichok.model.Comment;
import chekizybra.otzovichok.services.CommentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Comment> getComments() {
        return service.getAllComments();
    }

    @PostMapping("/{id}/upvote")
    public Comment upvote(@PathVariable Long id, Authentication authentication) {
        return service.upvote(id, authentication.getName());
    }

    @PostMapping("/{id}/downvote")
    public Comment downvote(@PathVariable Long id, Authentication authentication) {
        return service.downvote(id, authentication.getName());
    }

    @PostMapping
    public Comment addComment(@RequestBody Comment comment) {
        return service.saveComment(comment);
    }
}