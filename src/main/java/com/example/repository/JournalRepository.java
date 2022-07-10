package com.example.repository;

import com.example.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    @Query(value = "SELECT * FROM journal WHERE journal.id_student_course = (SELECT course_student.id FROM course_student WHERE student_id=? AND course_id =?)",  nativeQuery = true)
    Journal findMarksByCourse(Long studentId, Long courseId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO journal(id_student_course, mark_points, mark_code, mark_explanation) VALUES (?, ?, ?, ?)",  nativeQuery = true)
    void endCourse(String studentId, int mark, String s, String s1);


}
