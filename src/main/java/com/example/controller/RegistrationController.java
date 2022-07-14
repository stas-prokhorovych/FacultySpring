package com.example.controller;

import com.example.dto.UserDTO;
import com.example.exception.UserServiceException;
import com.example.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static com.example.textconstants.Constants.ATTR_DATA_ERROR;
import static com.example.textconstants.Pages.SIGNUP_PAGE;
import static com.example.textconstants.Uri.*;

/**
 * controls registration of new user
 */
@Controller
@RequestMapping(SIGNUP)
@AllArgsConstructor
public class RegistrationController {
    UserService userService;

    /**
     * displays login page
     *
     * @param model  model
     * @return signup page
     */
    @GetMapping
    public String signupPage(final Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return SIGNUP_PAGE;
    }

    /**
     * @param passwordRepeat password repetition
     * @param userDTO userDTO that stores inputted data
     * @param result validation results
     * @param model model
     * @return signup page if error, redirect to login if success
     */
    @PostMapping
    public String addUser(@RequestParam String passwordRepeat,
                          @Valid UserDTO userDTO,
                          BindingResult result,
                          Model model) {

        if (result.hasErrors()) {
            return SIGNUP_PAGE;
        }

        try {
            userService.saveNewUser(userDTO, passwordRepeat);
        } catch (UserServiceException e) {
            model.addAttribute(ATTR_DATA_ERROR, e.getMessage());
            return SIGNUP_PAGE;
        }

        return REDIRECT+LOGIN;
    }
}
