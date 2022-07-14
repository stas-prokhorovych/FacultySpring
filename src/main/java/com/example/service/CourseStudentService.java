package com.example.service;

import com.example.exception.UserServiceException;
import com.example.repository.CourseStudentRepository;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseStudentService {
    private static final Logger log = Logger.getLogger(CourseStudentService.class);

    private CourseStudentRepository courseStudentRepository;

    /**
     * @param courseId id of course to enroll
     * @param userId student to enroll on course
     * @throws UserServiceException if already enrolled
     */
    public void enrollStudentOnCourse(Long courseId, Long userId) throws UserServiceException {
        if(courseStudentRepository.findIdStudentOnCourse(courseId, userId) != null) {
            log.warn("You have already enrolled on this course");
            throw new UserServiceException("You have already enrolled on this course");
        }
        courseStudentRepository.enrollStudentOnCourse(courseId, userId);
    }

    /**
     * @param courseId id of course to enroll
     * @param userId student to leave on course
     * @throws UserServiceException if already left
     */
    public void leaveCourse(Long courseId, Long userId) throws UserServiceException {
        if(courseStudentRepository.findIdStudentOnCourse(courseId, userId) == null) {
            log.warn("You have already leave this course");
            throw new UserServiceException("You have already leave this course");
        }
        courseStudentRepository.leaveCourse(courseId, userId);
    }
}
