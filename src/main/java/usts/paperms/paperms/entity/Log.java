package usts.paperms.paperms.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sys_log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private String date;

    @Column(name ="action")
    private String action;

    @Column(name ="actor")
    private String actor;
}
