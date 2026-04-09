package chekizybra.otzovichok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fio;
    private String mail;
    private String pasword;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
