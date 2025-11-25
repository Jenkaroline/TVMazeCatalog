package src.tvmaze.model;

import java.time.LocalDate;
import java.util.*;

public class Serie {
    private int id;
    private int idExterno;
    private String nome;
    private String linguagem;
    private String status;
    private String sinopse;
    private Double notaMedia;
    private LocalDate dataEstreia;
    private LocalDate dataTermino;
    private String observacao;
    private String statusLocal;
    private Set<Genero> generos;

    public Serie() {
        this.generos = new HashSet<>();
    }

    public Serie(int idExterno, String nome) {
        this();
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

    public String getLinguagem() { return linguagem; }
    public void setLinguagem(String linguagem) { this.linguagem = linguagem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public Double getNotaMedia() { return notaMedia; }
    public void setNotaMedia(Double notaMedia) { this.notaMedia = notaMedia; }

    public LocalDate getDataEstreia() { return dataEstreia; }
    public void setDataEstreia(LocalDate dataEstreia) { this.dataEstreia = dataEstreia; }

    public LocalDate getDataTermino() { return dataTermino; }
    public void setDataTermino(LocalDate dataTermino) { this.dataTermino = dataTermino; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getStatusLocal() { return statusLocal; }
    public void setStatusLocal(String statusLocal) { this.statusLocal = statusLocal; }

    public Set<Genero> getGeneros() { return generos; }
    public void setGeneros(Set<Genero> generos) { this.generos = generos; }

    public void addGenero(Genero genero) { this.generos.add(genero); }

    @Override
    public String toString() {
        return String.format("[%d] %s (%.1f★) - %s", idExterno, nome, notaMedia != null ? notaMedia : 0.0, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Serie serie = (Serie) o;
        return idExterno == serie.idExterno;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExterno);
    }
}
