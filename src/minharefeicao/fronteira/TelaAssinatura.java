package minharefeicao.fronteira;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import minharefeicao.controle.ControladorAssinatura;
import minharefeicao.entidade.Acompanhamento;
import minharefeicao.entidade.PlanoAssinatura;
import minharefeicao.entidade.PratoPrincipal;
import minharefeicao.entidade.ResultadoValidacao;
import minharefeicao.entidade.Sobremesa;

/**
 * «boundary» TelaAssinatura: um JFrame com CardLayout, uma "página" por tela do protótipo.
 * Os métodos públicos são os 18 estímulos do Assinante; os privados exibir.../solicitar...
 * são as respostas (mensagens da tela para ela mesma). Não há regra de negócio aqui.
 */
public class TelaAssinatura extends JFrame {

    // TelaAssinatura -> ControladorAssinatura: associação navegável
    private ControladorAssinatura controlador;

    // Estado de interface entre dois estímulos (fora da UML)
    private int idPlanoSelecionado;
    private int limitePlano;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private int totalPratos;
    private int totalAcompanhamentos;
    private int totalSobremesas;
    private String etapaAtual = "";
    // Ids dos itens mostrados em cada lista, na mesma ordem (a tela guarda só números)
    private List<Integer> idsPlanos = new ArrayList<Integer>();
    private List<Integer> idsPratos = new ArrayList<Integer>();
    private List<Integer> idsAcompanhamentos = new ArrayList<Integer>();
    private List<Integer> idsSobremesas = new ArrayList<Integer>();

    // Componentes
    private CardLayout cartas = new CardLayout();
    private JPanel painelCartas = new JPanel(cartas);

    private JTextField txtCelular = new JTextField(15);

    private JTextField txtCodigo = new JTextField(10);
    private JButton btnValidarCodigo = new JButton("Validar código");
    private JButton btnReenviarCodigo = new JButton("Reenviar código");

    private DefaultListModel<String> modeloPlanos = new DefaultListModel<String>();
    private JList<String> listaPlanos = new JList<String>(modeloPlanos);
    private JLabel lblDetalhePlano = new JLabel("Selecione um plano.");
    private JButton btnConfirmarPlano = new JButton("Continuar");

    private JCheckBox chkSemRestricoes = new JCheckBox("Não tenho restrições");
    private JCheckBox chkVegetariano = new JCheckBox("Vegetariano");
    private JCheckBox chkSemGluten = new JCheckBox("Sem glúten");
    private JCheckBox chkSemLactose = new JCheckBox("Sem lactose");
    private JCheckBox chkLowCarb = new JCheckBox("Low carb");

    private DefaultListModel<String> modeloPratos = new DefaultListModel<String>();
    private JList<String> listaPratos = new JList<String>(modeloPratos);
    private JSpinner spnPrato = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private JLabel lblContadorPratos = new JLabel(" ");

    private DefaultListModel<String> modeloAcompanhamentos = new DefaultListModel<String>();
    private JList<String> listaAcompanhamentos = new JList<String>(modeloAcompanhamentos);
    private JSpinner spnAcompanhamento = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private JLabel lblContadorAcompanhamentos = new JLabel(" ");

    private DefaultListModel<String> modeloSobremesas = new DefaultListModel<String>();
    private JList<String> listaSobremesas = new JList<String>(modeloSobremesas);
    private JSpinner spnSobremesa = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private JLabel lblContadorSobremesas = new JLabel(" ");

    private JTextArea txtResumo = new JTextArea();

    private JTextField txtCep = new JTextField(12);
    private JTextField txtLogradouro = new JTextField(30);
    private JTextField txtNumero = new JTextField(8);
    private JTextField txtComplemento = new JTextField(15);
    private JTextField txtBairro = new JTextField(20);
    private JTextField txtCidade = new JTextField(20);
    private JTextArea txtEnderecoConferencia = new JTextArea();

    private JLabel lblValorTotal = new JLabel(" ");
    private JTextField txtTitular = new JTextField(25);
    private JTextField txtNumeroCartao = new JTextField(20);
    private JTextField txtValidade = new JTextField(6);
    private JPasswordField txtCvv = new JPasswordField(5);
    private JButton btnDesistir = new JButton("Desistir da assinatura");

    private JTextArea txtConfirmacao = new JTextArea();

    public TelaAssinatura(ControladorAssinatura controlador) {
        super("Minha Refeição - Assinar Plano de Refeições");
        this.controlador = controlador;
        montarTela();
    }

    // =====================================================================================
    // Estímulos do Assinante (métodos públicos)
    // =====================================================================================

    public void iniciarContratacao() {
        reiniciarEstado();
        solicitarNumeroCelular();
    }

    public void informarNumeroCelular(String celular) {
        // Passo 1 e 2: o controlador gera o código e envia o SMS
        controlador.iniciarAssinatura(celular);
        mostrar("codigo");
        exibirMensagem("Informe o código recebido por SMS");
    }

