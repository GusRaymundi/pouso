package com.pouso.controller;

import com.pouso.model.User;
import com.pouso.repository.PetRepository;
import com.pouso.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    public UserController(UserRepository userRepository, PetRepository petRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
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
        model.addAttribute("rating", userRepository.mediaAvaliacoesRecebidas(profileUser.getCpf()));
        model.addAttribute("reviewCount", userRepository.contarAvaliacoesRecebidas(profileUser.getCpf()));
        model.addAttribute("adoptionCount", userRepository.contarAdocoesDosPets(profileUser.getCpf()));
        model.addAttribute("location", userRepository.buscarLocalizacao(profileUser.getCpf()));
        model.addAttribute("pets", petRepository.listarAprovadosPorDono(profileUser.getCpf()));
        model.addAttribute("reviews", userRepository.listarAvaliacoesRecebidas(profileUser.getCpf()));

        return "profile";
    }

}
