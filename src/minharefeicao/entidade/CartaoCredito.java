package minharefeicao.entidade;

/**
 * «entity» CartaoCredito. O CVV não é atributo: ele só passa pela operadora
 * (InterfaceOperadoraCartao.solicitarAutorizacao) e nunca é guardado.
 */
public class CartaoCredito {

    private static int proximoId = 1;

    private int idCartao;
    private String nomeTitular;
    private String numero;
    private String validade;
    private String bandeira;

    public CartaoCredito(String nomeTitular, String numeroCartao, String validade) {
        this.idCartao = proximoId++;
        this.nomeTitular = nomeTitular;
        this.numero = numeroCartao;
        this.validade = validade;
        this.bandeira = descobrirBandeira(numeroCartao);
    }

    private String descobrirBandeira(String numero) {
        if (numero.startsWith("4")) {
            return "Visa";
        }
        if (numero.startsWith("5")) {
            return "Mastercard";
        }
        return "Outra";
    }

    /** Getter extra (fora da UML): a operadora simulada precisa do número. */
    public String getNumeroCartao() {
        return numero;
    }

    /** Getter extra (fora da UML): a operadora simulada precisa da validade. */
    public String getValidade() {
        return validade;
    }

    /** Acréscimo (fora da UML): só o final do cartão é gravado no CSV. */
    public String getFinalCartao() {
        int tamanho = numero.length();
        return tamanho <= 4 ? numero : numero.substring(tamanho - 4);
    }
}
