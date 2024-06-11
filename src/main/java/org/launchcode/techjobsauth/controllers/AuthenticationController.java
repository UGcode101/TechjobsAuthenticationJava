package org.launchcode.techjobsauth.controllers;

import org.launchcode.techjobsauth.models.data.UserRepository;
import org.launchcode.techjobsauth.models.User;
import org.launchcode.techjobsauth.models.DTO.LoginFormDTO;
import org.launchcode.techjobsauth.models.DTO.RegisterFormDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    private static final String userSessionKey = "user";

    // Utility method to set user in session
    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey, user.getId());
    }

    // Utility method to get user from session
    private static User getUserFromSession(HttpSession session, UserRepository userRepository) {
        Integer userId = (Integer) session.getAttribute(userSessionKey);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    // GET handler to display the registration form
    @GetMapping("/register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute("registerFormDTO", new RegisterFormDTO());
        return "register";
    }

    // POST handler to process the registration form
    @PostMapping("/register")
    public String processRegistrationForm(@Valid RegisterFormDTO registerFormDTO, Errors errors, HttpSession session) {
        if (errors.hasErrors()) {
            return "register";
        }

        User existingUser = userRepository.findByUsername(registerFormDTO.getUsername());
        if (existingUser != null) {
            errors.rejectValue("username", "username.alreadyexists", "A user with that username already exists.");
            return "register";
        }

        if (!registerFormDTO.getPassword().equals(registerFormDTO.getVerifyPassword())) {
            errors.rejectValue("verifyPassword", "passwords.dontmatch", "Passwords do not match.");
            return "register";
        }

        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(session, newUser);

        return "redirect:/";
    }

    // GET handler to display the login form
    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute("loginFormDTO", new LoginFormDTO());
        return "login";
    }

    // POST handler to process the login form
    @PostMapping("/login")
    public String processLoginForm(@Valid LoginFormDTO loginFormDTO, Errors errors, HttpSession session) {
        if (errors.hasErrors()) {
            return "login";
        }

        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());

        if (theUser == null) {
            errors.rejectValue("username", "user.notfound", "Invalid username");
            return "login";
        }

        if (!theUser.checkPassword(loginFormDTO.getPassword())) {
            errors.rejectValue("password", "password.invalid", "Invalid password");
            return "login";
        }

        setUserInSession(session, theUser);

        return "redirect:/";
    }

    // GET handler to log out
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}