package com.example.service;

import com.example.dto.UserDTO;
import com.example.exception.UserServiceException;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.textconstants.Constants.ROLE_STUDENT;
import static com.example.textconstants.Constants.ROLE_TEACHER;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveNewUser(UserDTO userDTO, String passwordRepeat) throws UserServiceException {
        if(passwordRepeat==null || passwordRepeat.equals("")) {
            throw new UserServiceException("Repeat password can't be empty");
        }
        if(!userDTO.getPassword().equals(passwordRepeat)) {
            throw new UserServiceException("Repeat password doesn't match");
        }

        Optional<User> userFromDb = userRepository.findUserByLogin(userDTO.getLogin());
        if(userFromDb.isPresent()) {
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

    public User findUserByLogin(String name) throws UserServiceException {
        Optional<User> userDB = userRepository.findUserByLogin(name);

        if(userDB.isEmpty()) {
            throw new UserServiceException("Can't find your info");
        }

        return userDB.get();
    }

    public List<User> findAllGraduates(Long courseId) {
        return userRepository.findAllGraduates(courseId);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findNewUser(String role) {
        return userRepository.findNewUser(role);
    }

    public void createTeacher(String role, Long studentId) {
        userRepository.createTeacher(role, studentId);
    }
}
