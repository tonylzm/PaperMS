package usts.paperms.paperms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.security.ValidateToken;
import usts.paperms.paperms.service.CourseService;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    //查询所有课程
    @ValidateToken
    @GetMapping("/all_page")
    public Result allCourse(int pageNum, int pageSize ,
                            @RequestParam(defaultValue = "") String name,
                            @RequestParam("college") String college){
        return Result.success(courseService.findAll(pageNum, pageSize,name,college));
    }
    //查询课程，id
    @ValidateToken
    @GetMapping("/find_course")
    public Result findCourse(@RequestParam("course_id") String course_id){
        return Result.success(courseService.findCourse(course_id));
    }
    //查询课程，name
    @ValidateToken
    @GetMapping("/find_name")
    public Result findCourseName(@RequestParam("course_name") String course_name){
        return Result.success(courseService.findCourseByName(course_name));
    }
    //查找教师所教课程
    @ValidateToken
    @GetMapping("/find_teacher")
    public Result findTeacherCourse(@RequestParam("teacher") String teacher){
        return Result.success(courseService.findCourseByTeacher(teacher));
    }
    //更新课程教师
    @ValidateToken
    @PostMapping("/update_teacher")
    public Result updateCourse(@RequestParam("Id") String Id, @RequestParam("teacher") String teacher){
        return Result.success(courseService.updateCourse(Id,teacher));
    }
    //重置所有课程教师
    @ValidateToken
    @PostMapping("/reset_all_teachers")
    public Result resetAllCourse(){
        return Result.success(courseService.restAllCourse());
    }
    //添加课程
    @ValidateToken
    @PostMapping("/add_course")
    public Result addCourse(@RequestParam("course_id") String course_id,
                            @RequestParam("course_name") String course_name,
                            @RequestParam("course_teacher") String course_teacher,
                            @RequestParam("college") String college){
        return Result.success(courseService.addCourse(course_id,course_name,course_teacher,college));
    }
    //批量添加课程
    @ValidateToken
    @PostMapping("/add_more_course")
    public Result addMoreCourse(@RequestParam("file") MultipartFile file,
                                @RequestParam("college") String courseCollege){
        //检测文件是否为excel文件
        return Result.success(courseService.addMoreCourse(file,courseCollege));
    }
    //删除课程
    @ValidateToken
    @PostMapping("/delete_course")
    public Result deleteCourse(@RequestParam("course_id") String course_id){
        return Result.success(courseService.deleteCourse(course_id));
    }
    //删除课程，name
    @ValidateToken
    @PostMapping("/delete_name")
    public Result deleteCourseName(@RequestParam("course_name") String course_name){
        return Result.success(courseService.deleteCourseByName(course_name));
    }
    //删除所有课程
    @ValidateToken
    @PostMapping("/delete_all")
    public Result deleteAllCourse( @RequestParam("college") String courseCollege){
        return Result.success(courseService.deleteAllCourse(courseCollege));
    }

}
