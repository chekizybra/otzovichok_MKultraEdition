package chekizybra.otzovichok.repository;

import chekizybra.otzovichok.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //основные
    List<Comment> findAllByOrderByPostDateDesc();
    //поиск
    List<Comment> findByTitleContainingIgnoreCase(String title);
    List<Comment> findByTitleContainingIgnoreCaseOrderByPostDateDesc(String title);
    List<Comment> findByTitleContainingIgnoreCaseOrderByPostDateAsc(String title);
    List<Comment> findByTitleContainingIgnoreCaseOrderByGradeDesc(String title);
    List<Comment> findByTitleContainingIgnoreCaseOrderByGradeAsc(String title);
    //сортировка
    List<Comment> findAllByOrderByPostDateAsc();
    List<Comment> findAllByOrderByGradeDesc();
    List<Comment> findAllByOrderByGradeAsc();
    //профиль
    List<Comment> findByUserMailOrderByPostDateDesc(String mail);
    List<Comment> findByUserMailOrderByPostDateAsc(String mail);
    List<Comment> findByUserMailOrderByGradeDesc(String mail);
    List<Comment> findByUserMailOrderByGradeAsc(String mail);
}

