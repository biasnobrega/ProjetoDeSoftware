package minharefeicao.fronteira;

import javax.swing.JOptionPane;

/** «boundary» InterfaceSMS (simulada): mostra o código numa caixa de diálogo e no console. */
public class InterfaceSMS {

    public void enviarCodigo(String celular, String codigo) {
        System.out.println("[SMS simulado] Para " + celular + ": seu código de confirmação é " + codigo);
        JOptionPane.showMessageDialog(null,
                "SMS enviado para " + celular + "\n\nSeu código de confirmação é: " + codigo,
                "SMS (simulado)", JOptionPane.INFORMATION_MESSAGE);
    }
}
