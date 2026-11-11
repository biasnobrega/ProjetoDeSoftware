package minharefeicao.entidade;

/** «entity» ItemSobremesa: item do pedido que referencia uma Sobremesa. */
public class ItemSobremesa extends ItemPedido {

    private Sobremesa sobremesa;

    public ItemSobremesa(Sobremesa sobremesa, int quantidade) {
        this.sobremesa = sobremesa;
        this.quantidade = quantidade;
    }

    @Override
    public String getDescricao() {
        return "Sobremesa: " + quantidade + "x " + sobremesa.getNome();
    }
}
