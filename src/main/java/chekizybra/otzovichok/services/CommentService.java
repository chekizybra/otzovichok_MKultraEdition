package chekizybra.otzovichok.services;

import chekizybra.otzovichok.dto.CategoryDto;
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
            CommentReadDto dto = CommentReadDto.from(c, plus, minus);
            dto.setCategoryPath(buildCategoryPath(c.getCategory()));
            return dto;
        }).toList();
    }

    public Comment getComment(Long id) {
        return repo.findById(id).orElse(null);
    }

    public CommentReadDto getCommentDto(Long id) {
        Comment c = repo.findById(id).orElse(null);

        if (c == null) {
            return null;
        }

        long plus = voteRepo.countByCommentIdAndValue(c.getId(), 1);
        long minus = voteRepo.countByCommentIdAndValue(c.getId(), -1);

        CommentReadDto dto = CommentReadDto.from(c, plus, minus);
        dto.setCategoryPath(buildCategoryPath(c.getCategory()));
        return dto;
    }

    public String buildCategoryPath(Category category) {
        if (category == null) {
            return "";
        }

        String path = category.getCategory();
        Category current = category.getParent();

        while (current != null) {
            path = current.getCategory() + " -> " + path;
            current = current.getParent();
        }

        return path;
    }

    public List<CommentReadDto> getMyCommentsDto(String mail, String sort) {
        List<Comment> comments;

        if ("date_asc".equals(sort)) {
            comments = repo.findByUserMailOrderByPostDateAsc(mail);
        } else if ("grade_desc".equals(sort)) {
            comments = repo.findByUserMailOrderByGradeDesc(mail);
        } else if ("grade_asc".equals(sort)) {
            comments = repo.findByUserMailOrderByGradeAsc(mail);
        } else {
            comments = repo.findByUserMailOrderByPostDateDesc(mail);
        }

        return comments.stream().map(c -> {
            CommentReadDto dto = new CommentReadDto();
            dto.setId(c.getId());
            dto.setTitle(c.getTitle());
            dto.setComment(c.getComment());
            dto.setGrade(c.getGrade());
            dto.setPostDate(c.getPostDate() != null ? c.getPostDate().toString() : null);
            dto.setPlusGrade(voteRepo.countByCommentIdAndValue(c.getId(), 1));
            dto.setMinusGrade(voteRepo.countByCommentIdAndValue(c.getId(), -1));
            if (c.getCategory() != null) {
                dto.setCategory(CategoryDto.from(c.getCategory()));
            }
            dto.setCategoryPath(buildCategoryPath(c.getCategory()));
            return dto;
        }).toList();
    }

    public void deleteMyComment(Long id, String mail) {
        Comment comment = repo.findById(id).orElse(null);

        if (comment == null) {
            return;
        }

        if (comment.getUser() == null || comment.getUser().getMail() == null) {
            return;
        }

        if (!comment.getUser().getMail().equals(mail)) {
            return;
        }

        repo.deleteById(id);
    }

    public List<CommentReadDto> getCommentsDto(String title, String sort) {
        List<Comment> comments;

        boolean hasTitle = title != null && !title.isBlank();

        if (hasTitle) {
            if ("date_asc".equals(sort)) {
                comments = repo.findByTitleContainingIgnoreCaseOrderByPostDateAsc(title);
            } else if ("grade_desc".equals(sort)) {
                comments = repo.findByTitleContainingIgnoreCaseOrderByGradeDesc(title);
            } else if ("grade_asc".equals(sort)) {
                comments = repo.findByTitleContainingIgnoreCaseOrderByGradeAsc(title);
            } else {
                comments = repo.findByTitleContainingIgnoreCaseOrderByPostDateDesc(title);
            }
        } else {
            if ("date_asc".equals(sort)) {
                comments = repo.findAllByOrderByPostDateAsc();
            } else if ("grade_desc".equals(sort)) {
                comments = repo.findAllByOrderByGradeDesc();
            } else if ("grade_asc".equals(sort)) {
                comments = repo.findAllByOrderByGradeAsc();
            } else {
                comments = repo.findAllByOrderByPostDateDesc();
            }
        }

        return comments.stream().map(c -> {
            CommentReadDto dto = new CommentReadDto();
            dto.setId(c.getId());
            dto.setTitle(c.getTitle());
            dto.setComment(c.getComment());
            dto.setGrade(c.getGrade());
            dto.setPostDate(c.getPostDate() != null ? c.getPostDate().toString() : null);
            dto.setPlusGrade(voteRepo.countByCommentIdAndValue(c.getId(), 1));
            dto.setMinusGrade(voteRepo.countByCommentIdAndValue(c.getId(), -1));

            if (c.getCategory() != null) {
                dto.setCategory(CategoryDto.from(c.getCategory()));
            }

            dto.setCategoryPath(buildCategoryPath(c.getCategory()));

            return dto;
        }).toList();
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