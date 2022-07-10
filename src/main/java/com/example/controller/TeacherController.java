package com.example.controller;

import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.JournalRepository;
import com.example.repository.UserRepository;
import com.example.textconstants.Mark;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/teacher")
@AllArgsConstructor
public class TeacherController {
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private JournalRepository journalRepository;

    @GetMapping("/journal")
    public String journal(Authentication auth, Model model) {
        User customUser = (User) auth.getPrincipal();
        Long id = customUser.getId();

        List<Course> openForRegCourses = courseRepository.findCourseByStatusAndTeacherId(id, "Opened for registration");
        List<Course> inProgressCourses = courseRepository.findCourseByStatusAndTeacherId(id, "In progress");
        List<Course> finishedCourses = courseRepository.findCourseByStatusAndTeacherId(id, "Finished");

        model.addAttribute("openForRegCourses", openForRegCourses);
        model.addAttribute("inProgressCourses", inProgressCourses);
        model.addAttribute("finishedCourses", finishedCourses);

        return "journal";
    }

    @PostMapping("/start-course")
    public String startCourse(@RequestParam Long courseId) {
        courseRepository.startCourse(LocalDateTime.now(), "In progress", courseId);
        return "redirect:journal";
    }

    @PostMapping("/end-course")
    public String endCourse(@RequestParam Long courseId,
                            @RequestParam(value = "studentId") String[] studentIds,
                            @RequestParam(value = "mark") String[] studentMarks) {

        int[] marks = Stream
                .of(studentMarks)
                .mapToInt(Integer::parseInt)
                .toArray();
        String[] markCode = new String[marks.length];
        String[] markExplanation = new String[marks.length];

        for (int i = 0; i < marks.length; i++) {
            Mark markInformation;
            if (marks[i] >= 90 && marks[i] <= 100) {
                markInformation = Mark.A;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
            if (marks[i] >= 81 && marks[i] <= 89) {
                markInformation = Mark.B;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
            if (marks[i] >= 75 && marks[i] <= 80) {
                markInformation = Mark.C;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
            if (marks[i] >= 65 && marks[i] <= 74) {
                markInformation = Mark.D;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
            if (marks[i] >= 55 && marks[i] <= 64) {
                markInformation = Mark.E;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
            if (marks[i] >= 30 && marks[i] <= 54) {
                markInformation = Mark.FX;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
            if (marks[i] >= 1 && marks[i] <= 29) {
                markInformation = Mark.F;
                markCode[i] = markInformation.getCode();
                markExplanation[i] = markInformation.getExplanation();
            }
        }

        for (int i = 0; i < marks.length; i++) {
            journalRepository.endCourse(studentIds[i], marks[i], markCode[i], markExplanation[i]);
        }

        courseRepository.finishCourse("Finished", Timestamp.valueOf(LocalDateTime.now()), courseId);

        return "redirect:journal";
    }

    @PostMapping("/show-graduates")
    public String showGraduates(@RequestParam Long courseId, Model model) {
        List<User> graduates = userRepository.findAllGraduates(courseId);

        model.addAttribute("graduates", graduates);
        model.addAttribute("courseId", courseId);

        return "graduates";
    }
}
