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

    Page<Course> findAll(Pageable pageable);

    Page<Course> findByCourseStatus(String status, Pageable pageable);

    List<Course> findByCourseStatus(String s);

    @Query(value = "SELECT * FROM course WHERE course_status=? AND id IN ( SELECT course_id FROM course_student WHERE student_id=?)",  nativeQuery = true)
    List<Course> findStudentCourseByStatus(String status, Long id);

    @Query(value = "SELECT * FROM course WHERE id_lecturer=? AND course_status = ?",  nativeQuery = true)
    List<Course> findCourseByStatusAndTeacherId(Long id, String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE course SET start_date=?, course_status=? WHERE course.id = ?",  nativeQuery = true)
    void startCourse(LocalDateTime now, String status, Long courseId);

    @Query(value = "SELECT * FROM course WHERE name=?",  nativeQuery = true)
    Optional<Course> findByName(String name);

    @Query(value = "SELECT DISTINCT theme FROM course",  nativeQuery = true)
    List<String> findAllThemes();

    @Query(value = "SELECT * FROM course WHERE course_status <> 'Closed no teacher assigned yet'",  nativeQuery = true)
    Page<Course> findActiveCourses(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE course SET id_lecturer=?, course_status=? WHERE id=?",  nativeQuery = true)
    void assignCourse(Long teacherId, String status, Long courseId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO course(name, theme, start_date, end_date, id_lecturer,course_status) VALUES(?,?,?,?,?,?)",  nativeQuery = true)
    void addCourse(Course course);

    @Query(value ="SELECT COUNT(id) FROM course_student WHERE course_id=?", nativeQuery = true)
    Integer findStudentsEnrolled(Long courseId);

    Page<Course> findByCourseStatusNot(String status, Pageable pageable);

    @Query(value ="SELECT id FROM course_student WHERE student_id = ? AND course_id = ?", nativeQuery = true)
    Integer findSelectedCourse(Long studentId, Long id);
}
