package chekizybra.otzovichok.repository;

import chekizybra.otzovichok.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndCommentId(Long userId, Long commentId);

    long countByCommentIdAndValue(Long commentId, Integer value);
}
