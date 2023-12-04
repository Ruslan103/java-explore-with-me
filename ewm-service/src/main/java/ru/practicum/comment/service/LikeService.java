package ru.practicum.comment.service;

import ru.practicum.comment.model.Like;

import java.util.List;


public interface LikeService {
    Like addLike(Long commentId, Long userId);

    void delete(Long userId, Long likeId);

    List<Like> getLikesByComment(Long commentId);
}
