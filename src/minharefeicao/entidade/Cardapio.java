package minharefeicao.entidade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** «entity» Cardapio: agrega os itens do cardápio (agregação Cardapio ◇ ItemCardapio). */
public class Cardapio {

    private int idCardapio;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean ativo;

    // Agregação: os itens são criados fora (CardapioDAO) e só guardados aqui
    private List<ItemCardapio> itens = new ArrayList<ItemCardapio>();

    public Cardapio() {
        this.idCardapio = 1;
        this.dataInicio = LocalDate.now();
        this.dataFim = LocalDate.now().plusMonths(1);
        this.ativo = true;
    }

    /** Acréscimo (fora da UML): recebe os itens criados pelo CardapioDAO. */
    public void adicionarItem(ItemCardapio item) {
        itens.add(item);
    }

    public List<PratoPrincipal> listarPratosDisponiveis(List<PreferenciaAlimentar> prefs) {
        List<PratoPrincipal> pratos = new ArrayList<PratoPrincipal>();
        for (ItemCardapio item : itens) {
            if (item instanceof PratoPrincipal) {
                PratoPrincipal prato = (PratoPrincipal) item;
                // Passo 10: só pratos disponíveis e compatíveis com as preferências
                if (prato.isDisponivel() && prato.atendePreferencias(prefs)) {
                    pratos.add(prato);
                }
            }
        }
        return pratos;
    }

    public List<Acompanhamento> listarAcompanhamentos() {
        List<Acompanhamento> acompanhamentos = new ArrayList<Acompanhamento>();
        for (ItemCardapio item : itens) {
            if (item instanceof Acompanhamento && item.isDisponivel()) {
                acompanhamentos.add((Acompanhamento) item);
            }
        }
        return acompanhamentos;
    }

    public List<Sobremesa> listarSobremesas() {
        List<Sobremesa> sobremesas = new ArrayList<Sobremesa>();
        for (ItemCardapio item : itens) {
            if (item instanceof Sobremesa && item.isDisponivel()) {
                sobremesas.add((Sobremesa) item);
            }
        }
        return sobremesas;
    }

    public PratoPrincipal buscarPrato(int idPrato) {
        for (ItemCardapio item : itens) {
            if (item instanceof PratoPrincipal && item.getIdItemCardapio() == idPrato) {
                return (PratoPrincipal) item;
            }
        }
        return null;
    }

    public Acompanhamento buscarAcompanhamento(int idAcompanhamento) {
        for (ItemCardapio item : itens) {
            if (item instanceof Acompanhamento && item.getIdItemCardapio() == idAcompanhamento) {
                return (Acompanhamento) item;
            }
        }
        return null;
    }

    public Sobremesa buscarSobremesa(int idSobremesa) {
        for (ItemCardapio item : itens) {
            if (item instanceof Sobremesa && item.getIdItemCardapio() == idSobremesa) {
                return (Sobremesa) item;
            }
        }
        return null;
    }
}
