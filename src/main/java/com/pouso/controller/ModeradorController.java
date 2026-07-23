package com.pouso.controller;

import com.pouso.repository.AdministradorRepository;
import com.pouso.repository.PetRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ModeradorController {

    private final AdministradorRepository administradorRepository;
    private final PetRepository petRepository;

    public ModeradorController(AdministradorRepository administradorRepository, PetRepository petRepository) {
        this.administradorRepository = administradorRepository;
        this.petRepository = petRepository;
    }

    // TODO: extrair para um interceptor/filtro quando surgirem mais telas de moderador
    private boolean acessoNegado(HttpSession session) {
        String cpf = (String) session.getAttribute("cpf");
        return !administradorRepository.isAdministrador(cpf);
    }

    @GetMapping("/moderador/solicitacoes")
    public String solicitacoes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (acessoNegado(session)) {
            redirectAttributes.addFlashAttribute("error", "Acesso restrito a moderadores.");
            return "redirect:/login";
        }

        model.addAttribute("isModerador", true);
        model.addAttribute("solicitacoes", petRepository.listarPendentes());
        return "moderador-solicitacoes";
    }

    @PostMapping("/moderador/solicitacoes/aprovar")
    public String aprovar(@RequestParam String nome, @RequestParam String cpfDono,
                           HttpSession session, RedirectAttributes redirectAttributes) {
        if (acessoNegado(session)) {
            redirectAttributes.addFlashAttribute("error", "Acesso restrito a moderadores.");
            return "redirect:/login";
        }

        petRepository.aprovar(nome, cpfDono, (String) session.getAttribute("cpf"));
        return "redirect:/moderador/solicitacoes";
    }

    @PostMapping("/moderador/solicitacoes/rejeitar")
    public String rejeitar(@RequestParam String nome, @RequestParam String cpfDono,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        if (acessoNegado(session)) {
            redirectAttributes.addFlashAttribute("error", "Acesso restrito a moderadores.");
            return "redirect:/login";
        }

        petRepository.rejeitar(nome, cpfDono, (String) session.getAttribute("cpf"));
        return "redirect:/moderador/solicitacoes";
    }
    @GetMapping("/moderador/historico")
public String historico(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    if (acessoNegado(session)) {
        redirectAttributes.addFlashAttribute("error", "Acesso restrito a moderadores.");
        return "redirect:/login";
    }

    model.addAttribute("isModerador", true);
    model.addAttribute("historico", petRepository.listarHistorico());
    return "moderador-historico";
}
}