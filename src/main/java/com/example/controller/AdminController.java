package com.example.controller;

import com.example.exception.UserServiceException;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import com.example.service.CourseService;
import com.example.service.UserService;
import com.example.util.Pdf;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static com.example.textconstants.Constants.*;
import static com.example.textconstants.Pages.*;
import static com.example.textconstants.Uri.*;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private UserService userService;
    private CourseService courseService;

    private UserRepository userRepository;
    private CourseRepository courseRepository;

    /**
     * @param model model
     * @return user catalogue page
     */
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> students = userService.findByRole(ROLE_STUDENT);
        List<User> teachers = userService.findByRole(ROLE_TEACHER);

        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);

        return USER_CATALOGUE_PAGE;
    }

    /**
     * @param model model
     * @return add course page
     */
    @GetMapping(ADD_COURSE)
    public String addCourseForm(Model model) {
        List<User> teachers = userService.findByRole(ROLE_TEACHER);

        model.addAttribute("teachers", teachers);
        model.addAttribute("course", new Course());
        return ADD_COURSE_PAGE;
    }

    @PostMapping("/add-course")
    public String addCourse(@Valid Course course,
                            BindingResult result,
                            @RequestParam(required = false) Long lecturerId,
                            Model model) {
        if (result.hasErrors()) {
            List<User> teachers = userService.findByRole(ROLE_TEACHER);
            model.addAttribute("teachers", teachers);
            return "addCourse";
        }

        try {
            courseService.addCourse(course, lecturerId);
        } catch (UserServiceException e) {
            model.addAttribute("dataError", e.getMessage());
            return "redirect:add-course";
        }

        return "redirect:/course-catalogue";
    }

    /**
     * @param model model
     * @return add teacher page
     */
    @GetMapping(ADD_TEACHER)
    public String addTeacherInfo(Model model) {
        List<User> students = userService.findNewUser(ROLE_STUDENT);
        List<Course> courses = courseService.findByCourseStatus(STATUS_CLOSED);
        List<User> teachers = userService.findByRole(ROLE_TEACHER);

        model.addAttribute("students", students);
        model.addAttribute("courses", courses);
        model.addAttribute("teachers", teachers);

        return ADD_TEACHER_PAGE;
    }

    @PostMapping(ADD_TEACHER)
    public String addTeacher(@RequestParam Long studentId,
                             @RequestParam Long courseId) {
        userService.createTeacher(ROLE_TEACHER, studentId);
        courseService.assignCourse(studentId, STATUS_OPEN, courseId);

        return "redirect:add-teacher";
    }


    @PostMapping("/assign-teacher")
    public String assignTeacher(@RequestParam Long teacherId,
                              @RequestParam Long courseId) {
        courseRepository.assignCourse(teacherId, STATUS_OPEN, courseId);
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

    @GetMapping("/update-course")
    public String updateCourseForm(Model model, @RequestParam Long courseId) {
        Course course = courseRepository.findById(courseId).get();
        model.addAttribute("course", course);
        return "updateCourse";
    }


    @PostMapping("/update-course")
    public String updateCourse(@Valid Course course,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            List<User> teachers = userRepository.findByRole(ROLE_TEACHER);
            model.addAttribute("teachers", teachers);
            return "updateCourse";
        }


        if(course.getIdLecturer() == null) {
            courseRepository.updateCourse(course.getName(), course.getTheme(),
                    course.getStartDate(), course.getEndDate(), course.getId());
        } else {
            courseRepository.updateCourse(course.getName(), course.getTheme(),
                    course.getStartDate(), course.getEndDate(), course.getIdLecturer().getId(), course.getId());
        }

        return "redirect:/course-catalogue";
    }

    @GetMapping(PDF)
    public ResponseEntity<InputStreamResource> pdfReport( @RequestParam String role ) {

        List<User> users = userRepository.findByRole(role);

        ByteArrayInputStream bis = Pdf.export(users, role);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=users-report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
