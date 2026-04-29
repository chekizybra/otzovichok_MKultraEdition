package chekizybra.otzovichok.controller;

import chekizybra.otzovichok.model.Role;
import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.repository.RoleRepository;
import chekizybra.otzovichok.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (user.getFio() == null || user.getFio().isBlank()) {
            return "Введите ФИО";
        }

        if (user.getMail() == null || user.getMail().isBlank()) {
            return "Введите почту";
        }

        if (!user.getMail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Неверный формат почты";
        }

        if (user.getPasword() == null || user.getPasword().isBlank()) {
            return "Введите пароль";
        }

        if (user.getPasword().length() < 6) {
            return "Пароль должен быть не короче 6 символов";
        }

        if (userRepo.findByMail(user.getMail()).isPresent()) {
            return "Пользователь с такой почтой уже есть";
        }

        Role role = roleRepo.findByRole("user").orElse(null);
        if (role == null) {
            return "ошибка выдачи роли";
        }

        user.setPasword(encoder.encode(user.getPasword()));
        user.setRole(role);

        userRepo.save(user);
        return "ok";
    }
}