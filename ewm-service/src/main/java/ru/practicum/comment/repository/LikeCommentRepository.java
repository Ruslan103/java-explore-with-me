package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Like;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface LikeCommentRepository extends JpaRepository<Like, Long> {
    List<Like> findByAuthor(User author);

    List<Like> findAllByCommentId(Long commentId);
}
