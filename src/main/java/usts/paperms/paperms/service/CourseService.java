package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import usts.paperms.paperms.Repository.CourseRespository;
import usts.paperms.paperms.entity.Course;

@Service
public class CourseService {
    @Autowired
    private CourseRespository courseRespository;

    // 分页查询所有课程
    public Page<Course> findAll(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return courseRespository.findAll(pageable);
    }

    // 更新课程教师
    public String updateCourse(String Id,String teacher){
        Course course = courseRespository.findById(Id).get();
        course.setCourseTeacher(teacher);
        courseRespository.save(course);
        return "success";
    }

    // 重置所有课程教师
    @Transactional
    public String restAllCourse(){
        courseRespository.resetAllCourseTeachers();
        return "success";
    }

    // 添加课程
    public String addCourse(String course_id,String course_name,String course_teacher){
        Course course = new Course();
        course.setCourse_id(course_id);
        course.setCourseName(course_name);
        course.setCourseTeacher(course_teacher);
        courseRespository.save(course);
        return "success";
    }

    // 批量添加课程
    public String addMoreCourse(MultipartFile file) {
        try {
            // 创建一个Workbook对象，这个对象代表了Excel文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            // 获取第一个Sheet
            Sheet sheet = workbook.getSheetAt(0);
            // 遍历每一行数据，跳过第一行（表头）
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                // 创建一个Course对象
                Course course = new Course();
                // 设置Course的属性
                course.setCourse_id(row.getCell(0).getStringCellValue());
                course.setCourseName(row.getCell(1).getStringCellValue());
                // 保存Course对象到数据库
                courseRespository.save(course);
            }
            // 关闭Workbook
            workbook.close();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    //删除课程
    public String deleteCourse(String course_id){
        courseRespository.deleteById(course_id);
        return "success";
    }
    //查询课程
    public Course findCourse(String course_id) {
        return courseRespository.findById(course_id).get();
    }

    //查询课程，通过课程名
    public Course findCourseByName(String course_name) {
        return courseRespository.findByCourseName(course_name);
    }

    //删除课程,通过课程名
    public String deleteCourseByName(String course_name){
        Course course = courseRespository.findByCourseName(course_name);
        courseRespository.deleteById(course.getCourse_id());
        return "success";
    }

    //删除所有课程
    public String deleteAllCourse(){
        courseRespository.deleteAll();
        return "success";
    }
}
