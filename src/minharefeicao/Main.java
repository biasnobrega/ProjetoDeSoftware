package minharefeicao;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import minharefeicao.colecao.Assinantes;
import minharefeicao.colecao.PlanosAssinatura;
import minharefeicao.controle.ControladorAssinatura;
import minharefeicao.entidade.Cardapio;
import minharefeicao.fronteira.InterfaceOperadoraCartao;
import minharefeicao.fronteira.InterfaceSMS;
import minharefeicao.fronteira.TelaAssinatura;
import minharefeicao.persistencia.AssinanteDAO;
import minharefeicao.persistencia.CardapioDAO;
import minharefeicao.persistencia.PlanoDAO;

/**
 * Cria os objetos que já existem no início do diagrama de sequência (pré-condição: planos
 * disponíveis e cardápio atualizado) e abre a tela. Execute a partir da pasta que contém dados/ e src/.
 */
public class Main {

    private static final String PASTA_DADOS = "dados";

    public static void main(String[] args) {
        if (!new File(PASTA_DADOS, "planos.csv").exists()) {
            JOptionPane.showMessageDialog(null,
                    "Pasta 'dados' não encontrada. Execute a partir da pasta que contém dados/ e src/.",
                    "Minha Refeição", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Persistência: carrega os CSV
        PlanoDAO planoDAO = new PlanoDAO(PASTA_DADOS + "/planos.csv");
        CardapioDAO cardapioDAO = new CardapioDAO(PASTA_DADOS + "/cardapio.csv");
        AssinanteDAO assinanteDAO = new AssinanteDAO(PASTA_DADOS + "/assinantes.csv",
                PASTA_DADOS + "/assinaturas.csv");

        // Entidades e coleções que já existem no início da interação
        PlanosAssinatura planos = new PlanosAssinatura(planoDAO.carregar());
        Cardapio cardapio = cardapioDAO.carregar();
        Assinantes assinantes = new Assinantes(assinanteDAO);

        // Fronteiras externas e controlador
        InterfaceSMS interfaceSMS = new InterfaceSMS();
        InterfaceOperadoraCartao interfaceOperadoraCartao = new InterfaceOperadoraCartao();
        ControladorAssinatura controlador = new ControladorAssinatura(assinantes, planos, cardapio,
                interfaceSMS, interfaceOperadoraCartao);

        // Fronteira do Assinante
        SwingUtilities.invokeLater(() -> new TelaAssinatura(controlador).setVisible(true));
    }
}
