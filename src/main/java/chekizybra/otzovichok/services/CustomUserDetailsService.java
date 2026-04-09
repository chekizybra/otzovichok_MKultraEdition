package chekizybra.otzovichok.services;

import chekizybra.otzovichok.model.User;
import chekizybra.otzovichok.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = repo.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        String roleName = "user";

        if (user.getRole() != null && user.getRole().getRole() != null) {
            roleName = user.getRole().getRole();
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getMail())
                .password(user.getPasword())
                .roles(roleName)
                .build();
    }
}