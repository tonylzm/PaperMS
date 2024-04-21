package usts.paperms.paperms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "sys_check")
public class Check {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "check_id", referencedColumnName = "id",unique = true)
    private SysFile sysFile;

    @Column(name="class_check")
    private String classCheck;

    @Column(name="college_check")
    private String collegeCheck;

    @Column(name="opinion")
    private String opinion;

    @Column(name="check_status")
    private String checkStatus;

}
