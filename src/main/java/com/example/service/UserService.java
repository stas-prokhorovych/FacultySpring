package com.example.service;

import com.example.dto.UserDTO;
import com.example.exception.UserServiceException;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.example.textconstants.Constants.ROLE_STUDENT;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger log = Logger.getLogger(UserService.class);

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param userDTO info entered in form
     * @param passwordRepeat password repeat
     * @throws UserServiceException if validation errors
     */
    public void saveNewUser(UserDTO userDTO, String passwordRepeat) throws UserServiceException {
        if(passwordRepeat==null || passwordRepeat.equals("")) {
            log.info("Repeat password can't be empty");
            throw new UserServiceException("Repeat password can't be empty");
        }
        if(!userDTO.getPassword().equals(passwordRepeat)) {
            log.info("Repeat password doesn't match");
            throw new UserServiceException("Repeat password doesn't match");
        }

        Optional<User> userFromDb = userRepository.findUserByLogin(userDTO.getLogin());
        if(userFromDb.isPresent()) {
            log.info("User with such login already exists");
            throw new UserServiceException("User with such login already exists");
        }

        User user = User.builder()
                .login(userDTO.getLogin())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .role(ROLE_STUDENT)
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phoneNumber(userDTO.getPhoneNumber())
                .userAccess(true)
                .build();

        userRepository.save(user);
    }

    /**
     * @param login login of user to find
     * @return user with such login
     * @throws UserServiceException if user with such login doesn't exist
     */
    public User findUserByLogin(String login) throws UserServiceException {
        Optional<User> userDB = userRepository.findUserByLogin(login);
        if(userDB.isEmpty()) {
            log.error("Can't find info");
            throw new UserServiceException("Can't find your info");
        }
        return userDB.get();
    }

    /**
     * @param courseId id of course to find graduates
     * @return students of this course
     */
    public List<User> findAllGraduates(Long courseId) {
        return userRepository.findAllGraduates(courseId);
    }

    /**
     * @param role role of user
     * @return users with such role
     */
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * @param role role of user
     * @return newly created user
     */
    public List<User> findNewUser(String role) {
        return userRepository.findNewUser(role);
    }

    /**
     * @param role new role of student
     * @param studentId if of student to become teacher
     */
    public void createTeacher(String role, Long studentId) {
        userRepository.createTeacher(role, studentId);
    }

    /**
     * @param access new access value for user
     * @param studentId id of student to change access
     * @throws UserServiceException if access was changed already
     */
    public void changeUserAccess(Integer access, Long studentId) throws UserServiceException {
        User user = userRepository.findUserById(studentId);
        boolean changeAccess = (access == 1);
        if(user.isUserAccess() == changeAccess) {
            log.warn("You already change access");
            throw new UserServiceException("You already change access");
        }
        userRepository.changeUserAccess(access, studentId);
    }

    /**
     * @param courses list of courses
     * @return teachers that belong to this courses
     */
    public List<User> findCoursesTeachers(List<Course> courses) {
        List<User> teachers = new LinkedList<>();
        for (Course course : courses) {
            teachers.add(userRepository.findUserById(course.getIdLecturer().getId()));
        }
        return teachers;
    }
}
