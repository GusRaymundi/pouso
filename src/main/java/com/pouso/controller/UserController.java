package com.pouso.controller;

import com.pouso.model.Endereco;
import com.pouso.model.User;
import com.pouso.repository.PetRepository;
import com.pouso.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    public UserController(
        UserRepository userRepository,
        PetRepository petRepository
    ) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
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
        model.addAttribute("endereco", userRepository.buscarEnderecoPorCpf(cpf));

        return "user/edit";
    }

    @PostMapping("/user")
    public String updateUser(
        @ModelAttribute("usuario") User usuario,
        @ModelAttribute("endereco") Endereco endereco,
        @RequestParam(value = "fotoArquivo", required = false) MultipartFile fotoArquivo,
        @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");

        if (cpf == null) {
            return "redirect:/login";
        }

        User usuarioAtual = userRepository.buscarPorCpf(cpf);

        if (usuarioAtual == null) {
            return "redirect:/";
        }

        usuario.setCpf(cpf);
        endereco.setUsuarioCpf(cpf);

        try {
            validarEdicao(usuario, endereco);

            if (removerFoto) {
                usuario.setFotoPerfil(null);
            }

            if (fotoArquivo != null && !fotoArquivo.isEmpty()) {
                usuario.setFotoPerfil(salvarFotoPerfil(cpf, fotoArquivo));
            }

            if (userRepository.emailEmUsoPorOutroCpf(usuario.getEmail(), cpf)) {
                throw new IllegalArgumentException("Email ja cadastrado por outro usuario");
            }

            if (userRepository.usernameEmUsoPorOutroCpf(usuario.getUsername(), cpf)) {
                throw new IllegalArgumentException("Username ja cadastrado por outro usuario");
            }

            userRepository.atualizarPerfil(usuario, endereco);
            redirectAttributes.addFlashAttribute(
                "success",
                "Alteracoes salvas com sucesso."
            );

            return "redirect:/user";
        } catch (IOException e) {
            model.addAttribute("error", "Nao foi possivel salvar a foto.");
            model.addAttribute("usuario", usuario);
            model.addAttribute("endereco", endereco);
            return "user/edit";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("endereco", endereco);
            return "user/edit";
        }
    }

    private String salvarFotoPerfil(
        String cpf,
        MultipartFile fotoArquivo
    ) throws IOException {
        String contentType = fotoArquivo.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Escolha um arquivo de imagem valido");
        }

        if (fotoArquivo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("A foto deve ter no maximo 5MB");
        }

        Path uploadDir = Path.of("uploads", "profile")
            .toAbsolutePath()
            .normalize();

        Files.createDirectories(uploadDir);

        String extensao = extensaoPermitida(fotoArquivo.getOriginalFilename());
        String nomeArquivo = cpf + "-" + UUID.randomUUID() + extensao;
        Path destino = uploadDir.resolve(nomeArquivo).normalize();

        try (var inputStream = fotoArquivo.getInputStream()) {
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/profile/" + nomeArquivo;
    }

    private String extensaoPermitida(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) {
            return ".jpg";
        }

        String extensao = nomeOriginal
            .substring(nomeOriginal.lastIndexOf("."))
            .toLowerCase(Locale.ROOT);

        return switch (extensao) {
            case ".jpg", ".jpeg", ".png", ".gif", ".webp" -> extensao;
            default -> ".jpg";
        };
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

    private void validarEdicao(
        User usuario,
        Endereco endereco
    ) {
        if (isBlank(usuario.getNome())) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }

        if (isBlank(usuario.getEmail()) || !usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email invalido");
        }

        if (isBlank(usuario.getSenha())) {
            throw new IllegalArgumentException("Senha e obrigatoria");
        }

        if (isBlank(usuario.getUsername())) {
            throw new IllegalArgumentException("Username e obrigatorio");
        }

        if (isBlank(usuario.getTelefone()) || usuario.getTelefone().length() != 11) {
            throw new IllegalArgumentException("Telefone deve ter 11 digitos");
        }

        if (isBlank(usuario.getGenero()) ||
            (!usuario.getGenero().equals("M") &&
            !usuario.getGenero().equals("F") &&
            !usuario.getGenero().equals("O"))) {
            throw new IllegalArgumentException("Genero invalido");
        }

        if (isBlank(endereco.getCep()) || endereco.getCep().length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 digitos");
        }

        if (isBlank(endereco.getRua())) {
            throw new IllegalArgumentException("Rua e obrigatoria");
        }

        if (isBlank(endereco.getNumero())) {
            throw new IllegalArgumentException("Numero e obrigatorio");
        }

        if (isBlank(endereco.getBairro())) {
            throw new IllegalArgumentException("Bairro e obrigatorio");
        }

        if (isBlank(endereco.getCidade())) {
            throw new IllegalArgumentException("Cidade e obrigatoria");
        }

        if (isBlank(endereco.getUf()) || endereco.getUf().length() != 2) {
            throw new IllegalArgumentException("Estado deve ter 2 letras");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
