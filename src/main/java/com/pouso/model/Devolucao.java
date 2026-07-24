package com.pouso.model;
import java.time.LocalDate;

public class Devolucao {

    private LocalDate adocao_inicio;
    private String adocao_adotante;
    private String adocao_pet;
    private String adocao_dono;
    private String motivo;
    private LocalDate data_solicitacao;

    public Devolucao() {
    }

    public Devolucao(LocalDate adocao_inicio, String adocao_adotante,
                     String adocao_pet, String adocao_dono,
                     String motivo, LocalDate data_solicitacao) {
        this.adocao_inicio = adocao_inicio;
        this.adocao_adotante = adocao_adotante;
        this.adocao_pet = adocao_pet;
        this.adocao_dono = adocao_dono;
        this.motivo = motivo;
        this.data_solicitacao = data_solicitacao;
    }

    public LocalDate getAdocao_inicio() {
        return adocao_inicio;
    }

    public void setAdocao_inicio(LocalDate adocao_inicio) {
        this.adocao_inicio = adocao_inicio;
    }

    public String getAdocao_adotante() {
        return adocao_adotante;
    }

    public void setAdocao_adotante(String adocao_adotante) {
        this.adocao_adotante = adocao_adotante;
    }

    public String getAdocao_pet() {
        return adocao_pet;
    }

    public void setAdocao_pet(String adocao_pet) {
        this.adocao_pet = adocao_pet;
    }

    public String getAdocao_dono() {
        return adocao_dono;
    }

    public void setAdocao_dono(String adocao_dono) {
        this.adocao_dono = adocao_dono;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getData_solicitacao() {
        return data_solicitacao;
    }

    public void setData_solicitacao(LocalDate data_solicitacao) {
        this.data_solicitacao = data_solicitacao;
    }
}