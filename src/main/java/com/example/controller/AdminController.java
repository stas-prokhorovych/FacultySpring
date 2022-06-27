package com.example.controller;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private UserRepository userRepository;

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> students = userRepository.getAllUsersByRole("Student");
        List<User> teachers = userRepository.getAllUsersByRole("Teacher");

        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);

        return "userCatalogue";
    }
}
