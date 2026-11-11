package minharefeicao.entidade;

import java.time.LocalDateTime;

/** «entity» Pagamento: nasce PENDENTE e vira AUTORIZADO ou RECUSADO depois da operadora. */
public class Pagamento {

    private static int proximoId = 1;

    private int idPagamento;
    private LocalDateTime dataPagamento;
    private double valor;
    private StatusPagamento status;
    private String codigoAutorizacao;

    // Pagamento -> CartaoCredito: associação navegável
    private CartaoCredito cartao;

    public Pagamento(double valor, CartaoCredito cartao) {
        this.idPagamento = proximoId++;
        this.valor = valor;
        this.cartao = cartao;
        this.status = StatusPagamento.PENDENTE;
    }

    public void confirmar(String codigoAutorizacao) {
        this.codigoAutorizacao = codigoAutorizacao;
        this.dataPagamento = LocalDateTime.now();
        this.status = StatusPagamento.AUTORIZADO;
    }

    public void recusar() {
        this.status = StatusPagamento.RECUSADO;
    }

    /** Getter extra (fora da UML): o controlador envia o valor à operadora. */
    public double getValor() {
        return valor;
    }

    /** Getter extra (fora da UML): usado para gravar o status no assinaturas.csv. */
    public StatusPagamento getStatus() {
        return status;
    }

    /** Getter extra (fora da UML): usado para gravar o final do cartão. */
    public CartaoCredito getCartao() {
        return cartao;
    }
}
