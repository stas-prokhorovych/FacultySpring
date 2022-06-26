package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/signup")
@AllArgsConstructor
public class RegistrationController {
    private UserRepository userRepository;

    @GetMapping
    public String signup() {
        return "signup";
    }

    @PostMapping
    public String addUser(User user, Model model) {
        Optional<User> userFromDb = userRepository.findByLogin(user.getLogin());

        if(userFromDb.isPresent()) {
            System.out.println("here");
            model.addAttribute("message", "User exists");
            return "signup";
        }
        System.out.println("here");
        userRepository.save(user);

        return "redirect:/profile";
    }
}
