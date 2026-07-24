package com.pouso.model;
import java.time.LocalDate;

public class Avaliacao {

    private LocalDate adocao_inicio;
    private String adocao_adotante;
    private String adocao_pet;
    private String adocao_dono;
    private Short nota;
    private String comentario;
    private LocalDate data;

    public Avaliacao() {
    }

    public Avaliacao(LocalDate adocao_inicio, String adocao_adotante,
                     String adocao_pet, String adocao_dono,
                     Short nota, String comentario, LocalDate data) {
        this.adocao_inicio = adocao_inicio;
        this.adocao_adotante = adocao_adotante;
        this.adocao_pet = adocao_pet;
        this.adocao_dono = adocao_dono;
        this.nota = nota;
        this.comentario = comentario;
        this.data = data;
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

    public Short getNota() {
        return nota;
    }

    public void setNota(Short nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}