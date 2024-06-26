package usts.paperms.paperms.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //通过课程名称查找课程，如果名称为空，则返回所有课程
    Page<Course> findByCourseNameContainingAndCourseCollege(String courseName,String courseCollege, Pageable pageable);

    //通过课程名称查找课程
    Course findByCourseName(String courseName);
    //通过教师查找课程
    List<Course> findByCourseTeacher(String courseTeacher);

    //删除所有课程通过课程学院
    void deleteAllByCourseCollege(String courseCollege);
}
