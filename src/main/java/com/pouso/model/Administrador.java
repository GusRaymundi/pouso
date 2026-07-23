package com.pouso.model;

public class Administrador {

    private String cpf;
    private String nome;
    private String nivel; // 'M' = Moderador, 'S' = Super administrador

    public Administrador(String cpf, String nome, String nivel) {
        this.cpf = cpf;
        this.nome = nome;
        this.nivel = nivel;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getNivel() { return nivel; }
}