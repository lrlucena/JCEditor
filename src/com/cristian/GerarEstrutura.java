package com.cristian;

import javax.swing.JTextArea;

/**
 * Classe que gera a estrutura inicial de determinadas linguagens
 *
 * @author Cristian Henrique (cristianmsbr@gmail.com)
 * @version 1.5
 * @since Segunda atualização
 */
public class GerarEstrutura {

    private final JTextArea area;
    private final String linguagem;
    private String t;

    /**
     * O construtor recebe o JTextArea no qual será gerada a estrutura e a
     * String que contém o nome da linguagem de programação.
     */
    public GerarEstrutura(JTextArea area, String linguagem) {
        this.area = area;
        this.linguagem = linguagem;

        gerar();
    }

    /**
     * Método que verifica a linguagem e gera a estrutura correspondente.
     */
    private void gerar() {
        switch (linguagem) {
            case "Java":
                t = "public class Nome {\n\tpublic static void main (String[] args) {\n\t\t\n\t}\n}";
                break;
            case "Scala":
                t = "object Nome extends Application {\n\n}";
                break;
            case "Portugol":
                t = "algoritmo : \"Nome\"\n\nvar\n\ninicio\n\nfimalgoritmo";
                break;
            case "PHP":
                t = "<?php\n\n?>";
                break;
            default:
                break;
        }

        area.setText(t);
    }
}
