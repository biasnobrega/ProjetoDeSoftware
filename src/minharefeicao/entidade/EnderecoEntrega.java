package minharefeicao.entidade;

/** «entity» EnderecoEntrega. */
public class EnderecoEntrega {

    private static int proximoId = 1;

    private int idEndereco;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    public EnderecoEntrega(String cep, String logradouro, String numero, String complemento,
                           String bairro, String cidade) {
        this.idEndereco = proximoId++;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
    }

    public String getDescricao() {
        String texto = logradouro + ", " + numero;
        if (complemento != null && !complemento.isEmpty()) {
            texto += " (" + complemento + ")";
        }
        return texto + " - " + bairro + ", " + cidade + " - CEP " + cep;
    }
}
