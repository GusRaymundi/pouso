package com.pouso.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pouso.dto.PetDetalheDTO;
import com.pouso.model.AdoptionSummary;
import com.pouso.repository.AdoptionRepository;

@Service
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final PetService petService;

    public AdoptionService(AdoptionRepository adoptionRepository, PetService petService) {
        this.adoptionRepository = adoptionRepository;
        this.petService = petService;
    }

    public void solicitarAdocao(
        String cpfDono,
        String petNome,
        boolean permanente,
        LocalDate dataFim,
        String sessionCpf
    ) {
        if (sessionCpf.equals(cpfDono)) {
            throw new PetValidationException(
                "Você não pode adotar seu próprio pet"
            );
        }

        PetDetalheDTO pet = petService.buscarDetalhe(cpfDono, petNome, sessionCpf);
        if (!pet.isDisponivel()) {
            throw new PetValidationException(
                "Este pet não está disponível para adoção"
            );
        }

        adoptionRepository.solicitar(sessionCpf, petNome, cpfDono, permanente, dataFim);
    }

    public void aceitarSolicitacao(
        LocalDate dataInicio,
        String cpfAdotante,
        String petNome,
        String petDono,
        String sessionCpf
    ) {
        if (!sessionCpf.equals(petDono)) {
            throw new PetValidationException("Sem permissão para essa ação");
        }
        adoptionRepository.aceitar(dataInicio, cpfAdotante, petNome, petDono);
    }

    public void recusarSolicitacao(
        LocalDate dataInicio,
        String cpfAdotante,
        String petNome,
        String petDono,
        String sessionCpf
    ) {
        if (!sessionCpf.equals(petDono)) {
            throw new PetValidationException("Sem permissão para essa ação");
        }
        adoptionRepository.recusar(dataInicio, cpfAdotante, petNome, petDono);
    }

    public List<AdoptionSummary> listarSolicitacoesPendentes(String cpf) {
        return adoptionRepository.listPendentesAsDonor(cpf);
    }
}
