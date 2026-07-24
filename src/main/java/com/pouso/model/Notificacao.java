package com.pouso.model;
import java.time.LocalDateTime;

public class Notificacao {

    private String pessoa_cpf;
    private LocalDateTime data;
    private String mensagem;
    private Boolean is_lido;

    public Notificacao() {
    }

    public Notificacao(String pessoa_cpf, LocalDateTime data,
                       String mensagem, Boolean is_lido) {
        this.pessoa_cpf = pessoa_cpf;
        this.data = data;
        this.mensagem = mensagem;
        this.is_lido = is_lido;
    }

    public String getPessoa_cpf() {
        return pessoa_cpf;
    }

    public void setPessoa_cpf(String pessoa_cpf) {
        this.pessoa_cpf = pessoa_cpf;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Boolean getIs_lido() {
        return is_lido;
    }

    public void setIs_lido(Boolean is_lido) {
        this.is_lido = is_lido;
    }
}