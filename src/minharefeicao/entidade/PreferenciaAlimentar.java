package minharefeicao.entidade;

/** «entity» PreferenciaAlimentar. */
public class PreferenciaAlimentar {

    private static int proximoId = 1;

    private int idPreferencia;
    private String nome;
    private String descricao;

    public PreferenciaAlimentar(String nome) {
        this.idPreferencia = proximoId++;
        this.nome = nome;
        this.descricao = "";
    }

    public String getNome() {
        return nome;
    }
}
