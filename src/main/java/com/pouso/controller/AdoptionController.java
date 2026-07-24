package com.pouso.controller;

import com.pouso.repository.AdoptionRepository;
import com.pouso.repository.PetRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdoptionController {

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;

    public AdoptionController(AdoptionRepository adoptionRepository, PetRepository petRepository) {
        this.adoptionRepository = adoptionRepository;
        this.petRepository = petRepository;
    }

    @GetMapping("/adocoes")
    public String myAdoptions(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        model.addAttribute("asDonor", adoptionRepository.listActiveAsDonor(cpf));
        model.addAttribute("history", adoptionRepository.listHistory(cpf));
        model.addAttribute("rejected", petRepository.listRejectedByOwner(cpf));
        return "minhas-adocoes";
    }
}
