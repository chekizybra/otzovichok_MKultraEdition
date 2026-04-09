package chekizybra.otzovichok.controller;

import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ProfileController {

    private final UserRepository userRepo;

    public ProfileController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        Map<String, Object> r = new LinkedHashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            r.put("message", "not auth");
            return r;
        }

        User user = userRepo.findByMail(authentication.getName()).orElse(null);
        if (user == null) {
            r.put("message", "user not found");
            return r;
        }

        r.put("fio", user.getFio());
        r.put("mail", user.getMail());
        r.put("role", user.getRole() != null ? user.getRole().getRole() : null);

        return r;
    }
}