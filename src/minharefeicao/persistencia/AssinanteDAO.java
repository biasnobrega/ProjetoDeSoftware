package minharefeicao.persistencia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import minharefeicao.entidade.Assinante;
import minharefeicao.entidade.Assinatura;
import minharefeicao.entidade.Pagamento;

/**
 * DAO dos assinantes e das assinaturas.
 * assinantes.csv: id;nome;celular (lido na inicialização, gravado em Assinantes.adicionar).
 * assinaturas.csv: idAssinatura;protocolo;celular;plano;status;data;valorTotal;endereco;
 *                  finalCartao;statusPagamento;dataPrevistaEntrega;itensDoPedido
 * O CVV nunca chega até aqui e do cartão só se grava o final.
 */
public class AssinanteDAO {

    private static final String CABECALHO_ASSINANTES = "id;nome;celular";
    private static final String CABECALHO_ASSINATURAS = "idAssinatura;protocolo;celular;plano;status;data;"
            + "valorTotal;endereco;finalCartao;statusPagamento;dataPrevistaEntrega;itensDoPedido";

    private String caminhoAssinantes;
    private String caminhoAssinaturas;

    public AssinanteDAO(String caminhoAssinantes, String caminhoAssinaturas) {
        this.caminhoAssinantes = caminhoAssinantes;
        this.caminhoAssinaturas = caminhoAssinaturas;
    }

    public List<Assinante> carregar() {
        List<Assinante> assinantes = new ArrayList<Assinante>();
        Path arquivo = Paths.get(caminhoAssinantes);
        if (!Files.exists(arquivo)) {
            return assinantes; // primeira execução: ainda não há assinantes
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (int i = 1; i < linhas.size(); i++) { // linha 0 é o cabeçalho
                String linha = linhas.get(i).trim();
                if (linha.isEmpty()) {
                    continue;
                }
                String[] c = linha.split(";", -1);
                if (c.length < 3) {
                    continue; // linha fora do formato id;nome;celular
                }
                assinantes.add(new Assinante(Integer.parseInt(c[0]), c[1], c[2]));
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler " + caminhoAssinantes + ": " + e.getMessage());
        }
        // Continua a numeração das assinaturas (protocolo único entre execuções)
        Assinatura.continuarNumeracao(lerUltimoIdAssinatura());
        return assinantes;
    }

    /** Maior idAssinatura já gravado em assinaturas.csv (0 se o arquivo ainda não existe). */
    private int lerUltimoIdAssinatura() {
        int maior = 0;
        Path arquivo = Paths.get(caminhoAssinaturas);
        if (!Files.exists(arquivo)) {
            return maior;
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (int i = 1; i < linhas.size(); i++) { // linha 0 é o cabeçalho
                String[] c = linhas.get(i).split(";", -1);
                try {
                    maior = Math.max(maior, Integer.parseInt(c[0].trim()));
                } catch (NumberFormatException e) {
                    // linha vazia ou fora do formato: ignorada
                }
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler " + caminhoAssinaturas + ": " + e.getMessage());
        }
        return maior;
    }

    public void gravarAssinante(Assinante a) {
        String linha = a.getIdAssinante() + ";" + limpar(a.getNome()) + ";" + limpar(a.getCelular());
        acrescentarLinha(caminhoAssinantes, CABECALHO_ASSINANTES, linha);
    }

    public void gravarAssinatura(Assinante a, Assinatura as) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Pagamento pag = as.getUltimoPagamento();
        String finalCartao = "";
        String statusPagamento = "";
        if (pag != null) {
            finalCartao = pag.getCartao().getFinalCartao();
            statusPagamento = pag.getStatus().name();
        }
        String endereco = "";
        if (as.getEnderecoEntrega() != null) {
            endereco = as.getEnderecoEntrega().getDescricao();
        }
        String dataPrevista = "";
        if (as.getDataPrevistaEntrega() != null) {
            dataPrevista = as.getDataPrevistaEntrega().format(fmt);
        }
        String itens = as.gerarResumoPedido().trim().replace("\n", " | ");

        String linha = as.getIdAssinatura() + ";"
                + limpar(as.getProtocolo()) + ";"
                + limpar(a.getCelular()) + ";"
                + limpar(as.getPlano().getNome()) + ";"
                + as.getStatus().name() + ";"
                + as.getDataContratacao().format(fmt) + ";"
                + String.format(java.util.Locale.US, "%.2f", as.getValorTotal()) + ";"
                + limpar(endereco) + ";"
                + finalCartao + ";"
                + statusPagamento + ";"
                + dataPrevista + ";"
                + limpar(itens);
        acrescentarLinha(caminhoAssinaturas, CABECALHO_ASSINATURAS, linha);
    }

    private void acrescentarLinha(String caminho, String cabecalho, String linha) {
        Path arquivo = Paths.get(caminho);
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            boolean novo = !Files.exists(arquivo);
            BufferedWriter escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            try {
                if (novo) {
                    escritor.write(cabecalho);
                    escritor.newLine();
                }
                escritor.write(linha);
                escritor.newLine();
            } finally {
                escritor.close();
            }
        } catch (IOException e) {
            System.out.println("Não foi possível gravar " + caminho + ": " + e.getMessage());
        }
    }

    /** Remove o separador do CSV e quebras de linha de qualquer texto gravado. */
    private String limpar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(";", ",").replace("\r", " ").replace("\n", " ");
    }
}
