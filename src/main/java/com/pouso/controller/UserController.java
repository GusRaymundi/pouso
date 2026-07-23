package com.pouso.controller;

import com.pouso.model.User;
import com.pouso.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
     
    @GetMapping("/user") //service user controller edit
     public String editUser(HttpSession session, Model model) {
        return "edit-user";
    }

    @GetMapping("/perfil")
    public String profile(HttpSession session, Model model) {
        String sessionCpf = (String) session.getAttribute("cpf");
        if (sessionCpf == null) {
            return "redirect:/login";
        }

        User profileUser = userRepository.buscarPorCpf(sessionCpf);
        if (profileUser == null) {
            return "redirect:/home";
        }

        return renderProfile(model, profileUser, sessionCpf);
    }

    @GetMapping("/perfil/{username}")
    public String profileByUsername(@PathVariable String username, HttpSession session, Model model) {
        String sessionCpf = (String) session.getAttribute("cpf");
        if (sessionCpf == null) {
            return "redirect:/login";
        }

        User profileUser = userRepository.buscarPorUsername(username);
        if (profileUser == null) {
            return "redirect:/home";
        }

        return renderProfile(model, profileUser, sessionCpf);
    }

    private String renderProfile(Model model, User profileUser, String sessionCpf) {
        boolean isSelf = sessionCpf.equals(profileUser.getCpf());

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("canEdit", isSelf);
        model.addAttribute("canDelete", false);
        model.addAttribute("rating", "--");
        model.addAttribute("reviewCount", "--");
        model.addAttribute("adoptionCount", "--");
        model.addAttribute("location", "Localizacao nao informada");
        model.addAttribute("petsPlaceholder", List.of("Nenhum pet cadastrado por enquanto."));
        model.addAttribute("reviewsPlaceholder", List.of("Nenhuma avaliacao ainda."));

        return "profile";
    }

}
