package com.example.repository;

import com.example.model.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CourseStudentRepository  extends JpaRepository<CourseStudent, Long>  {

    @Query(value = "SELECT id FROM course_student WHERE course_id=? AND student_id=?",  nativeQuery = true)
    String findIdStudentOnCourse(Long courseId, Long studentId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO course_student(course_id, student_id) VALUES (?, ?)", nativeQuery = true)
    void enrollStudentOnCourse(Long courseId, Long studentId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM course_student WHERE course_id=? AND student_id=?", nativeQuery = true)
    void leaveCourse(Long courseId, Long studentId);
}
