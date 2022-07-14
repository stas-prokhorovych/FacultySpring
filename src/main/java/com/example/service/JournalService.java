package com.example.service;

import com.example.exception.JournalServiceException;
import com.example.model.Course;
import com.example.model.Journal;
import com.example.repository.CourseRepository;
import com.example.repository.CourseStudentRepository;
import com.example.repository.JournalRepository;
import com.example.textconstants.Mark;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.textconstants.Constants.STATUS_FINISHED;

@Service
@AllArgsConstructor
public class JournalService {
    private static final Logger log = Logger.getLogger(JournalService.class);

    private JournalRepository journalRepository;
    private CourseRepository courseRepository;
    private CourseStudentRepository courseStudentRepository;

    /**
     * @param studentId student id to find mark
     * @param courseId course with given student's mark
     * @return Journal info about student study
     */
    public Journal findMarksByCourse(Long studentId, Long courseId) {
        return journalRepository.findMarksByCourse(studentId, courseId);
    }

    /**
     * @param courseId if of course that will finish
     * @param studentIds ids of graduates
     * @param studentMarks marks of graduates
     * @throws JournalServiceException occurs when can't find course or course already finished
     */
    public void endCourse(Long courseId, String[] studentIds, String[] studentMarks) throws JournalServiceException {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if(courseOptional.isEmpty()) {
            log.error("Can't find course");
            throw new JournalServiceException("Can't find course");
        }
        Course course = courseOptional.get();
        if(course.getCourseStatus().equals(STATUS_FINISHED)) {
            log.info("Course already finished");
            throw new JournalServiceException("Course already finished");
        }
        if(studentIds == null) {
            courseRepository.finishCourse("Finished", Timestamp.valueOf(LocalDateTime.now()), courseId);
            return;
        }

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
            String idStudentOnCourse = courseStudentRepository.findIdStudentOnCourse(courseId, Long.valueOf(studentIds[i]));
            journalRepository.endCourse(idStudentOnCourse, marks[i], markCode[i], markExplanation[i]);
        }

        courseRepository.finishCourse("Finished", Timestamp.valueOf(LocalDateTime.now()), courseId);
    }
}
