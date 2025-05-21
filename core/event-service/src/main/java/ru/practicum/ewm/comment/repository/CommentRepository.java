package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "WHERE (:authorIds IS NULL OR c.authorId IN :authorIds) " +
            "AND (:eventIds IS NULL OR c.event.id IN :eventIds)")
    List<Comment> findByAuthorIdInAndEventIdIn(@Param("authorIds") List<Long> authorIds,
                                               @Param("eventIds") List<Long> eventIds,
                                               Pageable pageable);




    List<Comment> findAllByEventId(long id);

    List<Comment> findByAuthorId(Long userId, Pageable pageable);
}
