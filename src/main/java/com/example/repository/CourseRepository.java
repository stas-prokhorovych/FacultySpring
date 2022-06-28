package com.example.repository;

import com.example.model.Course;
import com.example.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {





    @Query(value = "SELECT * FROM course WHERE course_status=? AND id IN ( SELECT course_id FROM course_student WHERE student_id=?)",  nativeQuery = true)
    List<Course> findStudentCourseByStatus(String status, Long id);

    @Query(value = "SELECT * FROM course WHERE id_lecturer=? AND course_status = ?",  nativeQuery = true)
    List<Course> findCourseByStatusAndTeacherId(Long id, String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE course SET start_date=?, course_status=? WHERE course.id = ?",  nativeQuery = true)
    void startCourse(LocalDateTime now, String status, Long courseId);

    Page<Course> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM course WHERE name=?",  nativeQuery = true)
    Optional<Course> findByName(String name);

    @Query(value = "SELECT DISTINCT theme FROM course",  nativeQuery = true)
    List<String> findAllThemes();

    List<Course> findByCourseStatus(String status);
}
