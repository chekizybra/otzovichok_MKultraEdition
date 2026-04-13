package chekizybra.otzovichok.services;

import chekizybra.otzovichok.dto.CommentCreateDto;
import chekizybra.otzovichok.dto.CommentReadDto;
import chekizybra.otzovichok.model.Category;
import chekizybra.otzovichok.model.Comment;
import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.model.Vote;
import chekizybra.otzovichok.repository.CategoryRepository;
import chekizybra.otzovichok.repository.CommentRepository;
import chekizybra.otzovichok.repository.UserRepository;
import chekizybra.otzovichok.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repo;
    private final VoteRepository voteRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;

    public CommentService(CommentRepository repo,
                          VoteRepository voteRepo,
                          UserRepository userRepo,
                          CategoryRepository categoryRepo) {
        this.repo = repo;
        this.voteRepo = voteRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Comment> getAllComments() {
        return repo.findAllByOrderByPostDateDesc();
    }

    public List<CommentReadDto> getAllCommentsDto() {
        return repo.findAllByOrderByPostDateDesc().stream().map(c -> {
            long plus = voteRepo.countByCommentIdAndValue(c.getId(), 1);
            long minus = voteRepo.countByCommentIdAndValue(c.getId(), -1);
            return CommentReadDto.from(c, plus, minus);
        }).toList();
    }

    public Comment getComment(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Comment createComment(CommentCreateDto req, String mail) {
        User user = userRepo.findByMail(mail).orElse(null);
        Category category = categoryRepo.findById(req.getCategoryId()).orElse(null);

        if (user == null || category == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setCategory(category);
        comment.setTitle(req.getTitle());
        comment.setComment(req.getComment());
        comment.setGrade(req.getGrade());
        comment.setPostDate(LocalDate.now());

        return repo.save(comment);
    }

    public void deleteComment(Long id) {
        repo.deleteById(id);
    }

    public Comment upvote(Long commentId, String mail) {
        System.out.println("auth = " + mail);

        Comment c = getComment(commentId);
        User u = userRepo.findByMail(mail).orElse(null);

        System.out.println("comment = " + c);
        System.out.println("user = " + u);

        if (c == null || u == null) {
            return null;
        }

        Vote v = voteRepo.findByUserIdAndCommentId(u.getId(), c.getId()).orElse(null);

        System.out.println("vote = " + v);

        if (v == null) {
            v = new Vote();
            v.setUser(u);
            v.setComment(c);
            v.setValue(1);
            voteRepo.save(v);
            return c;
        }

        if (v.getValue() == 1) {
            return c;
        }

        v.setValue(1);
        voteRepo.save(v);
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
            return c;
        }

        if (v.getValue() == -1) {
            return c;
        }

        v.setValue(-1);
        voteRepo.save(v);
        return c;
    }
}