    public void informarCodigoConfirmacao(String codigo) {
        // Passo 3 e 4: valida o código
        ResultadoValidacao resultado = controlador.validarCodigo(codigo);
        if (resultado == ResultadoValidacao.VALIDO) {
            // Passo 5: apresenta os planos
            exibirPlanos(controlador.listarPlanos());
        } else if (resultado == ResultadoValidacao.INVALIDO) {
            // FA1.2/FA1.3
            exibirMensagem("Código inválido. Tente novamente");
        } else {
            // FA1.5: 3 tentativas inválidas; só "Reenviar código" continua liberado
            btnValidarCodigo.setEnabled(false);
            exibirMensagem("Limite de tentativas atingido. Solicite um novo código");
        }
    }

    public void solicitarNovoCodigo() {
        // FA1.5: gera e envia um novo código; volta ao passo 3
        controlador.reenviarCodigo();
        txtCodigo.setText("");
        btnValidarCodigo.setEnabled(true);
        exibirMensagem("Novo código enviado");
    }

    public void selecionarPlano(int idPlano) {
        // Passo 6: guarda o id e mostra os dados do plano para conferência
        this.idPlanoSelecionado = idPlano;
        PlanoAssinatura p = controlador.consultarPlano(idPlano);
        this.limitePlano = p.getQuantidadeRefeicoes();
        // O campo de quantidade não aceita mais que o limite do plano de uma vez; o FA2
        // continua acontecendo quando a soma passa do limite (ex.: 15 + 10 num plano de 20)
        spnPrato.setModel(new SpinnerNumberModel(1, 1, limitePlano, 1));
        spnAcompanhamento.setModel(new SpinnerNumberModel(1, 1, limitePlano, 1));
        spnSobremesa.setModel(new SpinnerNumberModel(1, 1, limitePlano, 1));
        exibirPlanoSelecionado(p);
    }

    public void confirmarPlano() {
        // Passo 6 e 7: cria a assinatura e pede as preferências
        controlador.contratarPlano(idPlanoSelecionado);
        solicitarPreferencias();
    }

    public void informarPreferenciasAlimentares(List<String> preferencias) {
        // Passo 8 e 9: registra as preferências
        controlador.registrarPreferencias(preferencias);
        // Passo 10: apresenta os pratos compatíveis
        exibirPratos(controlador.listarPratosDisponiveis());
    }

    public void selecionarPratoPrincipal(int idPrato, int quantidade) {
        // Passo 11 e 12
        boolean adicionado = controlador.adicionarPrato(idPrato, quantidade);
        if (adicionado) {
            atualizarContador(quantidade);
        } else {
            // FA2.2/FA2.3: o plano não permite essa quantidade; a seleção continua
            exibirMensagem("Seu plano permite até " + limitePlano
                    + " refeições. Ajuste a seleção");
        }
    }

    public void concluirSelecaoPratos() {
        // Passo 13
        exibirAcompanhamentos(controlador.listarAcompanhamentos());
    }

    public void selecionarAcompanhamento(int idAcompanhamento, int quantidade) {
        // Passo 14 e 15
        controlador.adicionarAcompanhamento(idAcompanhamento, quantidade);
        // Apenas retorno visual da tela (acompanhamentos não têm limite na E3)
        atualizarContador(quantidade);
    }

    public void concluirSelecaoAcompanhamentos() {
        // Passo 16
        exibirSobremesas(controlador.listarSobremesas());
    }

    public void selecionarSobremesa(int idSobremesa, int quantidade) {
        // Passo 17 e 18
        boolean adicionado = controlador.adicionarSobremesa(idSobremesa, quantidade);
        if (adicionado) {
            atualizarContador(quantidade);
        } else {
            // FA2.2/FA2.3
            exibirMensagem("Seu plano permite até " + limitePlano
                    + " sobremesas. Ajuste a seleção");
        }
    }

    public void concluirSelecaoSobremesas() {
        // Passo 19
        exibirResumoPedido(controlador.obterResumoPedido());
    }

    public void confirmarComposicaoPedido() {
        // Passo 20
        solicitarEnderecoEntrega();
    }

    public void informarEnderecoEntrega(String cep, String logradouro, String numero,
                                        String complemento, String bairro, String cidade) {
        // Passo 21: guarda o endereço até a confirmação
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        exibirEnderecoParaConferencia();
    }

    public void confirmarEnderecoEntrega() {
        // Passos 22 a 24: armazena o endereço, aguarda pagamento e devolve o valor total
        double valorTotal = controlador.registrarEnderecoEntrega(cep, logradouro, numero,
                complemento, bairro, cidade);
        exibirValorTotal(valorTotal);
    }

    public void confirmarPagamento(String nomeTitular, String numeroCartao, String validade,
                                   String cvv) {
        // Passo 25 a 28
        boolean aprovado = controlador.efetuarPagamento(nomeTitular, numeroCartao, validade, cvv);
        if (aprovado) {
            // Passos 29 a 32
            String confirmacao = controlador.obterConfirmacao();
            exibirConfirmacao(confirmacao);
        } else {
            // FA3.3: o assinante pode tentar de novo (FA3.4) ou desistir (FA3.6)
            btnDesistir.setVisible(true);
            exibirMensagem("Pagamento não aprovado. Informe outro cartão ou tente novamente");
        }
    }

