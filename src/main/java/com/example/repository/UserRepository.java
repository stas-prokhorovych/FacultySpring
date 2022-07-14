package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByLogin(String login);

    User findUserById(Long lecturerId);

    List<User> findByRole(String role);

    @Query(value = "SELECT * FROM user WHERE id IN (SELECT student_id FROM course_student WHERE course_id = ?)", nativeQuery = true)
    List<User> findAllGraduates(Long courseId);

    @Query(value = "SELECT * FROM user WHERE role=? AND id NOT IN(SELECT DISTINCT student_id FROM course_student)", nativeQuery = true)
    List<User> findNewUser(String student);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET user_access = ? WHERE id=?", nativeQuery = true)
    void changeUserAccess(Integer userAccess, Long studentId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET user.role=? WHERE id=?", nativeQuery = true)
    void createTeacher(String role, Long studentId);
}
