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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class CourseCatalogueController {
    private CourseRepository courseRepository;
    private UserRepository userRepository;

    @GetMapping(value = "/course-catalogue")
    public String showCourseCatalogue(Model model,
                                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable,
                                      @RequestParam(value = "page", required = false) Integer page
                                      ) {

        List<String> themesForForm = courseRepository.findAllThemes();
        List<User> teacherForForm = userRepository.findByRole("Teacher");
        Page<Course> courses = courseRepository.findAll(pageable);



        if(page == null) {
            page = 0;
        }

        model.addAttribute("page", page);
        model.addAttribute("noOfPages", courses.getTotalPages());
        model.addAttribute("url", "/course-catalogue");
        model.addAttribute("themesForForm", themesForForm);
        model.addAttribute("teacherForForm", teacherForForm);
        model.addAttribute("courses", courses);
        return "courseCatalogue";
    }
}
