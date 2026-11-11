package minharefeicao.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import minharefeicao.entidade.Acompanhamento;
import minharefeicao.entidade.Cardapio;
import minharefeicao.entidade.PratoPrincipal;
import minharefeicao.entidade.PreferenciaAlimentar;
import minharefeicao.entidade.Sobremesa;

/**
 * DAO do cardápio. Lê dados/cardapio.csv (somente leitura).
 * Colunas: tipo;id;nome;descricao;calorias;disponivel;preferenciasCompativeis
 * (preferências separadas por "|"; só os pratos usam essa coluna).
 */
public class CardapioDAO {

    private String caminho;

    public CardapioDAO(String caminho) {
        this.caminho = caminho;
    }

    public Cardapio carregar() {
        Cardapio cardapio = new Cardapio();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminho), StandardCharsets.UTF_8);
            for (int i = 1; i < linhas.size(); i++) { // linha 0 é o cabeçalho
                String linha = linhas.get(i).trim();
                if (linha.isEmpty()) {
                    continue;
                }
                String[] c = linha.split(";", -1);
                String tipo = c[0];
                int id = Integer.parseInt(c[1]);
                int calorias = Integer.parseInt(c[4]);
                boolean disponivel = Boolean.parseBoolean(c[5]);

                if (tipo.equals("PRATO")) {
                    PratoPrincipal prato = new PratoPrincipal(id, c[2], c[3], calorias, disponivel);
                    if (!c[6].isEmpty()) {
                        for (String nome : c[6].split("\\|")) {
                            prato.adicionarPreferenciaCompativel(new PreferenciaAlimentar(nome));
                        }
                    }
                    cardapio.adicionarItem(prato);
                } else if (tipo.equals("ACOMPANHAMENTO")) {
                    cardapio.adicionarItem(new Acompanhamento(id, c[2], c[3], calorias, disponivel));
                } else if (tipo.equals("SOBREMESA")) {
                    cardapio.adicionarItem(new Sobremesa(id, c[2], c[3], calorias, disponivel));
                }
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler " + caminho + ": " + e.getMessage());
        }
        return cardapio;
    }
}
