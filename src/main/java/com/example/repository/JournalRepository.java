package com.example.repository;

import com.example.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    @Query(value = "SELECT * FROM journal WHERE journal.id_student_course = (SELECT course_student.id FROM course_student WHERE student_id=? AND course_id =?)",  nativeQuery = true)
    Journal findMarksByCourse(Long studentId, Long courseId);
}
