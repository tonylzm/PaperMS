package usts.paperms.paperms.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usts.paperms.paperms.entity.Course;

@Repository
public interface CourseRespository extends JpaRepository<Course, String> {


    Course findBYCourseName(String courseName);
    @Modifying
    @Query(value = "UPDATE `sys_course` c SET c.`course_teacher` = null", nativeQuery = true)
    void resetAllCourseTeachers();
}
