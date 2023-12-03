package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.Like;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.repository.LikeCommentRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeCommentRepository likeCommentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public Like addLike(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        List<Like> likes = likeCommentRepository.findByAuthor(user);
        for (Like l : likes) {
            if (Objects.equals(l.getComment().getId(), commentId)) {
                throw new ValidationException("The like already exists");
            }

        }
        Like like = Like.builder()
                .author(user)
                .comment(comment)
                .build();
        return likeCommentRepository.save(like);
    }

    public void delete(Long userId, Long likeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Like like = likeCommentRepository.findById(likeId)
                .orElseThrow(() -> new NotFoundException("Like not found"));

        if (!like.getAuthor().getId().equals(user.getId())) {
            throw new ValidationException("You are not the author of this like");
        }
        likeCommentRepository.delete(like);
    }

    public List<Like> getLikesByComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        return likeCommentRepository.findAllByCommentId(commentId);
    }
}
