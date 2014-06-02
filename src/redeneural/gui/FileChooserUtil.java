package redeneural.gui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Michael Murussi
 */
public final class FileChooserUtil {

    public static final FileNameExtensionFilter FILE_FILTER_XLS = new FileNameExtensionFilter("Planilhas do Excel (*.xls)", "xls");
    public static final FileNameExtensionFilter FILE_FILTER_XLSX = new FileNameExtensionFilter("Planilhas do Excel (*.xlsx)", "xlsx");
    public static final FileNameExtensionFilter FILE_FILTER_EXCEL = new FileNameExtensionFilter("Planilhas do Excel (*.xls; *.xlsx)", "xls", "xlsx");
    public static final FileNameExtensionFilter FILE_FILTER_CSV = new FileNameExtensionFilter("Arquivos CSV (*.csv; *.txt)", "csv", "txt");
    public static final FileNameExtensionFilter FILE_FILTER_TXT = new FileNameExtensionFilter("Arquivos Texto (*.txt)", "txt");

    public static final FileNameExtensionFilter FILE_FILTER_PNG = new FileNameExtensionFilter("Imagens PNG (*.png)", "png");
    public static final FileNameExtensionFilter FILE_FILTER_TIFF = new FileNameExtensionFilter("Imagens TIFF (*.tif; *.tiff)", "tif", "tiff");

    public static final FileNameExtensionFilter FILE_FILTER_PROJETO = new FileNameExtensionFilter("Projetos (*.bin)", "bin");

    public static File getSelectedFileWithExtension(JFileChooser fileChooser) {
        File file = fileChooser.getSelectedFile();
        if (fileChooser.getFileFilter() instanceof FileNameExtensionFilter) {
            FileNameExtensionFilter filter = (FileNameExtensionFilter) fileChooser.getFileFilter();

            /*
             * Se arquivo já estiver com uma das extensões do filtro selecionado, retorna inalterado,
             * caso contrário, retorna arquivo com a primeira extensão do filtro
             */
            String fileName = file.getName().toLowerCase();
            for (String ext: filter.getExtensions()) {
                if (fileName.endsWith("." + ext)) {
                    return file;
                }
            }
            file = new File(file.getPath() + "." + filter.getExtensions()[0]);
            
            return file;
        } else {
            return file;
        }
    }

    public static JFileChooser getNewFileChooser() {
        return getNewFileChooser(FILE_FILTER_EXCEL, FILE_FILTER_CSV);
    }

    public static JFileChooser getNewImageFileChooser() {
        return getNewFileChooser(FILE_FILTER_TIFF, FILE_FILTER_PNG);
    }

    public static JFileChooser getNewFileChooser(FileNameExtensionFilter... extensionFilters) {

        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        for (FileNameExtensionFilter extensionFilter: extensionFilters) {
            fileChooser.addChoosableFileFilter(extensionFilter);
        }

        return fileChooser;

    }

}
