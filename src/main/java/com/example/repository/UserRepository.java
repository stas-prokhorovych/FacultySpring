package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByLogin(String login);

    @Query(value = "SELECT * FROM user WHERE id IN (SELECT student_id FROM course_student WHERE course_id = ?)", nativeQuery = true)
    List<User> findAllGraduates(Long courseId);

    @Query(value = "SELECT * FROM user WHERE role = ?", nativeQuery = true)
    List<User> getAllUsersByRole(String role);

    @Query(value = "SELECT * FROM user WHERE role=? AND id NOT IN(SELECT DISTINCT student_id FROM course_student)", nativeQuery = true)
    List<User> findNewUser(String student);

    List<User> findByRole(String role);

//    void changeUserAccess(Long studentId);
}
