package usts.paperms.paperms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sys_historychecked")
public class historychecked {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private Long size;

    @Column(name = "url")
    @JsonIgnore
    private String url;

    @Column(name = "md5")
    @JsonIgnore
    private String md5;

    @Column(name="produced")
    private String produced;


    @Column(name = "is_decrypt")
    private boolean decrypt;

    @Column(name = "enable")
    private boolean enable;

    @Column(name ="classes")
    private String classes;

    @Column(name ="college")
    private String college;

    @Column(name ="status")
    private String status;

    @Column(name = "opinion",columnDefinition = "TEXT")
    private String opinion;

    @Column(name ="Date" , columnDefinition = "TIMESTAMP")
    private String Date;

    @Column(name ="interval_time")
    private String intervalTime;
}
