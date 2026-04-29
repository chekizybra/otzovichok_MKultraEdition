package chekizybra.otzovichok;

import chekizybra.otzovichok.model.Category;
import chekizybra.otzovichok.model.Comment;
import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.repository.CategoryRepository;
import chekizybra.otzovichok.repository.CommentRepository;
import chekizybra.otzovichok.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTitleContainingIgnoreCaseOrderByPostDateDesc_mustReturnMatchingComments() {
        User user = new User();
        user.setFio("Test User");
        user.setMail("test@mail.com");
        user.setPasword("123456");
        user = userRepository.save(user);

        Category category = new Category();
        category.setCategory("Сладости");
        category = categoryRepository.save(category);

        Comment c1 = new Comment();
        c1.setUser(user);
        c1.setCategory(category);
        c1.setTitle("Бублики с маком");
        c1.setComment("Text 1");
        c1.setGrade(9);
        c1.setPostDate(LocalDate.of(2026, 4, 10));
        commentRepository.save(c1);

        Comment c2 = new Comment();
        c2.setUser(user);
        c2.setCategory(category);
        c2.setTitle("ВАФЛИ шоколадные");
        c2.setComment("Text 2");
        c2.setGrade(7);
        c2.setPostDate(LocalDate.of(2026, 4, 12));
        commentRepository.save(c2);

        Comment c3 = new Comment();
        c3.setUser(user);
        c3.setCategory(category);
        c3.setTitle("Бублики ванильные");
        c3.setComment("Text 3");
        c3.setGrade(8);
        c3.setPostDate(LocalDate.of(2026, 4, 11));
        commentRepository.save(c3);

        List<Comment> result = commentRepository
                .findByTitleContainingIgnoreCaseOrderByPostDateDesc("бублик");

        assertEquals(2, result.size());
        assertEquals("Бублики ванильные", result.get(0).getTitle());
        assertEquals("Бублики с маком", result.get(1).getTitle());
    }
}