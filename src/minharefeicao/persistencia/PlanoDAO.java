package minharefeicao.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import minharefeicao.entidade.PlanoAssinatura;

/**
 * DAO dos planos. Lê dados/planos.csv (somente leitura).
 * Colunas: id;nome;quantidadeRefeicoes;periodicidadeEntrega;valorMensal;ativo
 */
public class PlanoDAO {

    private String caminho;

    public PlanoDAO(String caminho) {
        this.caminho = caminho;
    }

    public List<PlanoAssinatura> carregar() {
        List<PlanoAssinatura> planos = new ArrayList<PlanoAssinatura>();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminho), StandardCharsets.UTF_8);
            for (int i = 1; i < linhas.size(); i++) { // linha 0 é o cabeçalho
                String linha = linhas.get(i).trim();
                if (linha.isEmpty()) {
                    continue;
                }
                String[] c = linha.split(";", -1);
                planos.add(new PlanoAssinatura(
                        Integer.parseInt(c[0]),
                        c[1],
                        Integer.parseInt(c[2]),
                        c[3],
                        Double.parseDouble(c[4]),
                        Boolean.parseBoolean(c[5])));
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler " + caminho + ": " + e.getMessage());
        }
        return planos;
    }
}
