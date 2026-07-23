package com.pouso.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home") //remover o home
    public String home(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        model.addAttribute("cpf", cpf);
        return "home";
    }
}
