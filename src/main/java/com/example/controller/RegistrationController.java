package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import java.util.Optional;

import static com.example.textconstants.Pages.SIGNUP_PAGE;
import static com.example.textconstants.Uri.SIGNUP;

/**
 * controls registration of new user
 */
@Controller
@RequestMapping(SIGNUP)
@AllArgsConstructor
public class RegistrationController {
    private UserRepository userRepository;

    @GetMapping
    public String signupPage(final Model model) {
        model.addAttribute("user", new User());
        return SIGNUP_PAGE;

    }

    @PostMapping
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return SIGNUP_PAGE;
        }

        Optional<User> userFromDb = userRepository.findUserByLogin(user.getLogin());

        if(userFromDb.isPresent()) {
            model.addAttribute("dataError", "User with such login already exists");
            return SIGNUP_PAGE;
        }

        userRepository.save(user);
        return SIGNUP_PAGE;
    }
}
