package GUI.Util;

/**
 *
 * @author Marcio Tadeu de Oliveira Jr
 * 
 * Classe para obter e modificar a barra de progresso corrente em qualquer ponto do programa.
 */
public class ProgressHandler {

    private static ProgressBar pBar;

    /**
     * Cria uma nova janela com barra de progresso.
     * @param m Número de passos máximo que a barra fará
     * @param title Título da janela
     */
    public static void makeNew(int m, String title) {
        pBar = new ProgressBar(m, title);
    }

    /**
     * Incrementa o contador da barra de progresso.
     */
    public static void increase() {
        if(pBar!=null)pBar.increase();
    }

    /**
     * Muda o valor da legenda.
     * @param label Novo valor da legenda.
     */
    public static void setLabel(String label) {
        if(pBar!=null)pBar.setLabel(label);
    }

    /**
     * Desaloca o espaço ocupado pela barra de progresso na memória
     */
    public static void dispose() {
        if(pBar!=null)pBar.dispose();
        pBar = null;
    }
}
