package src.tvmaze.model;

public class Participacao {
    private int id;
    private int serieId;
    private int pessoaId;
    private String personagem;
    private Pessoa pessoa;

    public Participacao() {}

    public Participacao(int serieId, int pessoaId, String personagem) {
        this.serieId = serieId;
        this.pessoaId = pessoaId;
        this.personagem = personagem;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSerieId() { return serieId; }
    public void setSerieId(int serieId) { this.serieId = serieId; }

    public int getPessoaId() { return pessoaId; }
    public void setPessoaId(int pessoaId) { this.pessoaId = pessoaId; }

    public String getPersonagem() { return personagem; }
    public void setPersonagem(String personagem) { this.personagem = personagem; }

    public Pessoa getPessoa() { return pessoa; }
    public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }

    @Override
    public String toString() {
        return String.format("%s como %s", pessoa != null ? pessoa.getNome() : "N/A", personagem);
    }
}
