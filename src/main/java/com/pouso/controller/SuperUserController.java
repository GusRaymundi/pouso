package com.pouso.controller;

import com.pouso.dto.PetOwnerListDTO;
import com.pouso.dto.UserListDTO;
import com.pouso.model.PetSolicitacao;
import com.pouso.model.User;
import com.pouso.repository.PetRepository;
import com.pouso.repository.UserRepository;
import com.pouso.repository.UsuarioRepository;
import com.pouso.service.PetService;
import com.pouso.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SuperUserController {

    private final UsuarioRepository usuarioRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserService userService;
    private final PetService petService;

    public SuperUserController(
            UsuarioRepository usuarioRepository,
            UserRepository userRepository,
            PetRepository petRepository,
            UserService userService,
            PetService petService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.userService = userService;
        this.petService = petService;
    }

    @GetMapping("/sudo/users")
    public String sudo(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "false") boolean banidos
    ) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        if (size != 10 && size != 20 && size != 50) {
            size = 10;
        }
        if (!sortBy.equals("nome") && !sortBy.equals("data_registro")) {
            sortBy = "nome";
        }
        if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
            sortDir = "asc";
        }

        UserListDTO result = userService.listPaged(page, size, sortBy, sortDir, q, banidos);

        model.addAttribute("usuarios", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("currentSize", size);
        model.addAttribute("showBannedOnly", banidos);

        return "admin/users";
    }

    @GetMapping("/sudo/pets")
    public String pets(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        if (size != 10 && size != 20 && size != 50) {
            size = 10;
        }
        if (!sortBy.equals("nome") && !sortBy.equals("data_registro") && !sortBy.equals("pet_count")) {
            sortBy = "nome";
        }
        if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
            sortDir = "asc";
        }

        PetOwnerListDTO result = petService.listPaged(page, size, sortBy, sortDir, q);

        model.addAttribute("proprietarios", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("currentSize", size);

        return "admin/pets";
    }

    @GetMapping("/sudo/banidos")
    public String banned(HttpSession session, Model model) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        UserListDTO users = userService.listPaged(0, 50, "nome", "asc", "", true);
        var ownersWithBannedPets = petRepository.listarBanidosPorDono();
        model.addAttribute("usuariosBanidos", users.getContent());
        model.addAttribute("proprietariosComPetsBanidos", ownersWithBannedPets);
        model.addAttribute("totalPetsBanidos", ownersWithBannedPets.stream().mapToLong(PetOwnerListDTO.OwnerItem::getPetCount).sum());

        return "admin/banned";
    }

    @GetMapping("/sudo/users/{username}")
    public String userProfile(@PathVariable String username, HttpSession session, Model model) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        User profileUser = userRepository.buscarPorUsername(username);
        if (profileUser == null) {
            return "redirect:/sudo/users";
        }

        String cpf = profileUser.getCpf();

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isSelf", cpf.equals(session.getAttribute("cpf")));
        model.addAttribute("canEdit", false);
        model.addAttribute("canDelete", false);
        model.addAttribute("isSudoView", true);
        model.addAttribute("isBanned", usuarioRepository.isBanido(cpf));
        model.addAttribute("rating", userRepository.mediaAvaliacoesRecebidas(cpf));
        model.addAttribute("reviewCount", userRepository.contarAvaliacoesRecebidas(cpf));
        model.addAttribute("adoptionCount", userRepository.contarAdocoesDosPets(cpf));
        model.addAttribute("location", userRepository.buscarLocalizacao(cpf));
        model.addAttribute("pets", petRepository.listByOwner(cpf));
        model.addAttribute("reviews", userRepository.listarAvaliacoesRecebidas(cpf));

        return "admin/user-profile";
    }

    @PostMapping("/sudo/users/{username}/ban")
    public String banUser(@PathVariable String username, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        User profileUser = userRepository.buscarPorUsername(username);
        if (profileUser == null) {
            return "redirect:/sudo/users";
        }

        String cpf = profileUser.getCpf();
        if (cpf.equals(session.getAttribute("cpf"))) {
            redirectAttributes.addFlashAttribute("error", "Você não pode banir a própria conta.");
            return "redirect:/sudo/users/" + username;
        }

        usuarioRepository.setBanido(cpf, true);
        redirectAttributes.addFlashAttribute("success", "Usuário banido.");
        return "redirect:/sudo/users/" + username;
    }

    @PostMapping("/sudo/users/{username}/unban")
    public String unbanUser(@PathVariable String username, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        User profileUser = userRepository.buscarPorUsername(username);
        if (profileUser == null) {
            return "redirect:/sudo/users";
        }

        String cpf = profileUser.getCpf();
        usuarioRepository.setBanido(cpf, false);
        redirectAttributes.addFlashAttribute("success", "Usuário desbanido.");
        return "redirect:/sudo/users/" + username;
    }

    @GetMapping("/sudo/pets/{username}/{nome}")
    public String petDetails(@PathVariable String username, @PathVariable String nome, HttpSession session, Model model) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        PetSolicitacao pet = petRepository.findByOwnerUsernameAndName(username, nome);
        if (pet == null) {
            return "redirect:/sudo/pets";
        }

        model.addAttribute("pet", pet);
        model.addAttribute("ownerUsername", username);
        return "admin/pet-detail";
    }

    @PostMapping("/sudo/pets/{username}/{nome}/ban")
    public String banPet(@PathVariable String username, @PathVariable String nome, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        petRepository.setBanidoByOwnerUsername(nome, username, true);
        redirectAttributes.addFlashAttribute("success", "Pet banido.");
        redirectAttributes.addAttribute("username", username);
        redirectAttributes.addAttribute("nome", nome);
        return "redirect:/sudo/pets/{username}/{nome}";
    }

    @PostMapping("/sudo/pets/{username}/{nome}/unban")
    public String unbanPet(@PathVariable String username, @PathVariable String nome, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = requireSudo(session);
        if (redirect != null) {
            return redirect;
        }

        petRepository.setBanidoByOwnerUsername(nome, username, false);
        redirectAttributes.addFlashAttribute("success", "Pet desbanido.");
        redirectAttributes.addAttribute("username", username);
        redirectAttributes.addAttribute("nome", nome);
        return "redirect:/sudo/pets/{username}/{nome}";
    }

    private String requireSudo(HttpSession session) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) {
            return "redirect:/login";
        }

        String nivel = usuarioRepository.buscarNivelAdmin(cpf);
        if (!"S".equals(nivel)) {
            return "redirect:/";
        }

        return null;
    }
}
