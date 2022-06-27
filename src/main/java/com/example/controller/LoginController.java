package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.textconstants.Pages.LOGIN_PAGE;
import static com.example.textconstants.Uri.LOGIN;

/**
 * controls login
 */
@Controller
@RequestMapping(LOGIN)
public class LoginController {
    /**
     * displays login page
     *
     * @param error  arises with wrong user data
     * @param logout arises when user logged out
     * @param model  model
     * @return page template
     */
    @GetMapping
    public String loginPage(@RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "logout", required = false) String logout,
                            Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return LOGIN_PAGE;
    }

}
