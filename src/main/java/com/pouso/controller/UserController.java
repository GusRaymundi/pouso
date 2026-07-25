package com.pouso.controller;

import com.pouso.model.User;
import com.pouso.service.UserService;
import com.pouso.repository.PetRepository;
import com.pouso.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserService userService;

    public UserController(
        UserRepository userRepository,
        PetRepository petRepository,
       UserService userService
    ) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.userService = userService;
    }

    @GetMapping("/user")
    public String editUser(
        HttpSession session,
        Model model
    ) {
        String cpf = (String) session.getAttribute("cpf");

        if (cpf == null) {
            return "redirect:/login";
        }

        User usuario = userRepository.buscarPorCpf(cpf);

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);

        return "user/edit";
    }

    @PostMapping("/conta/salvar")
    public String saveUser(
        @RequestParam String nome,
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam(required = false) String bio,
        @RequestParam(required = false) String genero,
        @RequestParam(required = false) String telefone,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");

        if (cpf == null) {
            return "redirect:/login";
        }

        User usuario = userRepository.buscarPorCpf(cpf);

        if (usuario == null) {
            return "redirect:/";
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setBio(bio);
        usuario.setGenero(genero);
        usuario.setTelefone(telefone);

        userRepository.atualizar(usuario);

        redirectAttributes.addFlashAttribute(
            "success",
            "Alterações salvas com sucesso."
        );

        return "redirect:/user";
    }

    @GetMapping("/perfil")
    public String profile(
        HttpSession session,
        Model model
    ) {
        String sessionCpf = (String) session.getAttribute("cpf");

        if (sessionCpf == null) {
            return "redirect:/login";
        }

        User profileUser = userRepository.buscarPorCpf(sessionCpf);

        if (profileUser == null) {
            return "redirect:/";
        }

        return renderProfile(model, profileUser, sessionCpf);
    }

    @GetMapping("/perfil/{username}")
    public String profileByUsername(
        @PathVariable String username,
        HttpSession session,
        Model model
    ) {
        String sessionCpf = (String) session.getAttribute("cpf");

        if (sessionCpf == null) {
            return "redirect:/login";
        }

        User profileUser = userRepository.buscarPorUsername(username);

        if (profileUser == null) {
            return "redirect:/";
        }

        return renderProfile(model, profileUser, sessionCpf);
    }

    private String renderProfile(
        Model model,
        User profileUser,
        String sessionCpf
    ) {
        boolean isSelf = sessionCpf.equals(profileUser.getCpf());

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("canEdit", isSelf);
        model.addAttribute("canDelete", false);
        model.addAttribute(
            "rating",
            userRepository.mediaAvaliacoesRecebidas(profileUser.getCpf())
        );
        model.addAttribute(
            "reviewCount",
            userRepository.contarAvaliacoesRecebidas(profileUser.getCpf())
        );
        model.addAttribute(
            "adoptionCount",
            userRepository.contarAdocoesDosPets(profileUser.getCpf())
        );
        model.addAttribute(
            "location",
            userRepository.buscarLocalizacao(profileUser.getCpf())
        );
        model.addAttribute(
            "pets",
            petRepository.listarAprovadosPorDono(profileUser.getCpf())
        );
        model.addAttribute(
            "reviews",
            userRepository.listarAvaliacoesRecebidas(profileUser.getCpf())
        );

        return "user/profile";
    }
    @GetMapping("/notifications")
public String notifications(
    HttpSession session,
    Model model
) {
    String cpf = (String) session.getAttribute("cpf");

    if (cpf == null) {
        return "redirect:/login";
    }
model.addAttribute(
    "notifications",
    userService.listarNotificacoes(cpf)
);

    return "user/notifications";
}
}
