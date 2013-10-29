package GUI.Layout;

import java.awt.GridBagConstraints;

public class LayoutConstraints{
    
    /**
     * Define os valores de layout para um componente inserido num GridBagLayout
     * @param gb GridBagLayout que contêm o componente
     * @param gridx Coluna onde o componente será inserido
     * @param gridy Linha onde o componente será inserido
     * @param gridwidth Quantidade de colunas que o componente ocupará
     * @param gridheight Quantidade de linhas que o componente ocupará
     * @param weightx Proporção que as colunas ocupadas pelo componente terão
     * @param weighty Proporção que as linhas ocupadas pelo componente terão
     */
    public static void setConstraints(GridBagConstraints gb, int gridx, int gridy,int gridwidth, int gridheight,int weightx, int weighty){
        //horizontal cordinate on grid
        //escolhe em qual coluna o componente ficará
        gb.gridx = gridx;
        //vertical cordinate on grid
        //escolhe em qual linha o componente ficará
        gb.gridy = gridy;
        //how many columns the component will occupy
        //escolhe quantas colunas o comopnente ocupará
        gb.gridwidth = gridwidth;
        //how many rows the component will occupy
        //escolhe quantas linhas o comopnente ocupará
        gb.gridheight = gridheight;
        //sets the proportion occupied in the column
        //escolhe a porcentagem que o componente ocupará em sua coluna
        gb.weightx = weightx; 
        //sets the proportion occupied in the row
        //escolhe a porcentagem que o componente ocupará em sua linha
        gb.weighty = weighty; 
    }

}