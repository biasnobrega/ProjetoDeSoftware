package minharefeicao.entidade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** «entity» Assinatura: coordena plano, pedido, endereço e pagamentos da contratação. */
public class Assinatura {

    private static int proximoId = 1;

    private int idAssinatura;
    private LocalDate dataContratacao;
    private StatusAssinatura status;
    private String protocolo;

    // Assinatura -> PlanoAssinatura, Pedido, EnderecoEntrega (0..1) e Pagamento (0..*)
    private PlanoAssinatura plano;
    private Pedido pedido;
    private EnderecoEntrega endereco;
    private List<Pagamento> pagamentos = new ArrayList<Pagamento>();

    /**
     * Acréscimo (fora da UML): o AssinanteDAO informa o maior id já gravado em assinaturas.csv,
     * para que o protocolo não se repita de uma execução para outra.
     */
    public static void continuarNumeracao(int ultimoIdGravado) {
        if (ultimoIdGravado >= proximoId) {
            proximoId = ultimoIdGravado + 1;
        }
    }

    public Assinatura(Assinante a, PlanoAssinatura p) {
        // O Assinante entra só como parâmetro (a seta do diagrama de classes sai do Assinante)
        this.idAssinatura = proximoId++;
        this.dataContratacao = LocalDate.now();
        this.status = StatusAssinatura.PENDENTE;
        this.plano = p;
        // Passo 6: «create» Pedido
        this.pedido = new Pedido();
    }

    public boolean adicionarPrato(PratoPrincipal prato, int quantidade) {
        int limite = plano.getQuantidadeRefeicoes();
        return pedido.adicionarPrato(prato, quantidade, limite);
    }

    public void adicionarAcompanhamento(Acompanhamento acomp, int quantidade) {
        pedido.adicionarAcompanhamento(acomp, quantidade);
    }

    public boolean adicionarSobremesa(Sobremesa sobremesa, int quantidade) {
        int limite = plano.getQuantidadeRefeicoes();
        return pedido.adicionarSobremesa(sobremesa, quantidade, limite);
    }

    public String gerarResumoPedido() {
        return pedido.gerarResumo();
    }

    public void definirEnderecoEntrega(EnderecoEntrega end) {
        this.endereco = end;
    }

    public void aguardarPagamento() {
        alterarStatus(StatusAssinatura.AGUARDANDO_PAGAMENTO);
    }

    public double calcularValorTotal() {
        double valor = plano.getValorMensal();
        pedido.definirValorTotal(valor);
        return valor;
    }

    public Pagamento registrarPagamento(CartaoCredito cartao) {
        // Passo 25: «create» Pagamento (PENDENTE) com o valor total do pedido
        Pagamento pag = new Pagamento(pedido.getValorTotal(), cartao);
        pagamentos.add(pag);
        return pag;
    }

    public String ativar() {
        alterarStatus(StatusAssinatura.ATIVA);
        pedido.aprovar(endereco);
        gerarProtocolo();
        return protocolo;
    }

    private String gerarProtocolo() {
        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.protocolo = "MR-" + data + "-" + String.format("%04d", idAssinatura);
        return protocolo;
    }

    public String gerarConfirmacao() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String texto = "Assinatura confirmada!\n\n";
        texto += "Protocolo: " + protocolo + "\n";
        texto += "Plano: " + plano.getNome() + " (entrega " + plano.getPeriodicidadeEntrega() + ")\n\n";
        texto += "Pedido:\n" + pedido.gerarResumo() + "\n";
        texto += "Endereço: " + endereco.getDescricao() + "\n";
        texto += "Data prevista da primeira entrega: " + pedido.getDataPrevistaEntrega().format(fmt);
        return texto;
    }

    public void cancelar() {
        alterarStatus(StatusAssinatura.CANCELADA);
        pedido.cancelar();
    }

    private void alterarStatus(StatusAssinatura status) {
        this.status = status;
    }

    // Getters extras (fora da UML), usados só para gravar o assinaturas.csv

    public int getIdAssinatura() {
        return idAssinatura;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public LocalDate getDataContratacao() {
        return dataContratacao;
    }

    public StatusAssinatura getStatus() {
        return status;
    }

    public PlanoAssinatura getPlano() {
        return plano;
    }

    public EnderecoEntrega getEnderecoEntrega() {
        return endereco;
    }

    public double getValorTotal() {
        return pedido.getValorTotal();
    }

    public LocalDate getDataPrevistaEntrega() {
        return pedido.getDataPrevistaEntrega();
    }

    /** Último pagamento registrado, ou null se ainda não houve tentativa. */
    public Pagamento getUltimoPagamento() {
        if (pagamentos.isEmpty()) {
            return null;
        }
        return pagamentos.get(pagamentos.size() - 1);
    }
}
