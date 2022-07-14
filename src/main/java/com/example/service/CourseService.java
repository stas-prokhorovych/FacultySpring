package com.example.service;

import com.example.dto.CourseDTO;
import com.example.exception.CourseServiceException;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.example.textconstants.Constants.*;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.next;

@Service
@AllArgsConstructor
public class CourseService {
    private static final Logger log = Logger.getLogger(CourseService.class);

    private CourseRepository courseRepository;
    private UserRepository userRepository;

    /**
     * @param status status of course to find
     * @param id     if od student
     * @return courses of given student by status
     */
    public List<Course> findStudentCourseByStatus(String status, Long id) {
        return courseRepository.findStudentCourseByStatus(status, id);
    }

    /**
     * @param now      current time
     * @param status   status of course to be changed
     * @param courseId if of course to start
     * @throws CourseServiceException if course already started or can't be found
     */
    public void startCourse(LocalDateTime now, String status, Long courseId) throws CourseServiceException {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            log.info("Can't find course");
            throw new CourseServiceException("Can't find course");
        }

        Course course = courseOptional.get();
        if (course.getCourseStatus().equals(STATUS_IN_PROGRESS)) {
            log.info("Course already started");
            throw new CourseServiceException("Course already started");
        }

        courseRepository.startCourse(now, status, courseId);
    }

    /**
     * @param id     teacher id
     * @param status status of course
     * @return courses
     */
    public List<Course> findCourseByStatusAndTeacherId(Long id, String status) {
        return courseRepository.findCourseByStatusAndTeacherId(id, status);
    }

    /**
     * @param status status of course
     * @return courses with give status
     */
    public List<Course> findByCourseStatus(String status) {
        return courseRepository.findByCourseStatus(status);
    }

    /**
     * @param studentId student id to assign on course and become teacher
     * @param status    status to be changed of this course
     * @param courseId  id of this course
     */
    public void assignCourse(Long studentId, String status, Long courseId) {
        courseRepository.assignCourse(studentId, status, courseId);
    }

    /**
     * @param courseDTO  course info
     * @param lecturerId if of lecturer that will teach this course
     * @throws CourseServiceException validation errors
     */
    public void addCourse(CourseDTO courseDTO, Long lecturerId) throws CourseServiceException {
        Optional<Course> courseFromDb = courseRepository.findByName(courseDTO.getName());
        if (courseFromDb.isPresent()) {
            log.info("Course with such name already exist");
            throw new CourseServiceException("Course with such name already exist");
        }
        if (courseDTO.getStartDate().isAfter(courseDTO.getEndDate())) {
            log.info("start date too soon");
            throw new CourseServiceException("start date must be before end date");
        }
        if (courseDTO.getStartDate().isBefore(LocalDateTime.now().with(next(MONDAY)))) {
            log.info("start date too soon");
            throw new CourseServiceException("start date at least:\n" + LocalDateTime.now().with(next(MONDAY)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        User teacher;
        String status;
        if (lecturerId == null) {
            teacher = null;
            status = STATUS_CLOSED;
        } else {
            teacher = userRepository.findUserById(lecturerId);
            status = STATUS_OPEN;
        }

        Course course = Course.builder()
                .name(courseDTO.getName())
                .theme(courseDTO.getTheme())
                .startDate(courseDTO.getStartDate())
                .endDate(courseDTO.getEndDate())
                .courseStatus(status)
                .idLecturer(teacher)
                .build();

        courseRepository.addCourse(course.getName(), course.getTheme(), course.getStartDate(),
                course.getEndDate(), course.getIdLecturer(), course.getCourseStatus());
    }

    /**
     * @param courseId if of course to delete
     */
    public void deleteById(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            log.info("course already deleted");
            throw new CourseServiceException("course already deleted");
        }

        courseRepository.deleteById(courseId);
    }

    /**
     * @param courseId if by which to find course
     * @return course with specified id
     * @throws CourseServiceException if no course to be found
     */
    public Course findById(Long courseId) throws CourseServiceException {
        Optional<Course> course = courseRepository.findById(courseId);

        if (course.isEmpty()) {
            log.error("can't find course to find by id");
            throw new CourseServiceException("can't find course");
        }

        return course.get();
    }

    /**
     * @param courseDTO      course info
     * @param updateCourseId id of course that should be updated
     * @param lecturerId     id of lecturer if present
     * @throws CourseServiceException occurs if validation errors
     */
    public void updateCourse(CourseDTO courseDTO, Long updateCourseId, Long lecturerId) throws CourseServiceException {
        Optional<Course> updatedCourseOptional = courseRepository.findById(updateCourseId);
        if (updatedCourseOptional.isEmpty()) {
            log.error("can't find course that will be updated");
            throw new CourseServiceException("can't find course that will be updated");
        }
        Course updatedCourse = updatedCourseOptional.get();

        Optional<Course> courseDB = courseRepository.findByName(courseDTO.getName());
        if (courseDB.isPresent() && !courseDTO.getName().equals(updatedCourse.getName())) {
            log.info("such name already exists");
            throw new CourseServiceException("Such name already exists");
        }
        if (courseDTO.getStartDate().isAfter(courseDTO.getEndDate())) {
            log.info("dates not correct");
            throw new CourseServiceException("start date must be before end date");
        }
        if (courseDTO.getStartDate().isBefore(LocalDateTime.now().with(next(MONDAY)))) {
            log.info("start date is incorrect");
            throw new CourseServiceException("start date at least:\n" + LocalDateTime.now().with(next(MONDAY)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        if (lecturerId == null) {
            lecturerId = updatedCourse.getIdLecturer().getId();
        }

        courseRepository.updateCourse(courseDTO.getName(), courseDTO.getTheme(),
                courseDTO.getStartDate(), courseDTO.getEndDate(), lecturerId, updatedCourse.getId());
    }

    /**
     * @return all theme that present
     */
    public List<String> findAllThemes() {
        return courseRepository.findAllThemes();
    }

    /**
     * @param courses courses by which found how many students enrolled
     * @return numbers of student enrolled that correspond to each course
     */
    public List<Integer> findStudentsEnrolled(List<Course> courses) {
        List<Integer> studentEnrolled = new LinkedList<>();
        for (Course course : courses) {
            studentEnrolled.add(courseRepository.findStudentsEnrolled(course.getId()));
        }
        return studentEnrolled;
    }

    /**
     * @param courses   courses to check if student select them
     * @param studentId student id by which found selected courses
     * @return boolean list yes/no course select correspondent course
     */
    public List<Boolean> findSelectedCourse(List<Course> courses, Long studentId) {
        List<Boolean> selectedCourses = new LinkedList<>();
        for (Course course : courses) {
            Integer value = courseRepository.findSelectedCourse(studentId, course.getId());
            if (value == null) {
                selectedCourses.add(false);
                continue;
            }
            selectedCourses.add(true);
        }
        return selectedCourses;
    }
}
