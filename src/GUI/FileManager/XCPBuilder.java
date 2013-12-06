package GUI.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Classe para criar um arquivo compactado com as informações necssárias ao projeto.
 * @author Marcio Tadeu de Oliveira Júnior
 * @author Carlos Roberto Carvalho Oliveira
 */
public class XCPBuilder {

    List<String> fileList;
    private static String OUTPUT_ZIP_FILE;
    private static String SOURCE_FOLDER; //SourceFolder path

    /**
     * Cria o arquivo de projeto
     * @param pathWay pasta com os arquivos importantes ao projeto.
     */
    XCPBuilder(String pathWay) {
        fileList = new ArrayList<String>();
        SOURCE_FOLDER=pathWay;
        OUTPUT_ZIP_FILE=pathWay+".XCP";
        this.generateFileList(new File(SOURCE_FOLDER));
        this.zipIt(OUTPUT_ZIP_FILE);
    }


    /**
     * Salva os arquivos da pasta a ser compactada em uma arquivo zip.
     * @param zipFile arquivo zip
     */
    public void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = "";
        try {
            try {
                source = SOURCE_FOLDER.substring(SOURCE_FOLDER.lastIndexOf(System.getProperty("file.separator")) + 1, SOURCE_FOLDER.length());
            } catch (Exception e) {
                source = SOURCE_FOLDER;
            }
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            
            for (String file : this.fileList) {

                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in
                        = new FileInputStream(SOURCE_FOLDER + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Folder successfully compressed");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gera lista de arquivos e diretórios dentro de uma diretório node.
     * @param node diretório a ser examinado.
     */
    public void generateFileList(File node) {

        //add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));

        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }

    }

    /**
     * Gera o caminho para se salvar dentro do arquivo zip.
     * @param file arquivo a ser salvo dentro do zip.
     * @return caminho relativo dentro do zip.
     */
    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
}
