package minharefeicao.entidade;

import java.util.ArrayList;
import java.util.List;

/** «entity» Assinante: cria e mantém código, preferências, assinaturas, endereços e cartões. */
public class Assinante {

    private static int proximoId = 1;

    private int idAssinante;
    private String nome;
    private String celular;

    // Assinante -> ... (associações navegáveis; 0..* vira List)
    private List<CodigoConfirmacao> codigos = new ArrayList<CodigoConfirmacao>();
    private List<PreferenciaAlimentar> preferencias = new ArrayList<PreferenciaAlimentar>();
    private List<Assinatura> assinaturas = new ArrayList<Assinatura>();
    private List<EnderecoEntrega> enderecos = new ArrayList<EnderecoEntrega>();
    private List<CartaoCredito> cartoes = new ArrayList<CartaoCredito>();

    public Assinante(String celular) {
        this.idAssinante = proximoId++;
        this.nome = "";
        this.celular = celular;
    }

    /** Acréscimo (fora da UML): o AssinanteDAO recria assinantes já gravados no CSV. */
    public Assinante(int idAssinante, String nome, String celular) {
        this.idAssinante = idAssinante;
        this.nome = nome;
        this.celular = celular;
        if (idAssinante >= proximoId) {
            proximoId = idAssinante + 1;
        }
    }

    public CodigoConfirmacao gerarCodigoConfirmacao() {
        // Passo 2: «create» CodigoConfirmacao
        CodigoConfirmacao cod = new CodigoConfirmacao();
        codigos.add(cod);
        return cod;
    }

    public ResultadoValidacao validarCodigo(String codigo) {
        // Passo 4: valida sempre o último código gerado
        CodigoConfirmacao ultimo = codigos.get(codigos.size() - 1);
        return ultimo.validar(codigo);
    }

    public Assinatura contratarPlano(PlanoAssinatura p) {
        // Passo 6: «create» Assinatura
        Assinatura as = new Assinatura(this, p);
        assinaturas.add(as);
        return as;
    }

    public void adicionarPreferencia(String nome) {
        for (PreferenciaAlimentar existente : preferencias) {
            if (existente.getNome().equalsIgnoreCase(nome)) {
                return;
            }
        }
        // Passo 9: «create» PreferenciaAlimentar
        preferencias.add(new PreferenciaAlimentar(nome));
    }

    public List<PreferenciaAlimentar> getPreferencias() {
        return preferencias;
    }

    public EnderecoEntrega adicionarEndereco(String cep, String logradouro, String numero,
                                             String complemento, String bairro, String cidade) {
        // Passo 22: «create» EnderecoEntrega
        EnderecoEntrega end = new EnderecoEntrega(cep, logradouro, numero, complemento, bairro, cidade);
        enderecos.add(end);
        return end;
    }

    public CartaoCredito cadastrarCartao(String nomeTitular, String numeroCartao, String validade) {
        // Passo 25: «create» CartaoCredito
        CartaoCredito cartao = new CartaoCredito(nomeTitular, numeroCartao, validade);
        cartoes.add(cartao);
        return cartao;
    }

    // Getters extras (fora da UML)

    public int getIdAssinante() {
        return idAssinante;
    }

    public String getNome() {
        return nome;
    }

    public String getCelular() {
        return celular;
    }
}
