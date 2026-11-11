package minharefeicao.entidade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** «entity» Pedido: raiz da composição Pedido ◆ ItemPedido; cria os itens e a Entrega. */
public class Pedido {

    private static int proximoId = 1;

    private int idPedido;
    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private double valorTotal;

    // Composição: a lista é criada aqui e os itens só nascem com new dentro do Pedido
    private List<ItemPedido> itens = new ArrayList<ItemPedido>();

    // Pedido -> Entrega: associação navegável (0..1, criada em aprovar)
    private Entrega entrega;

    public Pedido() {
        this.idPedido = proximoId++;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedido.EM_MONTAGEM;
        this.valorTotal = 0;
    }

    public boolean adicionarPrato(PratoPrincipal prato, int quantidade, int limite) {
        if (totalPratos() + quantidade <= limite) {
            // Passo 12: «create» ItemPrato
            itens.add(new ItemPrato(prato, quantidade));
            return true;
        }
        // FA2: quantidade acima do limite do plano
        return false;
    }

    private int totalPratos() {
        int total = 0;
        for (ItemPedido item : itens) {
            if (item instanceof ItemPrato) {
                total += item.getQuantidade();
            }
        }
        return total;
    }

    public void adicionarAcompanhamento(Acompanhamento acomp, int quantidade) {
        // Passo 15: «create» ItemAcompanhamento
        itens.add(new ItemAcompanhamento(acomp, quantidade));
    }

    public boolean adicionarSobremesa(Sobremesa sobremesa, int quantidade, int limite) {
        if (totalSobremesas() + quantidade <= limite) {
            // Passo 18: «create» ItemSobremesa
            itens.add(new ItemSobremesa(sobremesa, quantidade));
            return true;
        }
        // FA2: quantidade acima do limite do plano
        return false;
    }

    private int totalSobremesas() {
        int total = 0;
        for (ItemPedido item : itens) {
            if (item instanceof ItemSobremesa) {
                total += item.getQuantidade();
            }
        }
        return total;
    }

    public String gerarResumo() {
        String resumo = "";
        for (ItemPedido item : itens) {
            // getDescricao() é polimórfico: cada tipo de item monta o seu texto
            resumo += item.getDescricao() + "\n";
        }
        return resumo;
    }

    public void definirValorTotal(double valor) {
        this.valorTotal = valor;
    }

    public void aprovar(EnderecoEntrega end) {
        alterarStatus(StatusPedido.APROVADO);
        // Passo 29: «create» Entrega no endereço informado
        this.entrega = new Entrega(end);
    }

    private void alterarStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDate getDataPrevistaEntrega() {
        if (entrega == null) {
            return null;
        }
        return entrega.getDataPrevista();
    }

    public void cancelar() {
        alterarStatus(StatusPedido.CANCELADO);
    }

    /** Getter extra (fora da UML): o valor do Pagamento vem do total do pedido. */
    public double getValorTotal() {
        return valorTotal;
    }
}
