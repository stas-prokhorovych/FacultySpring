package com.example.controller;

import com.example.model.Course;
import com.example.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class CourseCatalogueController {
    private CourseRepository courseRepository;

    @GetMapping("/courseCatalogue")
    public String showCourseCatalogue(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "courseCatalogue";
    }
}
