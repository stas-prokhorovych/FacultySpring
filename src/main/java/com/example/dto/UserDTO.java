package com.example.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDTO {
    @NotEmpty(message = "login can't be empty")
    @Pattern(regexp = "^[a-z\\d_-]{4,16}$", message = "Login is not valid")
    private String login;

    @NotEmpty(message = "password can't be empty")
    @Size(min = 3, max=64, message = "Password must be longer 3 characters")
    private String password;

    @NotEmpty(message = "password repeat can't be empty")
    @Size(min = 3, max=64, message = "Password repeat must be longer 3 characters")
    private String passwordRepeat;

    @NotEmpty(message = "Mail can't be empty")
    @Pattern(regexp = "^[a-zA-Z\\d_.]+@[a-zA-Z\\d.-]+$", message = "Email is not valid")
    private String email;


    @Pattern(regexp = "[A-Z][a-z]{3,20}", message = "First name is not valid")
    @NotEmpty(message = "First name can't be empty")
    private String firstName;

    @Pattern(regexp = "[A-Z][a-z]{3,20}", message = "Last name is not valid")
    @NotEmpty(message = "Last name can't be empty")
    private String lastName;

    @Pattern(regexp = "|^\\+\\d{12}$", message = "Phone is not valid")
    private String phoneNumber;
}
