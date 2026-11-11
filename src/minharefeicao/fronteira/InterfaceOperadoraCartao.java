package minharefeicao.fronteira;

import java.util.Random;

import minharefeicao.entidade.CartaoCredito;

/**
 * «boundary» InterfaceOperadoraCartao (simulada): cartão terminado em 0000 é recusado;
 * os demais são autorizados e recebem um código. Depende de CartaoCredito só como parâmetro.
 */
public class InterfaceOperadoraCartao {

    public String solicitarAutorizacao(CartaoCredito cartao, String cvv, double valor) {
        // Passo 27: a operadora (ator secundário) responde com o código ou com a recusa
        return autorizarPagamento(cartao.getNumeroCartao(), cartao.getValidade(), cvv, valor);
    }

    /** Mensagem enviada ao ator Operadora de Cartão de Crédito (simulada aqui dentro). */
    private String autorizarPagamento(String numeroCartao, String validade, String cvv, double valor) {
        System.out.println("[Operadora simulada] Valor " + valor + ", cartão final "
                + numeroCartao.substring(Math.max(0, numeroCartao.length() - 4)));
        if (numeroCartao.endsWith("0000")) {
            return null; // recusado (FA3)
        }
        return "AUT-" + String.format("%06d", new Random().nextInt(1000000));
    }
}
