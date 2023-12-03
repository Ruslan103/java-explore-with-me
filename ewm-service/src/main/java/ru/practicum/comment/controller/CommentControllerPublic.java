package ru.practicum.comment.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/events")
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getCommentsForEvent(@PathVariable Long eventId) {
        return commentService.getCommentsForEvent(eventId);
    }

    @GetMapping("comments/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }
}
