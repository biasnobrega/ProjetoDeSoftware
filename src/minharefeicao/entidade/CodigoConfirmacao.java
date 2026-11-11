package minharefeicao.entidade;

import java.time.LocalDateTime;
import java.util.Random;

/** «entity» CodigoConfirmacao: código enviado por SMS, com a regra das 3 tentativas (FA1). */
public class CodigoConfirmacao {

    private static final int MAX_TENTATIVAS = 3;
    private static final int MINUTOS_VALIDADE = 10;
    private static int proximoId = 1;

    private int idCodigo;
    private String codigo;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataExpiracao;
    private int quantidadeTentativas;
    private boolean validado;

    public CodigoConfirmacao() {
        this.idCodigo = proximoId++;
        this.codigo = String.format("%06d", new Random().nextInt(1000000));
        this.dataEnvio = LocalDateTime.now();
        this.dataExpiracao = dataEnvio.plusMinutes(MINUTOS_VALIDADE);
        this.quantidadeTentativas = 0;
        this.validado = false;
    }

    public ResultadoValidacao validar(String codigo) {
        if (validado) {
            return ResultadoValidacao.VALIDO;
        }
        if (quantidadeTentativas >= MAX_TENTATIVAS) {
            return ResultadoValidacao.BLOQUEADO;
        }
        boolean expirado = LocalDateTime.now().isAfter(dataExpiracao);
        if (!expirado && this.codigo.equals(codigo)) {
            validado = true;
            return ResultadoValidacao.VALIDO;
        }
        // FA1: código errado (ou expirado) conta como tentativa; a 3ª falha bloqueia
        quantidadeTentativas++;
        if (quantidadeTentativas >= MAX_TENTATIVAS) {
            return ResultadoValidacao.BLOQUEADO;
        }
        return ResultadoValidacao.INVALIDO;
    }

    public String getCodigo() {
        return codigo;
    }
}
