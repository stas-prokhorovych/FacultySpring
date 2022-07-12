package com.example.controller;

import com.example.exception.UserServiceException;
import com.example.model.User;
import com.example.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

import static com.example.textconstants.Pages.HOME_PAGE;
import static com.example.textconstants.Pages.PROFILE_PAGE;
import static com.example.textconstants.Uri.PROFILE;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * @param principal current login user info
     * @param model model
     * @return index if info can't be found, user info if success
     */
    @GetMapping(PROFILE)
    public String profile(Principal principal, Model model) {

        User user;

        try {
            user = userService.findUserByLogin(principal.getName());
        } catch (UserServiceException e) {
            model.addAttribute("dataError", e.getMessage());
            return HOME_PAGE;
        }

        model.addAttribute("user", user);
        return PROFILE_PAGE;
    }
}
