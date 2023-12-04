package ru.practicum.comment.dto;

import ru.practicum.comment.model.Comment;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;

public class CommentMapper {
    public static Comment toComment(CommentDto dto, User user, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .author(user)
                .event(event)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .event(EventMapper.toShortDto(comment.getEvent()))
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .build();
    }
}
