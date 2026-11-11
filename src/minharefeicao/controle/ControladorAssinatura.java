package minharefeicao.controle;

import java.util.List;

import minharefeicao.colecao.Assinantes;
import minharefeicao.colecao.PlanosAssinatura;
import minharefeicao.entidade.Acompanhamento;
import minharefeicao.entidade.Assinante;
import minharefeicao.entidade.Assinatura;
import minharefeicao.entidade.Cardapio;
import minharefeicao.entidade.CartaoCredito;
import minharefeicao.entidade.CodigoConfirmacao;
import minharefeicao.entidade.EnderecoEntrega;
import minharefeicao.entidade.Pagamento;
import minharefeicao.entidade.PlanoAssinatura;
import minharefeicao.entidade.PratoPrincipal;
import minharefeicao.entidade.PreferenciaAlimentar;
import minharefeicao.entidade.ResultadoValidacao;
import minharefeicao.entidade.Sobremesa;
import minharefeicao.fronteira.InterfaceOperadoraCartao;
import minharefeicao.fronteira.InterfaceSMS;

/**
 * «control» ControladorAssinatura: coordena o caso de uso Assinar Plano de Refeições.
 * Cada método corresponde a uma mensagem TelaAssinatura -> ControladorAssinatura do diagrama
 * de sequência e repassa o trabalho para a entidade que tem a informação.
 */
public class ControladorAssinatura {

    // Associações navegáveis (1)
    private Assinantes assinantes;
    private PlanosAssinatura planos;
    private Cardapio cardapio;
    private InterfaceSMS interfaceSMS;
    private InterfaceOperadoraCartao interfaceOperadoraCartao;

    // Objetos do processo em andamento, guardados entre um estímulo e outro
    private Assinante assinanteAtual;
    private Assinatura assinaturaAtual;
    private Pagamento pagamentoAtual;

    public ControladorAssinatura(Assinantes assinantes, PlanosAssinatura planos, Cardapio cardapio,
                                 InterfaceSMS interfaceSMS,
                                 InterfaceOperadoraCartao interfaceOperadoraCartao) {
        this.assinantes = assinantes;
        this.planos = planos;
        this.cardapio = cardapio;
        this.interfaceSMS = interfaceSMS;
        this.interfaceOperadoraCartao = interfaceOperadoraCartao;
    }

    // ---------------------------------------------------------------- Passos 1 a 4 (e FA1)

    public void iniciarAssinatura(String celular) {
        // Passo 1: busca o assinante pelo celular
        Assinante a = assinantes.buscarPorCelular(celular);
        if (a == null) {
            // opt [a == null]: assinante novo (se já existe no CSV, é reaproveitado)
            a = new Assinante(celular);
            assinantes.adicionar(a);
        }
        this.assinanteAtual = a;
        this.assinaturaAtual = null;
        this.pagamentoAtual = null;

        // Passo 2: gera o código e envia por SMS
        CodigoConfirmacao cod = a.gerarCodigoConfirmacao();
        String codigo = cod.getCodigo();
        interfaceSMS.enviarCodigo(celular, codigo);
    }

    public ResultadoValidacao validarCodigo(String codigo) {
        // Passo 4 / FA1: o resultado (VALIDO, INVALIDO, BLOQUEADO) volta para a tela
        ResultadoValidacao resultado = assinanteAtual.validarCodigo(codigo);
        return resultado;
    }

    public void reenviarCodigo() {
        // FA1.5: gera e envia um novo código
        CodigoConfirmacao novoCod = assinanteAtual.gerarCodigoConfirmacao();
        String codigo = novoCod.getCodigo();
        interfaceSMS.enviarCodigo(assinanteAtual.getCelular(), codigo);
    }

    // ---------------------------------------------------------------- Passos 5 a 9

    public List<PlanoAssinatura> listarPlanos() {
        // Passo 5
        return planos.listarPlanosAtivos();
    }

    public PlanoAssinatura consultarPlano(int idPlano) {
        // Passo 6: dados do plano para conferência
        PlanoAssinatura p = planos.buscarPlano(idPlano);
        return p;
    }

    public void contratarPlano(int idPlano) {
        // Passo 6: cria a assinatura (e, dentro dela, o pedido)
        PlanoAssinatura p = planos.buscarPlano(idPlano);
        this.assinaturaAtual = assinanteAtual.contratarPlano(p);
        this.pagamentoAtual = null;
    }

    public void registrarPreferencias(List<String> nomes) {
        // Passo 9: cria uma PreferenciaAlimentar para cada nome
        for (String nome : nomes) {
            assinanteAtual.adicionarPreferencia(nome);
        }
    }

    // ---------------------------------------------------------------- Passos 10 a 12 (e FA2)

