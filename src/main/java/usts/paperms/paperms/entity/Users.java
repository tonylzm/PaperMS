package usts.paperms.paperms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "test_user")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String username;
    private String password;
    private String realName;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<UserRole> roles;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    private Salt salt;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    private Key key;
}