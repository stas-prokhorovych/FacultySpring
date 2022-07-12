package com.example.controller;

import com.example.exception.UserServiceException;
import com.example.model.Course;
import com.example.model.Journal;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.CourseStudentService;
import com.example.service.JournalService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;

import static com.example.textconstants.Pages.STUDENT_COURSES_PAGE;
import static com.example.textconstants.Uri.*;

@Controller
@RequestMapping(STUDENT)
@AllArgsConstructor
public class StudentController {
    private JournalService journalService;
    private CourseStudentService courseStudentService;
    private CourseService courseService;

    /**
     * @param auth to get Principal
     * @param model model
     * @return student courses page
     */
    @GetMapping(COURSES)
    public String yourCourses(Authentication auth, Model model) {
        User customUser = (User) auth.getPrincipal();
        Long id = customUser.getId();

        List<Course> registeredCourses = courseService.findStudentCourseByStatus("Opened for registration", id);
        List<Course> inProgressCourses = courseService.findStudentCourseByStatus("In progress", id);
        List<Course> finishedCourses = courseService.findStudentCourseByStatus("Finished", id);

        List<Journal> journalInfo = new LinkedList<>();
        for(Course course: finishedCourses) {
            journalInfo.add(journalService.findMarksByCourse(id, course.getId()));
        }

        model.addAttribute("registeredCourses", registeredCourses);
        model.addAttribute("inProgressCourses", inProgressCourses);
        model.addAttribute("finishedCourses", finishedCourses);
        model.addAttribute("journalInfo", journalInfo);

        return STUDENT_COURSES_PAGE;
    }

    /**
     * @param auth to get Principal
     * @param courseId id of course to enroll
     * @param redirectAttributes message send if course already enrolled
     * @return redirect to course catalogue
     */
    @PostMapping(ENROLL)
    public String enroll(Authentication auth, @RequestParam Long courseId, RedirectAttributes redirectAttributes) {
        User currentStudent = (User) auth.getPrincipal();
        Long userId = currentStudent.getId();

        try {
            courseStudentService.enrollStudentOnCourse(courseId, userId);
        } catch (UserServiceException e) {
            redirectAttributes.addFlashAttribute("dataError", e.getMessage());
        }

        return REDIRECT + COURSE_CATALOGUE;
    }

    /**
     * @param auth to get Principal
     * @param courseId id of course to leave
     * @param redirectAttributes message send if course already left
     * @return redirect to course catalogue
     */
    @PostMapping(LEAVE)
    public String leave(Authentication auth, @RequestParam Long courseId, RedirectAttributes redirectAttributes) {
        User currentStudent = (User) auth.getPrincipal();
        Long userId = currentStudent.getId();

        try {
            courseStudentService.leaveCourse(courseId, userId);
        } catch (UserServiceException e) {
            redirectAttributes.addFlashAttribute("dataError", e.getMessage());
        }

        return REDIRECT + COURSE_CATALOGUE;
    }
}
