package GUI.Util;

/**
 *
 * @author Marcio Tadeu de Oliveira Jr
 * 
 * Classe para obter e modificar a barra de progresso corrente em qualquer ponto do programa.
 */
public class ProgressHandler {

    private static ProgressBar pBar;
    private static boolean inUse;

    /**
     * Cria uma nova janela com barra de progresso.
     * @param m Número de passos máximo que a barra fará
     * @param title Título da janela
     */
    public static ProgressBar makeNew(int m, String title) {
        pBar = new ProgressBar(m, title);
        inUse=true;
        return pBar;
    }
    
    /**
     * Cria uma nova janela com barra de progresso.
     * @param m Número de passos máximo que a barra fará
     * @param title Título da janela
     */
    public static void restart(int m, String title) {
        pBar.restart(m, title);
        inUse=true;
    }

    /**
     * Incrementa o contador da barra de progresso.
     */
    public static void increase(){
        if(pBar!=null && isInUse())pBar.increase();
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
    public static void stop() {
        inUse=false;
        pBar.stop();
    }
    
    /**
     * Verifica se a barra já está sendo usada por outro processo.
     * @return se a barra está em uso.
     */
    public static boolean isInUse(){
        return inUse;
    }
    
    /**
     * Alterna o estado de uso.
     * @param status Estado atual de uso.
     */
    public static void setUse(boolean status){
        inUse=status;
    }
    
    /**
     * Retorna a descrição do evento atual. 
     * @return a descrição do evento atual. 
     */
    public static String getLabel(){
        return pBar.getLabel();
    }
    
    public static int getValue(){
        return pBar.getValue();
    }
}
