package minharefeicao.entidade;

import java.util.ArrayList;
import java.util.List;

/** «entity» PratoPrincipal: item do cardápio com preferências alimentares compatíveis. */
public class PratoPrincipal extends ItemCardapio {

    // PratoPrincipal -> PreferenciaAlimentar (0..*): "compatível com"
    private List<PreferenciaAlimentar> compativelCom = new ArrayList<PreferenciaAlimentar>();

    public PratoPrincipal(int idItemCardapio, String nome, String descricao, int calorias,
                          boolean disponivel) {
        super(idItemCardapio, nome, descricao, calorias, disponivel);
    }

    /** Acréscimo (fora da UML): o CardapioDAO registra as preferências lidas do CSV. */
    public void adicionarPreferenciaCompativel(PreferenciaAlimentar pref) {
        compativelCom.add(pref);
    }

    /** O prato atende quando é compatível com todas as preferências informadas. */
    public boolean atendePreferencias(List<PreferenciaAlimentar> prefs) {
        for (PreferenciaAlimentar desejada : prefs) {
            boolean encontrou = false;
            for (PreferenciaAlimentar aceita : compativelCom) {
                if (aceita.getNome().equalsIgnoreCase(desejada.getNome())) {
                    encontrou = true;
                }
            }
            if (!encontrou) {
                return false;
            }
        }
        return true;
    }
}
