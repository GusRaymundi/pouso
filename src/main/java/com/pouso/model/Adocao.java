package com.pouso.model;
import java.time.LocalDate;

public class Adocao {

    private LocalDate data_inicio;
    private String cpf_adotante;
    private String pet_nome;
    private String pet_dono;
    private LocalDate data_fim;
    private LocalDate data_solicitacao;
    private Status status_enum;
    private Boolean is_permanente;

    // Construtor vazio
    public Adocao() {
    }

    // Construtor completo
    public Adocao(LocalDate data_inicio, String cpf_adotante, String pet_nome,
                  String pet_dono, LocalDate data_fim,
                  LocalDate data_solicitacao, Status status_enum,
                  Boolean is_permanente) {

        this.data_inicio = data_inicio;
        this.cpf_adotante = cpf_adotante;
        this.pet_nome = pet_nome;
        this.pet_dono = pet_dono;
        this.data_fim = data_fim;
        this.data_solicitacao = data_solicitacao;
        this.status_enum = status_enum;
        this.is_permanente = is_permanente;
    }

    public LocalDate getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(LocalDate data_inicio) {
        this.data_inicio = data_inicio;
    }

    public String getCpf_adotante() {
        return cpf_adotante;
    }

    public void setCpf_adotante(String cpf_adotante) {
        this.cpf_adotante = cpf_adotante;
    }

    public String getPet_nome() {
        return pet_nome;
    }

    public void setPet_nome(String pet_nome) {
        this.pet_nome = pet_nome;
    }

    public String getPet_dono() {
        return pet_dono;
    }

    public void setPet_dono(String pet_dono) {
        this.pet_dono = pet_dono;
    }

    public LocalDate getData_fim() {
        return data_fim;
    }

    public void setData_fim(LocalDate data_fim) {
        this.data_fim = data_fim;
    }

    public LocalDate getData_solicitacao() {
        return data_solicitacao;
    }

    public void setData_solicitacao(LocalDate data_solicitacao) {
        this.data_solicitacao = data_solicitacao;
    }

    public Status getStatus_enum() {
        return status_enum;
    }

    public void setStatus_enum(Status status_enum) {
        this.status_enum = status_enum;
    }

    public Boolean getIs_permanente() {
        return is_permanente;
    }

    public void setIs_permanente(Boolean is_permanente) {
        this.is_permanente = is_permanente;
    }
}