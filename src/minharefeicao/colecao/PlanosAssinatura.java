package minharefeicao.colecao;

import java.util.ArrayList;
import java.util.List;

import minharefeicao.entidade.PlanoAssinatura;

/** «collection» PlanosAssinatura: agregação PlanosAssinatura ◇ PlanoAssinatura. */
public class PlanosAssinatura {

    // Agregação: os planos são criados fora (PlanoDAO) e recebidos pelo construtor
    private List<PlanoAssinatura> planos;

    public PlanosAssinatura(List<PlanoAssinatura> planos) {
        this.planos = planos;
    }

    public List<PlanoAssinatura> listarPlanosAtivos() {
        List<PlanoAssinatura> ativos = new ArrayList<PlanoAssinatura>();
        for (PlanoAssinatura p : planos) {
            if (p.isAtivo()) {
                ativos.add(p);
            }
        }
        return ativos;
    }

    public PlanoAssinatura buscarPlano(int idPlano) {
        for (PlanoAssinatura p : planos) {
            if (p.getIdPlano() == idPlano) {
                return p;
            }
        }
        return null;
    }
}
