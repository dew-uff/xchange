package GUI.FileManager;

import Documents.Document;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcio Tadeu de Oliveira Jr
 */
public class LastpathManager {

    /**
     * Retorna o último caminho de um determinado tipo de arquivo.
     * @param type Tipo de arquivo que se deseja o último caminho
     * @return o último caminho de um determinado tipo type.
     */
    public static String getlastpath(String type) {
        try {
            File file = new File("lastpath.txt");
            file.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                String content = br.readLine();
                if (content.substring(0, content.indexOf(":")).equals(type)) {
                    return content.substring(content.indexOf(":") + 1);
                }
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Salva o último caminho de um determinado tipo de arquivo.
     * @param lastpath O último caminho de um determinado tipo
     * @param type O tipo do arquivo.
     */
    public static void savelastpath(String lastpath, String type) {
        ArrayList<String> content = new ArrayList<String>();
        try {
            File file = new File("lastpath.txt");
            file.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                content.add(br.readLine());
            }
            br.close();
            boolean typeExistance = false;
            BufferedWriter out = new BufferedWriter(new FileWriter("lastpath.txt"));
            for (String line : content) {
                if (line.substring(0, line.indexOf(":")).equals(type)) {
                    out.write(type + ":" + lastpath);
                    typeExistance = true;
                } else {
                    out.write(line);
                }
                out.newLine();
            }
            if (!typeExistance) {
                out.write(type + ":" + lastpath);
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
