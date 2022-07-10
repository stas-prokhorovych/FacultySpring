package com.example.controller;

import com.example.model.Course;
import com.example.model.Journal;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.JournalRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/student")
@AllArgsConstructor
public class StudentController {
    private CourseRepository courseRepository;
    private JournalRepository journalRepository;
    private UserRepository userRepository;

    @GetMapping("/courses")
    public String yourCourses(Authentication auth, Model model) {
        User customUser = (User) auth.getPrincipal();
        Long id = customUser.getId();

        List<Course> registeredCourses = courseRepository.findStudentCourseByStatus("Opened for registration", id);
        List<Course> inProgressCourses = courseRepository.findStudentCourseByStatus("In progress", id);
        List<Course> finishedCourses = courseRepository.findStudentCourseByStatus("Finished", id);

        List<Journal> journalInfo = new LinkedList<>();
        for(Course course: finishedCourses) {
            journalInfo.add(journalRepository.findMarksByCourse(id, course.getId()));
            System.out.println(journalRepository.findMarksByCourse(id, course.getId()));
        }

        model.addAttribute("registeredCourses", registeredCourses);
        model.addAttribute("inProgressCourses", inProgressCourses);
        model.addAttribute("finishedCourses", finishedCourses);
        model.addAttribute("journalInfo", journalInfo);

        return "studentCourses";
    }

    @PostMapping("/enroll")
    public String enroll(Authentication auth, @RequestParam Long courseId) {
        User customUser = (User) auth.getPrincipal();
        Long userId = customUser.getId();

        userRepository.enrollStudentOnCourse(userId, courseId);

        return "redirect:/course-catalogue";
    }

    @PostMapping("/leave")
    public String leave(Authentication auth, @RequestParam Long courseId) {
        User customUser = (User) auth.getPrincipal();
        Long userId = customUser.getId();

        userRepository.leaveCourse(userId, courseId);

        return "redirect:/course-catalogue";
    }
}
