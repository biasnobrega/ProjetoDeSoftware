package minharefeicao.entidade;

/** «entity» ItemPrato: item do pedido que referencia um PratoPrincipal. */
public class ItemPrato extends ItemPedido {

    private PratoPrincipal prato;

    public ItemPrato(PratoPrincipal prato, int quantidade) {
        this.prato = prato;
        this.quantidade = quantidade;
    }

    @Override
    public String getDescricao() {
        return "Prato: " + quantidade + "x " + prato.getNome();
    }
}