    public void desistirDaAssinatura() {
        // FA3.6
        controlador.cancelarAssinatura();
        exibirMensagem("Assinatura encerrada sem ativação do plano");
        mostrar("inicio");
    }

    // =====================================================================================
    // Respostas da tela para ela mesma (métodos privados)
    // =====================================================================================

    private void solicitarNumeroCelular() {
        mostrar("celular");
    }

    private void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Minha Refeição", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exibirPlanos(List<PlanoAssinatura> planos) {
        modeloPlanos.clear();
        idsPlanos.clear();
        for (PlanoAssinatura p : planos) {
            idsPlanos.add(p.getIdPlano());
            modeloPlanos.addElement(p.getNome() + " - " + p.getQuantidadeRefeicoes()
                    + " refeições - entrega " + p.getPeriodicidadeEntrega() + " - "
                    + moeda(p.getValorMensal()) + "/mês");
        }
        lblDetalhePlano.setText("Selecione um plano.");
        btnConfirmarPlano.setEnabled(false);
        mostrar("planos");
    }

    private void exibirPlanoSelecionado(PlanoAssinatura p) {
        lblDetalhePlano.setText("<html><b>" + p.getNome() + "</b><br>"
                + p.getQuantidadeRefeicoes() + " refeições e até " + p.getQuantidadeRefeicoes()
                + " sobremesas por mês<br>Entrega " + p.getPeriodicidadeEntrega()
                + "<br>Valor: " + moeda(p.getValorMensal()) + "/mês</html>");
        btnConfirmarPlano.setEnabled(true);
    }

    private void solicitarPreferencias() {
        mostrar("preferencias");
    }

    private void exibirPratos(List<PratoPrincipal> pratos) {
        modeloPratos.clear();
        idsPratos.clear();
        for (PratoPrincipal p : pratos) {
            idsPratos.add(p.getIdItemCardapio());
            modeloPratos.addElement(p.getNome() + " - " + p.getDescricao() + " (" + p.getCalorias() + " kcal)");
        }
        etapaAtual = "pratos";
        totalPratos = 0;
        lblContadorPratos.setText("Pratos selecionados: 0 de " + limitePlano);
        mostrar("pratos");
    }

    private void exibirAcompanhamentos(List<Acompanhamento> acompanhamentos) {
        modeloAcompanhamentos.clear();
        idsAcompanhamentos.clear();
        for (Acompanhamento a : acompanhamentos) {
            idsAcompanhamentos.add(a.getIdItemCardapio());
            modeloAcompanhamentos.addElement(a.getNome() + " - " + a.getDescricao() + " (" + a.getCalorias() + " kcal)");
        }
        etapaAtual = "acompanhamentos";
        totalAcompanhamentos = 0;
        lblContadorAcompanhamentos.setText("Acompanhamentos selecionados: 0");
        mostrar("acompanhamentos");
    }

    private void exibirSobremesas(List<Sobremesa> sobremesas) {
        modeloSobremesas.clear();
        idsSobremesas.clear();
        for (Sobremesa s : sobremesas) {
            idsSobremesas.add(s.getIdItemCardapio());
            modeloSobremesas.addElement(s.getNome() + " - " + s.getDescricao() + " (" + s.getCalorias() + " kcal)");
        }
        etapaAtual = "sobremesas";
        totalSobremesas = 0;
        lblContadorSobremesas.setText("Sobremesas selecionadas: 0 de " + limitePlano);
        mostrar("sobremesas");
    }

    private void atualizarContador(int quantidade) {
        if (etapaAtual.equals("pratos")) {
            totalPratos += quantidade;
            lblContadorPratos.setText("Pratos selecionados: " + totalPratos + " de " + limitePlano);
        } else if (etapaAtual.equals("acompanhamentos")) {
            totalAcompanhamentos += quantidade;
            lblContadorAcompanhamentos.setText("Acompanhamentos selecionados: " + totalAcompanhamentos);
        } else {
            totalSobremesas += quantidade;
            lblContadorSobremesas.setText("Sobremesas selecionadas: " + totalSobremesas + " de " + limitePlano);
        }
    }

    private void exibirResumoPedido(String resumo) {
        txtResumo.setText(agruparItensIguais(resumo));
        mostrar("resumo");
    }

    // Só para exibição: o Pedido continua com um item por seleção (como no diagrama
    // da E3); aqui a tela soma as linhas do mesmo tipo e nome, ex. "12x Mousse".
    // As demais linhas do texto ficam onde estavam.
    private String agruparItensIguais(String texto) {
        Pattern padrao = Pattern.compile("^(.+?): (\\d+)x (.+)$");
        Map<String, Integer> quantidades = new LinkedHashMap<String, Integer>();
        List<String> ordem = new ArrayList<String>();
        for (String linha : texto.split("\n", -1)) {
            Matcher m = padrao.matcher(linha);
            if (m.matches()) {
                String chave = m.group(1) + ": " + m.group(3);
                Integer atual = quantidades.get(chave);
                if (atual == null) {
                    ordem.add(chave);
                    atual = 0;
                }
                quantidades.put(chave, atual + Integer.parseInt(m.group(2)));
            } else {
                ordem.add(null);
                ordem.add(linha);
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean proximaLiteral = false;
        boolean primeira = true;
        for (String item : ordem) {
            if (item == null) {
                proximaLiteral = true;
                continue;
            }
            if (!primeira) {
                sb.append("\n");
            }
            primeira = false;
            if (proximaLiteral) {
                sb.append(item);
                proximaLiteral = false;
            } else {
                int sep = item.indexOf(": ");
                sb.append(item.substring(0, sep)).append(": ").append(quantidades.get(item))
                  .append("x ").append(item.substring(sep + 2));
            }
        }
        return sb.toString();
    }

    private void solicitarEnderecoEntrega() {
        mostrar("endereco");
    }

    private void exibirEnderecoParaConferencia() {
        String texto = logradouro + ", " + numero;
        if (!complemento.isEmpty()) {
            texto += " (" + complemento + ")";
        }
        texto += "\n" + bairro + " - " + cidade + "\nCEP " + cep;
        txtEnderecoConferencia.setText(texto);
        mostrar("enderecoConferencia");
    }

    private void exibirValorTotal(double valorTotal) {
        lblValorTotal.setText("Valor total: " + moeda(valorTotal));
        btnDesistir.setVisible(false);
        mostrar("pagamento");
    }

    private void exibirConfirmacao(String confirmacao) {
        txtConfirmacao.setText(agruparItensIguais(confirmacao));
        mostrar("confirmacao");
    }

    // =====================================================================================
    // Montagem da janela e tratamento dos cliques (apenas validam a digitação e chamam
    // o estímulo correspondente; nada aqui é regra de negócio)
    // =====================================================================================

    private void montarTela() {
        // Máscaras: só aceitam o que cabe no campo e já formatam enquanto se digita
        mascara(txtCelular, "(##) #####-####");
        mascara(txtCodigo, "######");
        mascara(txtCep, "#####-###");
        mascara(txtNumeroCartao, "#### #### #### ####");
        mascara(txtValidade, "##/##");
        mascara(txtCvv, "###");
        limitarTexto(txtLogradouro, 60, "[\\p{L}\\d .,'ºª-]", false);
        limitarTexto(txtNumero, 6, "[\\dA-Za-z/]", true);
        limitarTexto(txtComplemento, 40, "[\\p{L}\\d .,'ºª/-]", false);
        limitarTexto(txtBairro, 40, "[\\p{L} .'-]", false);
        limitarTexto(txtCidade, 40, "[\\p{L} .'-]", false);
        limitarTexto(txtTitular, 26, "[\\p{L} ]", true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 600);
        setLocationRelativeTo(null);

        // inicio
        JButton btnComecar = new JButton("Começar");
        btnComecar.addActionListener(e -> iniciarContratacao());
        JLabel boasVindas = new JLabel("Receba refeições prontas, no seu ritmo.", JLabel.CENTER);
        boasVindas.setFont(boasVindas.getFont().deriveFont(Font.BOLD, 18f));
        painelCartas.add(pagina("Minha Refeição", boasVindas, btnComecar), "inicio");

        // celular
        JButton btnCelular = new JButton("Continuar");
        btnCelular.addActionListener(e -> cliqueCelular());
        painelCartas.add(pagina("Criação da conta",
                formulario(linha("Número de celular (com DDD):", txtCelular)), btnCelular), "celular");

        // codigo
        btnValidarCodigo.addActionListener(e -> cliqueCodigo());
        btnReenviarCodigo.addActionListener(e -> solicitarNovoCodigo());
        painelCartas.add(pagina("Validação por SMS",
                formulario(linha("Código recebido:", txtCodigo)), btnValidarCodigo, btnReenviarCodigo),
                "codigo");

        // planos
        listaPlanos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPlanos.addListSelectionListener(e -> cliquePlano(e.getValueIsAdjusting()));
        btnConfirmarPlano.addActionListener(e -> confirmarPlano());
        JPanel centroPlanos = new JPanel(new BorderLayout(0, 10));
        centroPlanos.add(new JScrollPane(listaPlanos), BorderLayout.CENTER);
        centroPlanos.add(lblDetalhePlano, BorderLayout.SOUTH);
        painelCartas.add(pagina("Escolha do plano", centroPlanos, btnConfirmarPlano), "planos");

        // preferencias
        JButton btnPreferencias = new JButton("Continuar");
        btnPreferencias.addActionListener(e -> cliquePreferencias());
        chkSemRestricoes.addActionListener(e -> cliqueSemRestricoes());
        painelCartas.add(pagina("Preferências alimentares",
                formulario(chkSemRestricoes, chkVegetariano, chkSemGluten, chkSemLactose, chkLowCarb),
                btnPreferencias),
                "preferencias");

        // pratos, acompanhamentos e sobremesas
        JButton btnAddPrato = new JButton("Adicionar prato");
        btnAddPrato.addActionListener(e -> cliqueAdicionarPrato());
        JButton btnFimPratos = new JButton("Continuar");
        btnFimPratos.addActionListener(e -> cliqueConcluirPratos());
        painelCartas.add(pagina("Seleção de refeições",
                painelSelecao(listaPratos, spnPrato, lblContadorPratos), btnAddPrato, btnFimPratos),
                "pratos");

        JButton btnAddAcomp = new JButton("Adicionar acompanhamento");
        btnAddAcomp.addActionListener(e -> cliqueAdicionarAcompanhamento());
        JButton btnFimAcomp = new JButton("Continuar");
        btnFimAcomp.addActionListener(e -> concluirSelecaoAcompanhamentos());
        painelCartas.add(pagina("Seleção de acompanhamentos",
                painelSelecao(listaAcompanhamentos, spnAcompanhamento, lblContadorAcompanhamentos),
                btnAddAcomp, btnFimAcomp), "acompanhamentos");

        JButton btnAddSobremesa = new JButton("Adicionar sobremesa");
        btnAddSobremesa.addActionListener(e -> cliqueAdicionarSobremesa());
        JButton btnFimSobremesas = new JButton("Continuar");
        btnFimSobremesas.addActionListener(e -> concluirSelecaoSobremesas());
        painelCartas.add(pagina("Seleção de sobremesas",
                painelSelecao(listaSobremesas, spnSobremesa, lblContadorSobremesas),
                btnAddSobremesa, btnFimSobremesas), "sobremesas");

        // resumo
        txtResumo.setEditable(false);
        JButton btnResumo = new JButton("Confirmar pedido");
        btnResumo.addActionListener(e -> confirmarComposicaoPedido());
        painelCartas.add(pagina("Resumo do pedido", new JScrollPane(txtResumo), btnResumo), "resumo");

        // endereco
        JButton btnEndereco = new JButton("Continuar");
        btnEndereco.addActionListener(e -> cliqueEndereco());
        painelCartas.add(pagina("Endereço de entrega", formulario(
                linha("CEP:", txtCep), linha("Rua:", txtLogradouro), linha("Número:", txtNumero),
                linha("Complemento:", txtComplemento), linha("Bairro:", txtBairro),
                linha("Cidade:", txtCidade)), btnEndereco), "endereco");

        // conferencia do endereco
        txtEnderecoConferencia.setEditable(false);
        JButton btnConfirmarEndereco = new JButton("Confirmar endereço");
        btnConfirmarEndereco.addActionListener(e -> confirmarEnderecoEntrega());
        painelCartas.add(pagina("Confira o endereço", txtEnderecoConferencia,
                btnConfirmarEndereco), "enderecoConferencia");

        // pagamento
        JButton btnPagar = new JButton("Confirmar pagamento");
        btnPagar.addActionListener(e -> cliquePagamento());
        btnDesistir.addActionListener(e -> desistirDaAssinatura());
        btnDesistir.setVisible(false);
        lblValorTotal.setFont(lblValorTotal.getFont().deriveFont(Font.BOLD, 16f));
        painelCartas.add(pagina("Pagamento", formulario(lblValorTotal,
                linha("Nome do titular:", txtTitular), linha("Número do cartão:", txtNumeroCartao),
                linha("Validade (MM/AA):", txtValidade), linha("CVV:", txtCvv)),
                btnPagar, btnDesistir), "pagamento");

        // confirmacao
        txtConfirmacao.setEditable(false);
        JButton btnConcluir = new JButton("Concluir");
        btnConcluir.addActionListener(e -> mostrar("inicio"));
        painelCartas.add(pagina("Assinatura confirmada", new JScrollPane(txtConfirmacao), btnConcluir),
                "confirmacao");

        add(painelCartas);
        mostrar("inicio");
    }

    private void cliqueCelular() {
        String celular = txtCelular.getText().replaceAll("\\D", "");
        // Celular: DDD de 11 a 99 e 9 dígitos começando com 9
        boolean valido = celular.matches("[1-9][1-9]9\\d{8}");
        if (!valido) {
            exibirMensagem("Informe o celular com DDD, por exemplo (11) 98888-7777.");
            return;
        }
        informarNumeroCelular(celular);
    }

    private void cliqueCodigo() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            exibirMensagem("Digite o código recebido por SMS.");
            return;
        }
        informarCodigoConfirmacao(codigo);
    }

    private void cliquePlano(boolean ajustando) {
        int indice = listaPlanos.getSelectedIndex();
        if (!ajustando && indice >= 0) {
            selecionarPlano(idsPlanos.get(indice));
        }
    }

    /** "Não tenho restrições" (protótipo) exclui as outras opções. */
    private void cliqueSemRestricoes() {
        boolean semRestricoes = chkSemRestricoes.isSelected();
        JCheckBox[] caixas = {chkVegetariano, chkSemGluten, chkSemLactose, chkLowCarb};
        for (JCheckBox caixa : caixas) {
            if (semRestricoes) {
                caixa.setSelected(false);
            }
            caixa.setEnabled(!semRestricoes);
        }
    }

    private void cliquePreferencias() {
        List<String> nomes = new ArrayList<String>();
        JCheckBox[] caixas = {chkVegetariano, chkSemGluten, chkSemLactose, chkLowCarb};
        for (JCheckBox caixa : caixas) {
            if (caixa.isSelected()) {
                nomes.add(caixa.getText());
            }
        }
        if (nomes.isEmpty() && !chkSemRestricoes.isSelected()) {
            exibirMensagem("Marque suas preferências ou \"Não tenho restrições\".");
            return;
        }
        // "Não tenho restrições" vira lista vazia: nenhum filtro sobre os pratos
        informarPreferenciasAlimentares(nomes);
    }

    private void cliqueAdicionarPrato() {
        int indice = listaPratos.getSelectedIndex();
        if (indice < 0) {
            exibirMensagem("Selecione um prato na lista.");
            return;
        }
        int q = quantidade(spnPrato);
        if (q > 0) {
            selecionarPratoPrincipal(idsPratos.get(indice), q);
        }
    }

    private void cliqueConcluirPratos() {
        if (totalPratos == 0) {
            exibirMensagem("Selecione ao menos um prato antes de continuar.");
            return;
        }
        concluirSelecaoPratos();
    }

    private void cliqueAdicionarAcompanhamento() {
        int indice = listaAcompanhamentos.getSelectedIndex();
        if (indice < 0) {
            exibirMensagem("Selecione um acompanhamento na lista.");
            return;
        }
        int q = quantidade(spnAcompanhamento);
        if (q > 0) {
            selecionarAcompanhamento(idsAcompanhamentos.get(indice), q);
        }
    }

    private void cliqueAdicionarSobremesa() {
        int indice = listaSobremesas.getSelectedIndex();
        if (indice < 0) {
            exibirMensagem("Selecione uma sobremesa na lista.");
            return;
        }
        int q = quantidade(spnSobremesa);
        if (q > 0) {
            selecionarSobremesa(idsSobremesas.get(indice), q);
        }
    }

    private static final String LETRAS = "[\\p{L}][\\p{L} .'-]*";

    private void cliqueEndereco() {
        String cepDigitos = txtCep.getText().replaceAll("\\D", "");
        String logradouroDigitado = txtLogradouro.getText().trim().replaceAll("\\s+", " ");
        String numeroDigitado = txtNumero.getText().trim().toUpperCase();
        String complementoDigitado = txtComplemento.getText().trim().replaceAll("\\s+", " ");
        String bairroDigitado = txtBairro.getText().trim().replaceAll("\\s+", " ");
        String cidadeDigitada = txtCidade.getText().trim().replaceAll("\\s+", " ");

        String erro = null;
        if (!cepDigitos.matches("\\d{8}") || cepDigitos.matches("(\\d)\\1{7}")) {
            erro = "CEP inválido: informe os 8 dígitos, por exemplo 01310-100.";
        } else if (!logradouroDigitado.matches("[\\p{L}\\d][\\p{L}\\d .,'ºª-]{4,59}")
                || !logradouroDigitado.matches(".*\\p{L}{2,}.*") || !palavrasPlausiveis(logradouroDigitado)) {
            erro = "Rua inválida: informe o nome da rua, por exemplo Av. Paulista.";
        } else if (!numeroDigitado.matches("\\d{1,6}[A-Z]?|S/N")) {
            erro = "Número inválido: use até 6 dígitos (ex.: 1000 ou 12A) ou S/N.";
        } else if (!complementoDigitado.isEmpty() && !palavrasPlausiveis(complementoDigitado)) {
            erro = "Complemento inválido: por exemplo ap 12, bloco B.";
        } else if (!bairroDigitado.matches(LETRAS) || bairroDigitado.length() < 3
                || !palavrasPlausiveis(bairroDigitado)) {
            erro = "Bairro inválido: confira o nome, por exemplo Bela Vista.";
        } else if (!cidadeDigitada.matches(LETRAS) || cidadeDigitada.length() < 3
                || !palavrasPlausiveis(cidadeDigitada)) {
            erro = "Cidade inválida: escreva o nome completo, por exemplo São Paulo (não só a sigla).";
        }
        if (erro != null) {
            exibirMensagem(erro);
            return;
        }
        String cepFormatado = cepDigitos.substring(0, 5) + "-" + cepDigitos.substring(5);
        informarEnderecoEntrega(cepFormatado, logradouroDigitado, numeroDigitado,
                complementoDigitado, bairroDigitado, cidadeDigitada);
    }

    private void cliquePagamento() {
        String titular = txtTitular.getText().trim().replaceAll("\\s+", " ");
        String numeroCartao = txtNumeroCartao.getText().replaceAll("\\D", "");
        String validade = txtValidade.getText().trim();
        String cvv = new String(txtCvv.getPassword()).trim();

        String erro = null;
        if (!titularValido(titular)) {
            erro = "Nome do titular inválido: escreva nome e sobrenome como estão no cartão "
                    + "(ex.: EDUARDO K IKEMATU).";
        } else if (!numeroCartao.matches("\\d{16}")) {
            erro = "Número do cartão inválido: digite os 16 números do cartão.";
        } else if (!validadeValida(validade)) {
            erro = "Validade inválida: use MM/AA de um mês que ainda não passou.";
        } else if (!cvv.matches("\\d{3}")) {
            erro = "CVV inválido: são 3 dígitos no verso do cartão.";
        }
        if (erro != null) {
            exibirMensagem(erro);
            return;
        }
        confirmarPagamento(titular, numeroCartao, validade, cvv);
        txtCvv.setText("");
    }

    /**
     * Nome como impresso no cartão: ao menos duas palavras, só letras, e cada palavra
     * plausível (tem vogal e não repete a mesma letra 3 vezes). Uma palavra de uma letra
     * só vale como abreviação do meio (EDUARDO K IKEMATU).
     */
    private boolean titularValido(String titular) {
        String[] palavras = titular.split(" ");
        if (palavras.length < 2 || palavras[0].length() < 2
                || palavras[palavras.length - 1].length() < 2) {
            return false;
        }
        return titular.matches(LETRAS) && palavrasPlausiveis(titular);
    }

    /**
     * Confere se cada palavra parece uma palavra de verdade: tem vogal (ou é número,
     * abreviação com ponto, como "Av.", ou algarismo romano) e não repete a mesma letra
     * 3 vezes seguidas.
     * Não garante que o nome existe; isso exigiria uma base de endereços (Correios).
     */
    private boolean palavrasPlausiveis(String texto) {
        for (String palavra : texto.split("[ ,/-]+")) {
            String p = palavra.toLowerCase();
            // números (12, 3B), abreviações (Av.), letras soltas e algarismos romanos (XV)
            if (p.isEmpty() || p.matches("[\\dºª]+[a-z]?") || p.endsWith(".") || p.length() == 1
                    || p.matches("[ivxlcdm]+")) {
                continue;
            }
            String semAcento = java.text.Normalizer.normalize(p, java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            if (!semAcento.matches(".*[aeiouy].*") || semAcento.matches(".*(\\p{L})\\1\\1.*")) {
                return false;
            }
        }
        return true;
    }

    /** MM/AA com mês de 01 a 12, sem estar vencido e até 20 anos à frente. */
    private boolean validadeValida(String validade) {
        if (!validade.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }
        YearMonth mesValidade = YearMonth.of(2000 + Integer.parseInt(validade.substring(3)),
                Integer.parseInt(validade.substring(0, 2)));
        YearMonth atual = YearMonth.now();
        return !mesValidade.isBefore(atual) && !mesValidade.isAfter(atual.plusYears(20));
    }

    // =====================================================================================
    // Auxiliares de interface
    // =====================================================================================

    /** Lê o número digitado no campo de quantidade mesmo que o usuário não tenha saído dele. */
    private int quantidade(JSpinner campo) {
        try {
            campo.commitEdit();
        } catch (java.text.ParseException e) {
            Object maximo = ((SpinnerNumberModel) campo.getModel()).getMaximum();
            exibirMensagem("Quantidade inválida: escolha de 1 a " + maximo + " por vez.");
            ((JSpinner.DefaultEditor) campo.getEditor()).getTextField().setValue(campo.getValue());
            return -1;
        }
        return (Integer) campo.getValue();
    }



    private void mostrar(String pagina) {
        cartas.show(painelCartas, pagina);
    }

    private void reiniciarEstado() {
        totalPratos = 0;
        totalAcompanhamentos = 0;
        totalSobremesas = 0;
        etapaAtual = "";
        txtCodigo.setText("");
        btnValidarCodigo.setEnabled(true);
        chkSemRestricoes.setSelected(false);
        cliqueSemRestricoes();
        chkVegetariano.setSelected(false);
        chkSemGluten.setSelected(false);
        chkSemLactose.setSelected(false);
        chkLowCarb.setSelected(false);
        txtTitular.setText("");
        txtNumeroCartao.setText("");
        txtValidade.setText("");
        txtCvv.setText("");
        btnDesistir.setVisible(false);
    }

    private String moeda(double valor) {
        return "R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    private JPanel pagina(String titulo, JComponent conteudo, JButton... botoes) {
        JPanel pagina = new JPanel(new BorderLayout(10, 10));
        pagina.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 20f));
        pagina.add(lblTitulo, BorderLayout.NORTH);
        pagina.add(conteudo, BorderLayout.CENTER);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton botao : botoes) {
            rodape.add(botao);
        }
        pagina.add(rodape, BorderLayout.SOUTH);
        return pagina;
    }

    private JPanel formulario(JComponent... linhas) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        for (JComponent linha : linhas) {
            painel.add(linha);
        }
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(painel, BorderLayout.NORTH);
        return envoltorio;
    }

    private JPanel linha(String rotulo, JComponent campo) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Largura mínima de 150 px para alinhar os campos, mas nunca menor que o texto.
        // O tamanho é recalculado sempre que a janela é desenhada (fonte e escala do
        // Windows já aplicadas), para o rótulo não aparecer cortado com "...".
        JLabel lbl = new JLabel(rotulo) {
            @Override
            public java.awt.Dimension getPreferredSize() {
                java.awt.Dimension d = super.getPreferredSize();
                return new java.awt.Dimension(Math.max(150, d.width + 16), Math.max(24, d.height));
            }
        };
        painel.add(lbl);
        painel.add(campo);
        return painel;
    }

    private JPanel painelSelecao(JList<String> lista, JSpinner quantidade, JLabel contador) {
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel base = new JPanel(new FlowLayout(FlowLayout.LEFT));
        base.add(new JLabel("Quantidade:"));
        base.add(quantidade);
        base.add(contador);
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.add(new JScrollPane(lista), BorderLayout.CENTER);
        painel.add(base, BorderLayout.SOUTH);
        return painel;
    }
    // =====================================================================================
    // Filtros de digitação (Swing DocumentFilter)
    // =====================================================================================

    private void mascara(JTextField campo, String modelo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new Mascara(modelo));
    }

    private void limitarTexto(JTextField campo, int maximo, String caractereAceito,
                              boolean maiusculas) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(
                new LimiteTexto(maximo, caractereAceito, maiusculas));
    }

    /**
     * Aceita só dígitos, no máximo a quantidade de '#' do modelo, e escreve no formato do
     * modelo (ex.: "(##) #####-####"). Apagar um separador apaga o dígito antes dele.
     */
    private static class Mascara extends DocumentFilter {
        private final String modelo;
        private final int maximo;

        Mascara(String modelo) {
            this.modelo = modelo;
            this.maximo = modelo.replaceAll("[^#]", "").length();
        }

        @Override
        public void insertString(FilterBypass fb, int pos, String texto, AttributeSet a)
                throws BadLocationException {
            replace(fb, pos, 0, texto, a);
        }

        @Override
        public void remove(FilterBypass fb, int pos, int tamanho) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String depois = atual.substring(0, pos) + atual.substring(pos + tamanho);
            String digitos = depois.replaceAll("\\D", "");
            if (tamanho > 0 && digitos.equals(atual.replaceAll("\\D", ""))) {
                // só um separador foi apagado: apaga o dígito anterior
                int antes = atual.substring(0, pos).replaceAll("\\D", "").length();
                if (antes > 0) {
                    digitos = digitos.substring(0, antes - 1) + digitos.substring(antes);
                }
            }
            fb.replace(0, atual.length(), formatar(digitos), null);
        }

        @Override
        public void replace(FilterBypass fb, int pos, int tamanho, String texto, AttributeSet a)
                throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String novo = atual.substring(0, pos) + (texto == null ? "" : texto)
                    + atual.substring(pos + tamanho);
            String digitos = novo.replaceAll("\\D", "");
            if (digitos.length() > maximo) {
                digitos = digitos.substring(0, maximo);
            }
            fb.replace(0, atual.length(), formatar(digitos), a);
        }

        private String formatar(String digitos) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (char c : modelo.toCharArray()) {
                if (i >= digitos.length()) {
                    break;
                }
                if (c == '#') {
                    sb.append(digitos.charAt(i++));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
    }

    /** Limita o tamanho do campo e descarta caracteres que não fazem sentido nele. */
    private static class LimiteTexto extends DocumentFilter {
        private final int maximo;
        private final String caractereAceito;
        private final boolean maiusculas;

        LimiteTexto(int maximo, String caractereAceito, boolean maiusculas) {
            this.maximo = maximo;
            this.caractereAceito = caractereAceito;
            this.maiusculas = maiusculas;
        }

        @Override
        public void insertString(FilterBypass fb, int pos, String texto, AttributeSet a)
                throws BadLocationException {
            replace(fb, pos, 0, texto, a);
        }

        @Override
        public void replace(FilterBypass fb, int pos, int tamanho, String texto, AttributeSet a)
                throws BadLocationException {
            StringBuilder aceito = new StringBuilder();
            if (texto != null) {
                for (char c : texto.toCharArray()) {
                    if (String.valueOf(c).matches(caractereAceito)) {
                        aceito.append(maiusculas ? Character.toUpperCase(c) : c);
                    }
                }
            }
            int espaco = maximo - (fb.getDocument().getLength() - tamanho);
            if (aceito.length() > espaco) {
                aceito.setLength(Math.max(0, espaco));
            }
            fb.replace(pos, tamanho, aceito.toString(), a);
        }
    }
}
