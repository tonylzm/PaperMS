package usts.paperms.paperms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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


    @Column(name = "username")
    private String username;
    @Column(name = "password")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    @Column(name = "real_name")
    private String realName;
    @Column(name = "college")
    private String college;
    @Column(name = "tel")
    private String tel;
    @Column(name = "email")
    private String email;


    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private UserRole userRole;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Salt salt;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Key key;
}