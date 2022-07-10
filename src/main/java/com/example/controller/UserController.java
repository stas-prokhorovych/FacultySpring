package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        Optional<User> user = userRepository.findUserByLogin(principal.getName());
        if(user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            return "414";
        }
        return "profile";
    }
}
