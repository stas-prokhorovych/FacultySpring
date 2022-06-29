package com.example.controller;

import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private UserRepository userRepository;
    private CourseRepository courseRepository;

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> students = userRepository.getAllUsersByRole("Student");
        List<User> teachers = userRepository.getAllUsersByRole("Teacher");

        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);

        return "userCatalogue";
    }

    @GetMapping("/add-course")
    public String addCourseForm(Model model) {
        List<User> teachers = userRepository.findByRole("Teacher");
        model.addAttribute("teachers", teachers);
        model.addAttribute("course", new Course());
        return "addCourse";
    }

    @PostMapping("/add-course")
    public String addCourse(@Valid Course course, BindingResult result,
                            @RequestParam Long lecturerId,
                            Model model) {
        if (result.hasErrors()) {
            List<User> teachers = userRepository.findByRole("Teacher");
            model.addAttribute("teachers", teachers);
            return "addCourse";
        }

        Optional<Course> courseFromDb = courseRepository.findByName(course.getName());
        if (courseFromDb.isPresent()) {
            model.addAttribute("dataError", "Course with such name already exist");
            return "redirect:add-course";
        }

        if(lecturerId == null) {
            course.setIdLecturer(null);
            course.setCourseStatus("Closed, no teacher assigned yet");
        } else {
            User teacher = userRepository.findUserById(lecturerId);
            course.setIdLecturer(teacher);
            course.setCourseStatus("Opened for registration");
        }

        courseRepository.addCourse(course);

        return "redirect:add-course";
    }

    @GetMapping("/add-teacher")
    public String addTeacherInfo(Model model) {
        List<User> students = userRepository.findNewUser("Student");
        List<Course> courses = courseRepository.findByCourseStatus("Closed, no teacher assigned yet");
        List<User> teachers = userRepository.getAllUsersByRole("Teacher");

        model.addAttribute("students", students);
        model.addAttribute("courses", courses);
        model.addAttribute("teachers", teachers);

        return "addTeacher";
    }

    @PostMapping("/add-teacher")
    public String addTeacher(@RequestParam Long studentId,
                             @RequestParam Long courseId) {
        userRepository.createTeacher("Teacher", studentId);
        courseRepository.assignCourse(studentId, "Opened for registration", courseId);
        return "redirect:add-teacher";
    }


    @PostMapping("/assign-teacher")
    public String assignTeacher(@RequestParam Long teacherId,
                              @RequestParam Long courseId) {
        courseRepository.assignCourse(teacherId, "Opened for registration", courseId);
        return "redirect:add-teacher";
    }


    @PostMapping("/student-access")
    public String userAccess(@RequestParam Long studentId,
                             @RequestParam Integer userAccess) {
        userRepository.changeUserAccess(userAccess, studentId);
        return "redirect:users";
    }


    @PostMapping("/delete-course")
    public String deleteCourse(@RequestParam Long courseId) {
        courseRepository.deleteById(courseId);
        return "redirect:/course-catalogue";
    }


    @PostMapping("/update-course")
    public String updateCourse(@RequestParam Long courseId) {


        return "redirect:/course-catalogue";
    }
}
