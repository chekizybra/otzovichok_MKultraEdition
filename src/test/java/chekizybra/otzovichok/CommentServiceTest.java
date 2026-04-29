package chekizybra.otzovichok;

import chekizybra.otzovichok.dto.CommentCreateDto;
import chekizybra.otzovichok.model.Category;
import chekizybra.otzovichok.model.Comment;
import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.model.Vote;
import chekizybra.otzovichok.repository.CategoryRepository;
import chekizybra.otzovichok.repository.CommentRepository;
import chekizybra.otzovichok.repository.UserRepository;
import chekizybra.otzovichok.repository.VoteRepository;
import chekizybra.otzovichok.services.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_mustSaveComment_whenUserAndCategoryExist() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setCategoryId(1L);
        dto.setTitle("Test title");
        dto.setComment("Test comment");
        dto.setGrade(8);

        User user = new User();
        user.setId(10L);
        user.setMail("test@mail.com");

        Category category = new Category();
        category.setId(1L);
        category.setCategory("Tech");

        when(userRepository.findByMail("test@mail.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Comment saved = new Comment();
        saved.setId(100L);
        saved.setUser(user);
        saved.setCategory(category);
        saved.setTitle("Test title");
        saved.setComment("Test comment");
        saved.setGrade(8);
        saved.setPostDate(LocalDate.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        Comment result = commentService.createComment(dto, "test@mail.com");

        assertNotNull(result);
        assertEquals("Test title", result.getTitle());
        assertEquals("Test comment", result.getComment());
        assertEquals(8, result.getGrade());
        assertEquals(user, result.getUser());
        assertEquals(category, result.getCategory());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment toSave = captor.getValue();
        assertEquals(user, toSave.getUser());
        assertEquals(category, toSave.getCategory());
        assertEquals("Test title", toSave.getTitle());
        assertEquals("Test comment", toSave.getComment());
        assertEquals(8, toSave.getGrade());
        assertNotNull(toSave.getPostDate());
    }

    @Test
    void createComment_mustReturnNull_whenUserNotFound() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setCategoryId(1L);
        dto.setTitle("Test");
        dto.setComment("Text");
        dto.setGrade(5);

        when(userRepository.findByMail("missing@mail.com")).thenReturn(Optional.empty());

        Comment result = commentService.createComment(dto, "missing@mail.com");

        assertNull(result);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_mustReturnNull_whenCategoryNotFound() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setCategoryId(999L);
        dto.setTitle("Test");
        dto.setComment("Text");
        dto.setGrade(5);

        User user = new User();
        user.setId(1L);
        user.setMail("test@mail.com");

        when(userRepository.findByMail("test@mail.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Comment result = commentService.createComment(dto, "test@mail.com");

        assertNull(result);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void upvote_mustCreateVote_whenVoteNotExists() {
        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setMail("test@mail.com");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByMail("test@mail.com")).thenReturn(Optional.of(user));
        when(voteRepository.findByUserIdAndCommentId(10L, 1L)).thenReturn(Optional.empty());

        Comment result = commentService.upvote(1L, "test@mail.com");

        assertNotNull(result);

        ArgumentCaptor<Vote> captor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository).save(captor.capture());

        Vote savedVote = captor.getValue();
        assertEquals(1, savedVote.getValue());
        assertEquals(user, savedVote.getUser());
        assertEquals(comment, savedVote.getComment());
    }

    @Test
    void upvote_mustChangeVoteFromMinusToPlus() {
        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setMail("test@mail.com");

        Vote vote = new Vote();
        vote.setId(5L);
        vote.setUser(user);
        vote.setComment(comment);
        vote.setValue(-1);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByMail("test@mail.com")).thenReturn(Optional.of(user));
        when(voteRepository.findByUserIdAndCommentId(10L, 1L)).thenReturn(Optional.of(vote));

        Comment result = commentService.upvote(1L, "test@mail.com");

        assertNotNull(result);
        assertEquals(1, vote.getValue());
        verify(voteRepository).save(vote);
    }

    @Test
    void downvote_mustCreateVote_whenVoteNotExists() {
        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setMail("test@mail.com");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByMail("test@mail.com")).thenReturn(Optional.of(user));
        when(voteRepository.findByUserIdAndCommentId(10L, 1L)).thenReturn(Optional.empty());

        Comment result = commentService.downvote(1L, "test@mail.com");

        assertNotNull(result);

        ArgumentCaptor<Vote> captor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository).save(captor.capture());

        Vote savedVote = captor.getValue();
        assertEquals(-1, savedVote.getValue());
        assertEquals(user, savedVote.getUser());
        assertEquals(comment, savedVote.getComment());
    }

    @Test
    void downvote_mustChangeVoteFromPlusToMinus() {
        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setMail("test@mail.com");

        Vote vote = new Vote();
        vote.setId(5L);
        vote.setUser(user);
        vote.setComment(comment);
        vote.setValue(1);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByMail("test@mail.com")).thenReturn(Optional.of(user));
        when(voteRepository.findByUserIdAndCommentId(10L, 1L)).thenReturn(Optional.of(vote));

        Comment result = commentService.downvote(1L, "test@mail.com");

        assertNotNull(result);
        assertEquals(-1, vote.getValue());
        verify(voteRepository).save(vote);
    }

    @Test
    void deleteMyComment_mustDelete_whenOwnerMatches() {
        User user = new User();
        user.setId(10L);
        user.setMail("test@mail.com");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteMyComment(1L, "test@mail.com");

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteMyComment_mustNotDelete_whenOwnerDoesNotMatch() {
        User owner = new User();
        owner.setId(10L);
        owner.setMail("owner@mail.com");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(owner);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteMyComment(1L, "another@mail.com");

        verify(commentRepository, never()).deleteById(anyLong());
    }
}