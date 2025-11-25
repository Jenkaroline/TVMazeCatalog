package src.tvmaze.model;

import java.time.LocalDate;

public class Pessoa {
    private int id;
    private int idExterno;
    private String nome;
    private String pais;
    private LocalDate dataNascimento;

    public Pessoa() {}

    public Pessoa(int idExterno, String nome) {
        this.idExterno = idExterno;
        this.nome = nome;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdExterno() { return idExterno; }
    public void setIdExterno(int idExterno) { this.idExterno = idExterno; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    @Override
    public String toString() {
        return String.format("%s (%s)", nome, pais != null ? pais : "N/A");
    }
}