    public List<PratoPrincipal> listarPratosDisponiveis() {
        // Passo 10: só pratos compatíveis com as preferências do assinante
        List<PreferenciaAlimentar> prefs = assinanteAtual.getPreferencias();
        List<PratoPrincipal> pratos = cardapio.listarPratosDisponiveis(prefs);
        return pratos;
    }

    public boolean adicionarPrato(int idPrato, int quantidade) {
        // Passo 12: false significa FA2 (acima do limite do plano)
        PratoPrincipal prato = cardapio.buscarPrato(idPrato);
        boolean adicionado = assinaturaAtual.adicionarPrato(prato, quantidade);
        return adicionado;
    }

    // ---------------------------------------------------------------- Passos 13 a 15

    public List<Acompanhamento> listarAcompanhamentos() {
        // Passo 13
        List<Acompanhamento> acompanhamentos = cardapio.listarAcompanhamentos();
        return acompanhamentos;
    }

    public void adicionarAcompanhamento(int idAcompanhamento, int quantidade) {
        // Passo 15: acompanhamentos não têm limite
        Acompanhamento acomp = cardapio.buscarAcompanhamento(idAcompanhamento);
        assinaturaAtual.adicionarAcompanhamento(acomp, quantidade);
    }

    // ---------------------------------------------------------------- Passos 16 a 18 (e FA2)

    public List<Sobremesa> listarSobremesas() {
        // Passo 16
        List<Sobremesa> sobremesas = cardapio.listarSobremesas();
        return sobremesas;
    }

    public boolean adicionarSobremesa(int idSobremesa, int quantidade) {
        // Passo 18: false significa FA2 (acima do limite do plano)
        Sobremesa sobremesa = cardapio.buscarSobremesa(idSobremesa);
        boolean adicionado = assinaturaAtual.adicionarSobremesa(sobremesa, quantidade);
        return adicionado;
    }

    // ---------------------------------------------------------------- Passos 19 a 24

    public String obterResumoPedido() {
        // Passo 19
        String resumo = assinaturaAtual.gerarResumoPedido();
        return resumo;
    }

    public double registrarEnderecoEntrega(String cep, String logradouro, String numero,
                                           String complemento, String bairro, String cidade) {
        // Passo 22: armazena o endereço
        EnderecoEntrega end = assinanteAtual.adicionarEndereco(cep, logradouro, numero,
                complemento, bairro, cidade);
        assinaturaAtual.definirEnderecoEntrega(end);
        // Passo 23: status AGUARDANDO_PAGAMENTO
        assinaturaAtual.aguardarPagamento();
        // Passo 24: valor total para a tela
        double valorTotal = assinaturaAtual.calcularValorTotal();
        return valorTotal;
    }

    // ---------------------------------------------------------------- Passos 25 a 32 (e FA3)

    public boolean efetuarPagamento(String nomeTitular, String numeroCartao, String validade,
                                    String cvv) {
        // Passo 25: cria o cartão e o pagamento (uma nova tentativa cria novos objetos, FA3.4)
        CartaoCredito cartao = assinanteAtual.cadastrarCartao(nomeTitular, numeroCartao, validade);
        this.pagamentoAtual = assinaturaAtual.registrarPagamento(cartao);
        double valor = pagamentoAtual.getValor();

        // Passos 26 e 27: o CVV só passa pela operadora, nunca é guardado
        String codigoAutorizacao = interfaceOperadoraCartao.solicitarAutorizacao(cartao, cvv, valor);

        if (codigoAutorizacao != null) {
            // alt: pagamento autorizado
            // Passo 28: registra o pagamento como AUTORIZADO
            pagamentoAtual.confirmar(codigoAutorizacao);
            // Passos 29 e 30: ativa a assinatura, aprova o pedido (cria a Entrega) e gera o protocolo
            String protocolo = assinaturaAtual.ativar();
            // Acréscimo (fora da UML): grava a assinatura no assinaturas.csv
            assinantes.gravarAssinatura(assinanteAtual, assinaturaAtual);
            return true;
        } else {
            // FA3.1 a FA3.3: pagamento recusado, a assinatura continua AGUARDANDO_PAGAMENTO
            pagamentoAtual.recusar();
            return false;
        }
    }

    public String obterConfirmacao() {
        // Passos 31 e 32
        String confirmacao = assinaturaAtual.gerarConfirmacao();
        return confirmacao;
    }

    public void cancelarAssinatura() {
        // FA3.6: o assinante desiste
        assinaturaAtual.cancelar();
        // Acréscimo (fora da UML): grava a assinatura CANCELADA no assinaturas.csv
        assinantes.gravarAssinatura(assinanteAtual, assinaturaAtual);
    }
}
