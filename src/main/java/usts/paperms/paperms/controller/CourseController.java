package usts.paperms.paperms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.service.CourseService;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/all_page")
    public Result allCourse(int pageNum, int pageSize) {
        return Result.success(courseService.findAll(pageNum, pageSize));
    }

    @GetMapping("/find_course")
    public Result findCourse(@RequestParam("course_id") String course_id){
        return Result.success(courseService.findCourse(course_id));
    }

    @GetMapping("/find_name")
    public Result findCourseName(@RequestParam("course_name") String course_name){
        return Result.success(courseService.findCourseByName(course_name));
    }


    @PostMapping("/update_teacher")
    public Result updateCourse(@RequestParam("Id") String Id, @RequestParam("teacher") String teacher){
        return Result.success(courseService.updateCourse(Id,teacher));
    }

    @PostMapping("/reset_all_teachers")
    public Result resetAllCourse(){
        return Result.success(courseService.restAllCourse());
    }

    @PostMapping("/add_course")
    public Result addCourse(@RequestParam("course_id") String course_id,
                            @RequestParam("course_name") String course_name,
                            @RequestParam("course_teacher") String course_teacher){
        return Result.success(courseService.addCourse(course_id,course_name,course_teacher));
    }

    @PostMapping("/add_more_course")
    public Result addMoreCourse(@RequestParam("file") MultipartFile file) {
        //检测文件是否为excel文件
        return Result.success(courseService.addMoreCourse(file));
    }

    @PostMapping("/delete_course")
    public Result deleteCourse(@RequestParam("course_id") String course_id){
        return Result.success(courseService.deleteCourse(course_id));
    }

    @PostMapping("/delete_name")
    public Result deleteCourseName(@RequestParam("course_name") String course_name){
        return Result.success(courseService.deleteCourseByName(course_name));
    }


}
