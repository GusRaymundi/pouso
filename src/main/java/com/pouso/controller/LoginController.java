package com.pouso.controller;

import com.pouso.model.Person;
import com.pouso.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginScreen() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @RequestParam String email,
        @RequestParam String password,
        HttpSession session,
        Model model
    ) {
        Person person = authService.login(email, password);

        if (person != null) {
            session.setAttribute("cpf", person.getCPF());
            return "redirect:/home";
        }

        model.addAttribute("error", "Email ou senha inválidos");
        return "login";
    }
}
