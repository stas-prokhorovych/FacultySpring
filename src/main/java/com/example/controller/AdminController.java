package com.example.controller;

import com.example.dto.CourseDTO;
import com.example.exception.CourseServiceException;
import com.example.exception.UserServiceException;
import com.example.model.Course;
import com.example.model.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.List;

import static com.example.textconstants.Constants.*;
import static com.example.textconstants.Pages.*;
import static com.example.textconstants.Uri.*;

@Controller
@RequestMapping(ADMIN)
@AllArgsConstructor
public class AdminController {
    private UserService userService;
    private CourseService courseService;

    /**
     * @param model model
     * @return user catalogue page
     */
    @GetMapping(USERS)
    public String showUsers(Model model) {
        List<User> students = userService.findByRole(ROLE_STUDENT);
        List<User> teachers = userService.findByRole(ROLE_TEACHER);

        model.addAttribute(ATTR_STUDENTS, students);
        model.addAttribute(ATTR_TEACHERS, teachers);

        return USER_CATALOGUE_PAGE;
    }

    /**
     * @param model model
     * @return add course page
     */
    @GetMapping(ADD_COURSE)
    public String addCourseForm(Model model) {
        List<User> teachers = userService.findByRole(ROLE_TEACHER);
        model.addAttribute(ATTR_TEACHERS, teachers);
        model.addAttribute("courseDTO", new CourseDTO());
        return ADD_COURSE_PAGE;
    }

    /**
     * @param courseDTO  courseDTO that stores inputted data
     * @param result     validation results
     * @param lecturerId id of lecturer if present
     * @param model      model
     * @return add course page if error,
     * redirect to add teacher page if teacher empty,
     * redirect to course page if all ok
     */
    @PostMapping(ADD_COURSE)
    public String addCourse(@Valid CourseDTO courseDTO,
                            BindingResult result,
                            @RequestParam(required = false) Long lecturerId,
                            Model model) {
        if (result.hasErrors()) {
            List<User> teachers = userService.findByRole(ROLE_TEACHER);
            model.addAttribute(ATTR_TEACHERS, teachers);
            return ADD_COURSE_PAGE;
        }

        try {
            courseService.addCourse(courseDTO, lecturerId);
        } catch (CourseServiceException e) {
            List<User> teachers = userService.findByRole(ROLE_TEACHER);
            model.addAttribute(ATTR_TEACHERS, teachers);
            model.addAttribute(ATTR_DATA_ERROR, e.getMessage());
            return ADD_COURSE_PAGE;
        }

        if (lecturerId == null) {
            return REDIRECT + ADD_TEACHER_PAGE;
        }

        return REDIRECT + COURSE_CATALOGUE;
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

        model.addAttribute(ATTR_STUDENTS, students);
        model.addAttribute(ATTR_COURSES, courses);
        model.addAttribute(ATTR_TEACHERS, teachers);

        return ADD_TEACHER_PAGE;
    }

    /**
     * @param studentId student to become teacher
     * @param courseId  course that this teacher will teach
     * @return redirect to add teacher route
     */
    @PostMapping(ADD_TEACHER)
    public String addTeacher(@RequestParam Long studentId,
                             @RequestParam Long courseId) {
        userService.createTeacher(ROLE_TEACHER, studentId);
        courseService.assignCourse(studentId, STATUS_OPEN, courseId);
        return "redirect:add-teacher";
    }

    /**
     * @param teacherId teacher to teach this course
     * @param courseId  course to teach
     * @return redirect to add teacher route
     */
    @PostMapping("/assign-teacher")
    public String assignTeacher(@RequestParam Long teacherId,
                                @RequestParam Long courseId) {
        courseService.assignCourse(teacherId, STATUS_OPEN, courseId);
        return "redirect:add-teacher";
    }

    /**
     * @param studentId  student to who change access
     * @param userAccess 1 or 0, has access or don't have access
     * @return redirect to users route
     */
    @PostMapping("/student-access")
    public String userAccess(@RequestParam Long studentId,
                             @RequestParam Integer userAccess,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserAccess(userAccess, studentId);
        } catch (UserServiceException e) {
            redirectAttributes.addFlashAttribute(ATTR_DATA_ERROR, e.getMessage());
        }
        return "redirect:users";
    }

    /**
     * @param courseId course to delete
     * @return redirect to course catalogue route
     */
    @PostMapping("/delete-course")
    public String deleteCourse(@RequestParam Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteById(courseId);
        } catch (CourseServiceException e) {
            redirectAttributes.addFlashAttribute(ATTR_DATA_ERROR, e.getMessage());
        }

        return REDIRECT + COURSE_CATALOGUE;
    }

    /**
     * @param model model
     * @param courseId course id to update
     * @return update course page
     */
    @GetMapping(UPDATE_COURSE)
    public String updateCourseForm(Model model, @RequestParam Long courseId) {
        Course course;
        CourseDTO courseDTO = new CourseDTO();
        try {
            course = courseService.findById(courseId);
            courseDTO.setName(course.getName());
            courseDTO.setTheme(course.getTheme());
            courseDTO.setStartDate(course.getStartDate());
            courseDTO.setEndDate(course.getEndDate());
        } catch (UserServiceException e) {
            model.addAttribute(ATTR_DATA_ERROR, e.getMessage());
            return HOME_PAGE;
        }
        List<User> teachers = userService.findByRole(ROLE_TEACHER);

        model.addAttribute(ATTR_TEACHERS, teachers);
        model.addAttribute("courseDTO", courseDTO);
        model.addAttribute(ATTR_COURSE, course);
        model.addAttribute("updateCourseId", courseId);
        return UPDATE_COURSE_PAGE;
    }

    /**
     * @param courseDTO courseDTO that stores inputted data
     * @param result validation results
     * @param updateCourseId id of course to update
     * @param lecturerId new teacher id if present
     * @param model model
     * @return if errors then update course page,
     *         redirect to course catalogue if success update
     */
    @PostMapping(UPDATE_COURSE)
    public String updateCourse(@Valid CourseDTO courseDTO,
                               BindingResult result,
                               @RequestParam Long updateCourseId,
                               @RequestParam(required = false) Long lecturerId,
                               Model model) {
        if (result.hasErrors()) {
            List<User> teachers = userService.findByRole(ROLE_TEACHER);
            model.addAttribute(ATTR_TEACHERS, teachers);
            return UPDATE_COURSE_PAGE;
        }

        try {
            courseService.updateCourse(courseDTO, updateCourseId, lecturerId);
        } catch (CourseServiceException e) {
            model.addAttribute(ATTR_DATA_ERROR, e.getMessage());
            return UPDATE_COURSE_PAGE;
        }

        return REDIRECT + COURSE_CATALOGUE;
    }

    /**
     * @param role role of users
     * @return pdf file
     */
    @GetMapping(PDF)
    public ResponseEntity<InputStreamResource> pdfReport(@RequestParam String role) {
        List<User> users = userService.findByRole(role);
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
