package src.tvmaze.model;

import java.time.LocalDate;

public class Episodio {
    private int id;
    private int idExterno;
    private int serieId;
    private int temporada;
    private int numero;
    private String nome;
    private LocalDate dataExibicao;
    private int duracao;

    public Episodio() {}

    public Episodio(int idExterno, int serieId, int temporada, int numero, String nome) {
        this.idExterno = idExterno;
        this.serieId = serieId;
        this.temporada = temporada;
        this.numero = numero;
        this.nome = nome;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdExterno() { return idExterno; }
    public void setIdExterno(int idExterno) { this.idExterno = idExterno; }

    public int getSerieId() { return serieId; }
    public void setSerieId(int serieId) { this.serieId = serieId; }

    public int getTemporada() { return temporada; }
    public void setTemporada(int temporada) { this.temporada = temporada; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getDataExibicao() { return dataExibicao; }
    public void setDataExibicao(LocalDate dataExibicao) { this.dataExibicao = dataExibicao; }

    public int getDuracao() { return duracao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }

    @Override
    public String toString() {
        return String.format("S%02dE%02d - %s (%d min)", temporada, numero, nome, duracao);
    }
}
