package chekizybra.otzovichok;

import chekizybra.otzovichok.controller.AuthController;
import chekizybra.otzovichok.model.Role;
import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.repository.RoleRepository;
import chekizybra.otzovichok.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_mustReturnOk_whenDataIsCorrect() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("ivan@test.com");
        user.setPasword("123456");

        Role role = new Role();
        role.setId(1L);
        role.setRole("user");

        when(userRepo.findByMail("ivan@test.com")).thenReturn(Optional.empty());
        when(roleRepo.findByRole("user")).thenReturn(Optional.of(role));
        when(encoder.encode("123456")).thenReturn("encoded_password");

        String result = authController.register(user);

        assertEquals("ok", result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("Иван Иванов", savedUser.getFio());
        assertEquals("ivan@test.com", savedUser.getMail());
        assertEquals("encoded_password", savedUser.getPasword());
        assertEquals(role, savedUser.getRole());
    }

    @Test
    void register_mustReturnMessage_whenFioIsEmpty() {
        User user = new User();
        user.setFio("");
        user.setMail("ivan@test.com");
        user.setPasword("123456");

        String result = authController.register(user);

        assertEquals("Введите ФИО", result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void register_mustReturnMessage_whenMailIsEmpty() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("");
        user.setPasword("123456");

        String result = authController.register(user);

        assertEquals("Введите почту", result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void register_mustReturnMessage_whenPasswordIsEmpty() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("ivan@test.com");
        user.setPasword("");

        String result = authController.register(user);

        assertEquals("Введите пароль", result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void register_mustReturnMessage_whenMailFormatIsInvalid() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("ivan_test.com");
        user.setPasword("123456");

        String result = authController.register(user);

        assertEquals("Неверный формат почты", result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void register_mustReturnMessage_whenPasswordTooShort() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("ivan@test.com");
        user.setPasword("123");

        String result = authController.register(user);

        assertEquals("Пароль должен быть не короче 6 символов", result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void register_mustReturnMessage_whenMailAlreadyExists() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("ivan@test.com");
        user.setPasword("123456");

        when(userRepo.findByMail("ivan@test.com")).thenReturn(Optional.of(new User()));

        String result = authController.register(user);

        assertEquals("Пользователь с такой почтой уже есть", result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void register_mustReturnMessage_whenRoleNotFound() {
        User user = new User();
        user.setFio("Иван Иванов");
        user.setMail("ivan@test.com");
        user.setPasword("123456");

        when(userRepo.findByMail("ivan@test.com")).thenReturn(Optional.empty());
        when(roleRepo.findByRole("user")).thenReturn(Optional.empty());

        String result = authController.register(user);

        assertEquals("ошибка выдачи роли", result);
        verify(userRepo, never()).save(any(User.class));
    }
}