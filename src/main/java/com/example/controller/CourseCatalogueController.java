package com.example.controller;

import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@Controller
@AllArgsConstructor
public class CourseCatalogueController {
    private CourseRepository courseRepository;
    private UserRepository userRepository;

    @GetMapping(value = "/course-catalogue")
    public String showCourseCatalogue(Model model,
                                      Principal principal,
                                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable,
                                      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size
    ) {


//        pageable = PageRequest.of(page, size, Sort.by("name").descending());



        List<String> themesForForm = courseRepository.findAllThemes();
        List<User> teacherForForm = userRepository.findByRole("Teacher");

        Page<Course> courses;
        if (principal != null) {
            courses = courseRepository.findByCourseStatusNot("Closed, no teacher assigned yet", pageable);
        } else {
            courses = courseRepository.findByCourseStatus("Opened for registration", pageable);
        }

        List<Integer> studentsEnrolled = new LinkedList<>();
        List<User> teachers = new LinkedList<>();
        for (Course course : courses.getContent()) {
            studentsEnrolled.add(courseRepository.findStudentsEnrolled(course.getId()));
            teachers.add(userRepository.findUserById(course.getIdLecturer().getId()));
        }


        if (principal != null) {
            String login = principal.getName();
            User user = userRepository.findUserByLogin(login).get();
            if (user.getRole().equals("Student")) {
                Long studentId = user.getId();
                List<Boolean> courseAlreadySelected = new LinkedList<>();
                for (Course course : courses.getContent()) {
                    Integer value = courseRepository.findSelectedCourse(studentId, course.getId());
                    if (value == null) {
                        courseAlreadySelected.add(false);
                    } else {
                        courseAlreadySelected.add(true);
                    }
                }
                model.addAttribute("courseAlreadySelected", courseAlreadySelected);
            }
        }


        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("noOfPages", courses.getTotalPages());
        model.addAttribute("url", "/course-catalogue");
        model.addAttribute("themesForForm", themesForForm);
        model.addAttribute("teacherForForm", teacherForForm);
        model.addAttribute("courses", courses);
        model.addAttribute("studentsEnrolled", studentsEnrolled);
        model.addAttribute("teachers", teachers);
        return "courseCatalogue";
    }
}
