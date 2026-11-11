package minharefeicao.entidade;

/** «entity» PlanoAssinatura: dados de um plano disponível para contratação. */
public class PlanoAssinatura {

    private int idPlano;
    private String nome;
    private int quantidadeRefeicoes;
    private String periodicidadeEntrega;
    private double valorMensal;
    private boolean ativo;

    /** Construtor usado pelo PlanoDAO ao carregar planos.csv (pré-condição: planos disponíveis). */
    public PlanoAssinatura(int idPlano, String nome, int quantidadeRefeicoes,
                           String periodicidadeEntrega, double valorMensal, boolean ativo) {
        this.idPlano = idPlano;
        this.nome = nome;
        this.quantidadeRefeicoes = quantidadeRefeicoes;
        this.periodicidadeEntrega = periodicidadeEntrega;
        this.valorMensal = valorMensal;
        this.ativo = ativo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeRefeicoes() {
        return quantidadeRefeicoes;
    }

    public String getPeriodicidadeEntrega() {
        return periodicidadeEntrega;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    /** Getter extra (fora da UML): a tela e a coleção precisam do id. */
    public int getIdPlano() {
        return idPlano;
    }
}
