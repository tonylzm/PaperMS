package usts.paperms.paperms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_course")
public class Course {
    @Id
    private String course_id;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_teacher")
    private String courseTeacher;

}
