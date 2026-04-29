package chekizybra.otzovichok.controller;

import chekizybra.otzovichok.dto.CommentCreateDto;
import chekizybra.otzovichok.dto.CommentReadDto;
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

    @GetMapping("/my")
    public List<CommentReadDto> getMyComments(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "date_desc") String sort
    ) {
        return service.getMyCommentsDto(authentication.getName(), sort);
    }

    @GetMapping
    public List<CommentReadDto> getComments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "date_desc") String sort
    ) {
        return service.getCommentsDto(title, sort);
    }

    @GetMapping("/{id}")
    public CommentReadDto getCommentById(@PathVariable Long id) {
        return service.getCommentDto(id);
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
    public Comment addComment(@RequestBody CommentCreateDto req, Authentication authentication) {
        return service.createComment(req, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id, Authentication authentication) {
        service.deleteMyComment(id, authentication.getName());
    }
}