package redeneural.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Michael Murussi
 */
public class AmostrasXlsWriter implements AmostrasWriter {

    @Override
    public void write(File file, double[][] amostras) throws IOException {

        Workbook workbook;

        // seleciona formato da planilha
        if (file.getName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else {
            workbook = new HSSFWorkbook();
        }

        Map<String, CellStyle> styles = createStyles(workbook);

        Sheet sheet = workbook.createSheet();

        Row row;
        Cell cell;
        int lin = 0;
        for (double[] dy: amostras) {
            row = sheet.createRow(lin);
            int col = 0;
            for (double dx: dy) {                
                cell = row.createCell(col);
                cell.setCellValue(dx);
                cell.setCellStyle(styles.get("value"));
                col++;
            }
            lin++;
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        
    }

    private static Map<String, CellStyle> createStyles(Workbook workbook) {

        Map<String, CellStyle> styles = new HashMap<>();

        DataFormat dataFormat = workbook.createDataFormat();

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setDataFormat(dataFormat.getFormat("#,####0.0000"));
        styles.put("value", style);

        return styles;
    }


}
