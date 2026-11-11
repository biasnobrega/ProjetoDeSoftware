package minharefeicao.entidade;

/** «entity» Sobremesa. */
public class Sobremesa extends ItemCardapio {

    public Sobremesa(int idItemCardapio, String nome, String descricao, int calorias,
                     boolean disponivel) {
        super(idItemCardapio, nome, descricao, calorias, disponivel);
    }
}
