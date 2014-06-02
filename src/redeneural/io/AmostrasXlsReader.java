package redeneural.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Michael Murussi
 */
public class AmostrasXlsReader implements AmostrasReader {

    @Override
    public double[][] load(File file) throws FileNotFoundException, IOException {

        double[][] dados = null;

        try {
            Workbook workbook = WorkbookFactory.create(file);
            if (workbook.getNumberOfSheets() == 0) throw new IOException("Arquivo danificado ou formato inválido!");

            Sheet sheet = workbook.getSheetAt(0);
            int linhas = sheet.getLastRowNum() + 1;
            int colunas = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum();
            if (colunas == -1) throw new IOException("Arquivo vazio ou primeira linha vazia!");

            dados = new double[linhas][colunas];
            int i = 0;
            for (Row row : sheet) {
                int j = 0;
                for(Cell cell : row) {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                dados[i][j] = 0.0d;
                            } else {
                                dados[i][j] = cell.getNumericCellValue();
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            try {
                                dados[i][j] = Double.parseDouble(cell.getRichStringCellValue().getString());
                            } catch (NumberFormatException | NullPointerException ex){
                                dados[i][j] = 0.0d;
                            }
                            break;
                        default:
                            dados[i][j] = 0.0d;
                    }
                    j++;
                }
                i++;
            }

        } catch (InvalidFormatException ex) {
            throw new IOException("Formato inválido!");
        }
        
        return dados;
    }

}
