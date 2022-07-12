package com.example.service;

import com.example.exception.UserServiceException;
import com.example.repository.CourseStudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseStudentService {
    private CourseStudentRepository courseStudentRepository;

    public void enrollStudentOnCourse(Long courseId, Long userId) {
        if(courseStudentRepository.findIdStudentOnCourse(courseId, userId) != null) {
            throw new UserServiceException("You have already enrolled on this course");
        }

        courseStudentRepository.enrollStudentOnCourse(courseId, userId);
    }

    public void leaveCourse(Long courseId, Long userId) {
        if(courseStudentRepository.findIdStudentOnCourse(courseId, userId) == null) {
            throw new UserServiceException("You have already leave this course");
        }

        courseStudentRepository.leaveCourse(courseId, userId);
    }
}
