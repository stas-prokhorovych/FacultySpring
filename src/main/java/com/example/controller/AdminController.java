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

import static com.example.textconstants.Pages.SIGNUP_PAGE;

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
        model.addAttribute("course", new Course());
        return "addCourse";
    }

    @PostMapping("/add-course")
    public String addCourse(@Valid Course course, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return "addCourse";
        }

        Optional<Course> courseFromDb = courseRepository.findByName(course.getName());
        if(courseFromDb.isPresent()) {
            model.addAttribute("dataError", "Course with such name already exist");
            return SIGNUP_PAGE;
        }

        return "redirect:addCourse";
    }

    @GetMapping("/add-teacher")
    public String addTeacher(Model model) {
        List<User> students = userRepository.findNewUser("Student");
        List<Course> courses = courseRepository.findByCourseStatus("Closed, no teacher assigned yet");
        List<User> teachers = userRepository.getAllUsersByRole("Teacher");

        model.addAttribute("students", students);
        model.addAttribute("courses", courses);
        model.addAttribute("teachers", teachers);

        return "addTeacher";
    }

    @PostMapping("/student-access")
    public String userAccess(@RequestParam Long studentId, Model model) {
//        userRepository.changeUserAccess(studentId);
        return "userCatalogue";
    }
}
