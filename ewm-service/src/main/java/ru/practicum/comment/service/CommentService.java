package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsForEvent(Long eventId);

    CommentDto getCommentById(Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentDto addComment(Long userId, Long eventId, CommentDto dto);

    CommentDto updateComment(Long userId, Long commentId, CommentDto dto);

    void delete(Long userId, Long commentId);

    List<CommentDto> getAllCommentsByUser(Long userId);
}
