package chekizybra.otzovichok.repository;

import chekizybra.otzovichok.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByOrderByPostDateDesc();
    List<Comment> findByCategory_IdOrderByPostDateDesc(Long categoryId);
    List<Comment> findByUser_IdOrderByPostDateDesc(Long userId);
}

