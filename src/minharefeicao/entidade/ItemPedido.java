package minharefeicao.entidade;

/** Classe abstrata ItemPedido: superclasse dos itens da composição Pedido ◆ ItemPedido. */
public abstract class ItemPedido {

    protected int quantidade;

    public int getQuantidade() {
        return quantidade;
    }

    /** Método polimórfico: cada subclasse monta a própria descrição. */
    public abstract String getDescricao();
}
