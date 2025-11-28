package com.example.proyectoaplicaciones.api_server.comment;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping
    // LA CORRECCIÓN ESTÁ AQUÍ: Se escribe List<Comment> en lugar de List<Comment
    public List<Comment> getCommentsForPost(@PathVariable Integer postId) {
        return commentRepository.findByPostId(postId);
    }

    @PostMapping
    public Comment addCommentToPost(@PathVariable Integer postId, @RequestBody Comment comment) {
        comment.setPostId(postId);
        return commentRepository.save(comment);
    }
}