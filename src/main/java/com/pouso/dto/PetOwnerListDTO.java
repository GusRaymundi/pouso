package com.pouso.dto;

import java.util.List;

public class PetOwnerListDTO {

    private final List<OwnerItem> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PetOwnerListDTO(List<OwnerItem> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<OwnerItem> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isFirst() { return page <= 0; }
    public boolean isLast() { return page >= totalPages - 1; }

    public static class OwnerItem {
        private final String cpf;
        private final String nome;
        private final String username;
        private final long petCount;
        private List<PetItem> pets;

        public OwnerItem(String cpf, String nome, String username, long petCount) {
            this.cpf = cpf;
            this.nome = nome;
            this.username = username;
            this.petCount = petCount;
        }

        public String getCpf() { return cpf; }
        public String getNome() { return nome; }
        public String getUsername() { return username; }
        public long getPetCount() { return petCount; }
        public List<PetItem> getPets() { return pets; }
        public void setPets(List<PetItem> pets) { this.pets = pets; }
    }

    public static class PetItem {
        private final String nome;
        private final String tipoPetNome;
        private final String cpfDono;
        private final String statusAprovacao;
        private final boolean banned;

        public PetItem(String nome, String tipoPetNome, String cpfDono, String statusAprovacao, boolean banned) {
            this.nome = nome;
            this.tipoPetNome = tipoPetNome;
            this.cpfDono = cpfDono;
            this.statusAprovacao = statusAprovacao;
            this.banned = banned;
        }

        public String getNome() { return nome; }
        public String getTipoPetNome() { return tipoPetNome; }
        public String getCpfDono() { return cpfDono; }
        public String getStatusAprovacao() { return statusAprovacao; }
        public boolean isBanned() { return banned; }
    }
}
