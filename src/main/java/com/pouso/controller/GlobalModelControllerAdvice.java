package com.pouso.controller;

import com.pouso.model.User;
import com.pouso.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private final UserRepository userRepository;

    public GlobalModelControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) {
            return null;
        }

        return userRepository.buscarPorCpf(cpf);
    }
}
