package usts.paperms.paperms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_key")
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "key_id", referencedColumnName = "id",unique = true)
    private Users users;

    @Column(name = "key_name")
    private String keyName;

    @Column(name = "key_public", columnDefinition = "TEXT")
    private String keyPublic;

    @Column(name = "key_private", columnDefinition = "TEXT")
    private String keyPrivate;

}
