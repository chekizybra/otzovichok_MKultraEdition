package chekizybra.otzovichok.services;

import chekizybra.otzovichok.model.Comment;
import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.model.Vote;
import chekizybra.otzovichok.repository.CommentRepository;
import chekizybra.otzovichok.repository.UserRepository;
import chekizybra.otzovichok.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repo;
    private final VoteRepository voteRepo;
    private final UserRepository userRepo;

    public CommentService(CommentRepository repo, VoteRepository voteRepo, UserRepository userRepo) {
        this.repo = repo;
        this.voteRepo = voteRepo;
        this.userRepo = userRepo;
    }

    public List<Comment> getAllComments() {
        return repo.findAllByOrderByPostDateDesc();
    }

    public Comment getComment(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Comment saveComment(Comment comment) {
        return repo.save(comment);
    }

    public void deleteComment(Long id) {
        repo.deleteById(id);
    }

    public Comment upvote(Long commentId, String mail) {
        Comment c = getComment(commentId);
        User u = userRepo.findByMail(mail).orElse(null);

        if (c == null || u == null) {
            return null;
        }

        Vote v = voteRepo.findByUserIdAndCommentId(u.getId(), c.getId()).orElse(null);

        if (v == null) {
            v = new Vote();
            v.setUser(u);
            v.setComment(c);
            v.setValue(1);
            voteRepo.save(v);

            c.setPlus_grade(c.getPlus_grade() + 1);
            repo.save(c);
            return c;
        }

        if (v.getValue() == 1) {
            return c;
        }

        if (v.getValue() == -1) {
            v.setValue(1);
            voteRepo.save(v);

            c.setMinus_grade(c.getMinus_grade() - 1);
            c.setPlus_grade(c.getPlus_grade() + 1);
            repo.save(c);
        }

        return c;
    }

    public Comment downvote(Long commentId, String mail) {
        Comment c = getComment(commentId);
        User u = userRepo.findByMail(mail).orElse(null);

        if (c == null || u == null) {
            return null;
        }

        Vote v = voteRepo.findByUserIdAndCommentId(u.getId(), c.getId()).orElse(null);

        if (v == null) {
            v = new Vote();
            v.setUser(u);
            v.setComment(c);
            v.setValue(-1);
            voteRepo.save(v);

            c.setMinus_grade(c.getMinus_grade() + 1);
            repo.save(c);
            return c;
        }

        if (v.getValue() == -1) {
            return c;
        }

        if (v.getValue() == 1) {
            v.setValue(-1);
            voteRepo.save(v);

            c.setPlus_grade(c.getPlus_grade() - 1);
            c.setMinus_grade(c.getMinus_grade() + 1);
            repo.save(c);
        }

        return c;
    }
}