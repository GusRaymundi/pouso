package com.pouso.controller;

import com.pouso.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("") //remover o home
    public String home(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if ("S".equals(usuarioRepository.buscarNivelAdmin(cpf))) {
            return "redirect:/sudo/users";
        }

        model.addAttribute("cpf", cpf);
        return "home";
    }
}
