package minharefeicao.entidade;

import java.time.LocalDate;

/** «entity» Entrega: criada pelo Pedido quando ele é aprovado. */
public class Entrega {

    private static final int DIAS_ATE_PRIMEIRA_ENTREGA = 3;
    private static int proximoId = 1;

    private int idEntrega;
    private LocalDate dataPrevista;
    private LocalDate dataRealizada;
    private StatusEntrega status;

    // Entrega -> EnderecoEntrega: associação navegável
    private EnderecoEntrega endereco;

    public Entrega(EnderecoEntrega end) {
        this.idEntrega = proximoId++;
        this.endereco = end;
        this.status = StatusEntrega.AGENDADA;
        this.dataPrevista = calcularDataPrevista();
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    private LocalDate calcularDataPrevista() {
        return LocalDate.now().plusDays(DIAS_ATE_PRIMEIRA_ENTREGA);
    }
}
