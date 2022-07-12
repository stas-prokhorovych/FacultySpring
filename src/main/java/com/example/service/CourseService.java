package com.example.service;

import com.example.exception.CourseServiceException;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.textconstants.Constants.STATUS_OPEN;

@Service
@AllArgsConstructor
public class CourseService {
    private CourseRepository courseRepository;
    private UserRepository userRepository;


    public List<Course> findStudentCourseByStatus(String status, Long id) {
        return courseRepository.findStudentCourseByStatus(status, id);
    }

    public void startCourse(LocalDateTime now, String status, Long courseId) {
        courseRepository.startCourse(now, status, courseId);
    }

    public List<Course> findCourseByStatusAndTeacherId(Long id, String status) {
        return courseRepository.findCourseByStatusAndTeacherId(id, status);
    }

    public List<Course> findByCourseStatus(String status) {
        return courseRepository.findByCourseStatus(status);
    }

    public void assignCourse(Long studentId, String status, Long courseId) {
        courseRepository.assignCourse(studentId, status, courseId);
    }

    public void addCourse(Course course, Long lecturerId) {
        Optional<Course> courseFromDb = courseRepository.findByName(course.getName());
        if (courseFromDb.isPresent()) {
            throw new CourseServiceException("Course with such name already exist");
        }

        if(lecturerId == null) {
            course.setIdLecturer(null);
            course.setCourseStatus("Closed, no teacher assigned yet");
        } else {
            User teacher = userRepository.findUserById(lecturerId);
            course.setIdLecturer(teacher);
            course.setCourseStatus(STATUS_OPEN);
        }

        courseRepository.addCourse(course.getName(), course.getTheme(), course.getStartDate(),
                course.getEndDate(), course.getIdLecturer(), course.getCourseStatus());

    }
}
