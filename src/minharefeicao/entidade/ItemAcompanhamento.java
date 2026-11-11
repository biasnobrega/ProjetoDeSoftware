package minharefeicao.entidade;

/** «entity» ItemAcompanhamento: item do pedido que referencia um Acompanhamento. */
public class ItemAcompanhamento extends ItemPedido {

    private Acompanhamento acomp;

    public ItemAcompanhamento(Acompanhamento acomp, int quantidade) {
        this.acomp = acomp;
        this.quantidade = quantidade;
    }

    @Override
    public String getDescricao() {
        return "Acompanhamento: " + quantidade + "x " + acomp.getNome();
    }
}
