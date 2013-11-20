package GUI.FileManager;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author Carlos Roberto Carvalho Oliveira
 */
public class ReportUtil {
    
    public static void exportReport(String result){
        try {            
            System.out.println("Gerando relatório...");
            
            // lista que conterá as informações do relatório.
            List<ContentReport> list = new ArrayList<ContentReport>();
            
            ContentReport content = new ContentReport();
            content.setContent(result);
                
            list.add(content);
                           
            String pathWay = "";
            JFileChooser chooser= new JFileChooser(LastpathManager.getlastpath(".pdf"));
            int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
            if(openedFile == JFileChooser.APPROVE_OPTION){
                pathWay = chooser.getSelectedFile().getAbsolutePath();
                if(!pathWay.toUpperCase().endsWith(".PDF"))
                    pathWay += ".pdf";
                
                // compilacao do JRXML
                JasperReport report = JasperCompileManager.compileReport("XChangeReport.jrxml");
                
                JasperPrint print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(list));
                
                // exportacao do relatorio para outro formato, no caso PDF
                JasperExportManager.exportReportToPdfFile(print,pathWay);
                                
                System.out.println("Relatório gerado.");
                JOptionPane.showMessageDialog(null, "Report Generated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }else{
                System.out.println("Erro, Relatorio nao foi gerado");
                JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
