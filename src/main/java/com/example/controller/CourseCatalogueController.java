package com.example.controller;

import com.example.model.Course;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.PaginationService;
import com.example.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

import static com.example.textconstants.Constants.ROLE_STUDENT;
import static com.example.textconstants.Constants.ROLE_TEACHER;
import static com.example.textconstants.Pages.COURSE_CATALOGUE_PAGE;
import static com.example.textconstants.Uri.COURSE_CATALOGUE;

@Controller
@AllArgsConstructor
public class CourseCatalogueController {
    private PaginationService paginationService;
    private UserService userService;
    private CourseService courseService;

    /**
     * @param model model
     * @param principal current log in user if present
     * @param page current page
     * @param size number of pages
     * @param theme selected theme if present
     * @param teacher selected teacher if present
     * @param sortWay sort way if present
     * @param order order of sorting if present
     * @return course catalogue page
     */
    @GetMapping( COURSE_CATALOGUE)
    public String showCourseCatalogue(Model model,
                                      Principal principal,
                                      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
                                      @RequestParam(required = false, defaultValue = "") String theme,
                                      @RequestParam(required = false, defaultValue = "") String teacher,
                                      @RequestParam(required = false, defaultValue = "") String sortWay,
                                      @RequestParam(required = false, defaultValue = "") String order
    ) {
        Page<Course> courses = paginationService.findCoursesByParameters(page, size, theme, teacher, sortWay, order);
        List<String> themesForForm = courseService.findAllThemes();
        List<User> teacherForForm = userService.findByRole(ROLE_TEACHER);
        List<Integer> studentsEnrolled = courseService.findStudentsEnrolled(courses.getContent());
        List<User> teachers = userService.findCoursesTeachers(courses.getContent());

        if (principal != null) {
            User user = userService.findUserByLogin(principal.getName());
            if (user.getRole().equals(ROLE_STUDENT)) {
                List<Boolean> courseAlreadySelected = courseService.findSelectedCourse( courses.getContent(), user.getId());
                model.addAttribute("courseAlreadySelected", courseAlreadySelected);
            }
        }

        model.addAttribute("theme", theme);
        model.addAttribute("teacher", teacher);
        model.addAttribute("sortWay", sortWay);
        model.addAttribute("order", order);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("noOfPages", courses.getTotalPages());
        model.addAttribute("url", "/course-catalogue");
        model.addAttribute("themesForForm", themesForForm);
        model.addAttribute("teacherForForm", teacherForForm);
        model.addAttribute("courses", courses);
        model.addAttribute("studentsEnrolled", studentsEnrolled);
        model.addAttribute("teachers", teachers);
        return COURSE_CATALOGUE_PAGE;
    }
}
