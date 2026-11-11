package minharefeicao.colecao;

import java.util.List;

import minharefeicao.entidade.Assinante;
import minharefeicao.entidade.Assinatura;
import minharefeicao.persistencia.AssinanteDAO;

/** «collection» Assinantes: agregação Assinantes ◇ Assinante (busca por celular). */
public class Assinantes {

    // Agregação: os Assinante são criados fora (DAO ou controlador) e só guardados aqui
    private List<Assinante> assinantes;

    // Acréscimo (fora da UML): o DAO grava no CSV quando a coleção muda
    private AssinanteDAO dao;

    public Assinantes(AssinanteDAO dao) {
        this.dao = dao;
        this.assinantes = dao.carregar();
    }

    public Assinante buscarPorCelular(String celular) {
        for (Assinante a : assinantes) {
            if (a.getCelular().equals(celular)) {
                return a;
            }
        }
        return null;
    }

    public void adicionar(Assinante a) {
        assinantes.add(a);
        dao.gravarAssinante(a);
    }

    /** Acréscimo (fora da UML): grava a assinatura ao fim do pagamento ou no cancelamento (FA3.6). */
    public void gravarAssinatura(Assinante a, Assinatura as) {
        dao.gravarAssinatura(a, as);
    }
}
