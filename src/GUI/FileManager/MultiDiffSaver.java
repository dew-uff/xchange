package GUI.FileManager;

import Exception.NoSelectedFileException;
import Documents.Documents;
import Documents.XMLFormatter;
import Phoenix.PhoenixWrapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcio Tadeu de Oliveira Junior Cria um objeto para salvar um projeto
 * .XCP
 */
public abstract class MultiDiffSaver {

    /**
     * Recebe um conjunto de documentos e os salva os diff's na ordem linear em
     * uma pasta externa determinado pelo usuário.
     *
     * @param documents(conjunto de documentos a ser salvo)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static void save(Documents documents) throws NoSelectedFileException {
        JOptionPane.showMessageDialog(null, "You may save each combination of diff's with a name you choose\n"
                + "or you may use tags to generate the name of each combination automaticaly.\n"
                + "The tags are:\n\n"
                + "%name_before% %name_after% - File name\n"
                + "%id_before% %id_after% - Order number of the file\n"
                + "%last_update_before% %last_update_after% - Last update of the file\n"
                + "%now% - Current date and time\n"
                + "%today% - Current date\n\n"
                + "Pay attention: The tag %today% does not work alone, you must use another tag with it.", "About Tags", JOptionPane.INFORMATION_MESSAGE);
        int openedFile = 0;
        JFileChooser chooser = null;
        File file = null;
        String taggedName = "";
        boolean hasTag = false;
        String pathWay = "";
        for (int i = 1; i < documents.getSize(); i++) {
            if (!hasTag) {
                chooser = new JFileChooser(LastpathManager.getlastpath("singlediff") + ".xml");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
                openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
                pathWay = chooser.getSelectedFile().getAbsolutePath();  // o getSelectedFile pega o arquivo e o getAbsolutePath retorna uma string contendo o endereço.
                taggedName = pathWay;
                hasTag = hasTag(pathWay);
                if (hasTag) {
                    pathWay = replaceTags(taggedName, documents, i);
                }
            } else {
                pathWay = replaceTags(taggedName, documents, i);
            }
            if (openedFile == JFileChooser.APPROVE_OPTION || hasTag) {
                LastpathManager.savelastpath(pathWay, "singlediff");
                //salva o arquivo no local selecionado
                String name = pathWay;
                if (!name.toUpperCase().endsWith(".XML")) {
                    name += ".xml";
                }
                file = new File(name); // o +".txt" é para ele salvar como txt . Para outro tipo de arquivo, mude a extensao final. se você nao mudar a extensao, ele vai criar como ".bin"
                try {
                    String lcsxml = PhoenixWrapper.doSimilarity(documents.getContent(i-1), documents.getContent(i));
                    String[] lines = XMLFormatter.format(lcsxml).split("\n");
                    BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                    for (int j = 0; j < lines.length; j++) {
                        out.write(lines[j]);
                        out.newLine();
                    }
                    out.newLine();

                    out.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
                }
            } else {
                throw new NoSelectedFileException();
            }
        }
    }

    private static boolean hasTag(String s) {
        if (s.contains("%name_before%") || s.contains("%name_after%")) {
            return true;
        }
        if (s.contains("%id_before%") || s.contains("%id_after%")) {
            return true;
        }
        if (s.contains("%last_update_before%") || s.contains("%last_update_after%")) {
            return true;
        }
        if (s.contains("%now%")) {
            return true;
        }
        return false;
    }

    private static String replaceTags(String s, Documents doc, int i) {
        s = s.replaceAll("%name_before%", doc.getDocument(i).getFile().getName().substring(0, doc.getDocument(i).getFile().getName().lastIndexOf(".")));
        s = s.replaceAll("%name_after%", doc.getDocument(i + 1).getFile().getName().substring(0, doc.getDocument(i + 1).getFile().getName().lastIndexOf(".")));
        s = s.replaceAll("%id_before%", Integer.toString(i));
        s = s.replaceAll("%id_after%", Integer.toString(i + 1));
        Date date = new Date(doc.getDocument(i).getFile().lastModified());
        s = s.replaceAll("%last_update_before%", date.toString());
        date = new Date(doc.getDocument(i + 1).getFile().lastModified());
        s = s.replaceAll("%last_update_before%", date.toString());
        date = new Date(System.currentTimeMillis());
        s = s.replaceAll("%now%", date.toString());
        DateFormat formatter;
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        String today = formatter.format(date);
        s = s.replaceAll("%today%", today);
        return s;
    }
}
