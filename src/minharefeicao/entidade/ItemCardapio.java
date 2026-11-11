package minharefeicao.entidade;

/** Classe abstrata ItemCardapio: superclasse de PratoPrincipal, Acompanhamento e Sobremesa. */
public abstract class ItemCardapio {

    private int idItemCardapio;
    private String nome;
    private String descricao;
    private int calorias;
    private boolean disponivel;

    /** Construtor usado pelo CardapioDAO ao carregar cardapio.csv. */
    public ItemCardapio(int idItemCardapio, String nome, String descricao, int calorias,
                        boolean disponivel) {
        this.idItemCardapio = idItemCardapio;
        this.nome = nome;
        this.descricao = descricao;
        this.calorias = calorias;
        this.disponivel = disponivel;
    }

    public String getNome() {
        return nome;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    /** Getter extra (fora da UML): a tela identifica o item escolhido pelo id. */
    public int getIdItemCardapio() {
        return idItemCardapio;
    }

    /** Getter extra (fora da UML): exibido na lista da tela. */
    public int getCalorias() {
        return calorias;
    }

    /** Getter extra (fora da UML): exibido na lista da tela. */
    public String getDescricao() {
        return descricao;
    }
}
