package com.example.controller;

import com.example.model.Course;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.JournalService;
import com.example.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.textconstants.Constants.*;
import static com.example.textconstants.Pages.GRADUATES_PAGE;
import static com.example.textconstants.Pages.JOURNAL_PAGE;
import static com.example.textconstants.Uri.*;

@Controller
@RequestMapping(TEACHER)
@AllArgsConstructor
public class TeacherController {
    private UserService userService;
    private CourseService courseService;
    private JournalService journalService;

    /**
     * @param auth to get Principal
     * @param model model
     * @return journal page
     */
    @GetMapping(JOURNAL)
    public String journal(Authentication auth, Model model) {
        User customUser = (User) auth.getPrincipal();
        Long id = customUser.getId();

        List<Course> openForRegCourses = courseService.findCourseByStatusAndTeacherId(id, STATUS_OPEN);
        List<Course> inProgressCourses = courseService.findCourseByStatusAndTeacherId(id, STATUS_IN_PROGRESS);
        List<Course> finishedCourses = courseService.findCourseByStatusAndTeacherId(id, STATUS_FINISHED);

        model.addAttribute("openForRegCourses", openForRegCourses);
        model.addAttribute("inProgressCourses", inProgressCourses);
        model.addAttribute("finishedCourses", finishedCourses);

        return JOURNAL_PAGE;
    }

    /**
     * @param courseId id of course to start
     * @return redirect to journal route
     */
    @PostMapping(START_COURSE)
    public String startCourse(@RequestParam Long courseId) {
        courseService.startCourse(LocalDateTime.now(), STATUS_IN_PROGRESS, courseId);
        return "redirect:journal";
    }

    /**
     * @param courseId id of course that will be finished
     * @param studentIds id's of students that will finish course
     * @param studentMarks marks that correspond to each student
     * @return redirect to journal route
     */
    @PostMapping("/end-course")
    public String endCourse(@RequestParam Long courseId,
                            @RequestParam(value = "studentId") String[] studentIds,
                            @RequestParam(value = "mark") String[] studentMarks) {
        journalService.endCourse(courseId, studentIds, studentMarks);
        return "redirect:journal";
    }

    /**
     * @param courseId course to which show graduates
     * @param model model
     * @return graduates page
     */
    @PostMapping(SHOW_GRADUATES)
    public String showGraduates(@RequestParam Long courseId, Model model) {
        List<User> graduates = userService.findAllGraduates(courseId);
        model.addAttribute("graduates", graduates);
        model.addAttribute("courseId", courseId);
        return GRADUATES_PAGE;
    }
}
