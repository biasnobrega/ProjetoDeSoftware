package minharefeicao.entidade;

/** «entity» Acompanhamento. */
public class Acompanhamento extends ItemCardapio {

    public Acompanhamento(int idItemCardapio, String nome, String descricao, int calorias,
                          boolean disponivel) {
        super(idItemCardapio, nome, descricao, calorias, disponivel);
    }
}
