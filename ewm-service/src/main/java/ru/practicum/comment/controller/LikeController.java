package ru.practicum.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.model.Like;
import ru.practicum.comment.service.LikeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/comment/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/comment/{commentId}/user/{userId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Like addLike(@PathVariable Long commentId, @PathVariable Long userId) {
        return likeService.addLike(commentId, userId);
    }

    @DeleteMapping("/user/{userId}/like/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long userId, @PathVariable Long likeId) {
        likeService.delete(userId, likeId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Like> getLikes(@PathVariable Long commentId) {
        return likeService.getLikesByComment(commentId);
    }
}
