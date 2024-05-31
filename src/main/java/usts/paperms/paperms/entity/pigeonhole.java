package usts.paperms.paperms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pigeonhole")
public class pigeonhole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @Column(name = "md5")
    @JsonIgnore
    private String md5;

    @Column(name="produced")
    private String produced;

    @Column(name = "is_decrypt")
    private boolean decrypt;

    @Column(name = "is_delete")
    private boolean delete;

    @Column(name ="classes")
    private String classes;

    @Column(name ="college")
    private String college;

}
