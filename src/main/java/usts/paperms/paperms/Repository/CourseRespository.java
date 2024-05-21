package usts.paperms.paperms.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Course;

import java.util.List;

@Repository
public interface CourseRespository extends JpaRepository<Course, String> {


    @Modifying
    @Query(value = "UPDATE `sys_course` c SET c.`course_teacher` = null", nativeQuery = true)
    void resetAllCourseTeachers();

    //通过课程名称查找课程
    Course findByCourseName(String courseName);
    //通过教师查找课程
    List<Course> findByCourseTeacher(String courseTeacher);

}
