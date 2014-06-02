package redeneural.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import redeneural.classificador.validacao.Validacao;

/**
 *
 * @author Michael Murussi
 */
public class ValidacaoXlsWriter extends ValidacaoWriter {

    private final int linhas;
    private final int colunasEntrada;
    private final int colunasSaida;

    public ValidacaoXlsWriter(Validacao validacao) {
        super(validacao);

        linhas = validacao.getAmostrasRede().getEntrada().length;
        colunasEntrada = validacao.getAmostrasRede().getNumeroParametros();
        // saída desejada e obtida possuem o mesmo número de colunas
        colunasSaida = validacao.getAmostrasRede().getNumeroClasses();
    }

    @Override
    public void write(File file) throws FileNotFoundException, IOException {

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
        int col = 0, lin = 0;

        // cabeçalho
        row = sheet.createRow(lin);
        cell = row.createCell(col);
        cell.setCellValue("P");
        cell.setCellStyle(styles.get("header"));
        col = 1;
        for (int j=0; j < colunasEntrada; j++) {
            cell = row.createCell(col);
            cell.setCellValue(String.format("E%d", j));
            cell.setCellStyle(styles.get("header"));
            col++;
        }
        for (int j=0; j < colunasSaida; j++) {
            cell = row.createCell(col);
            cell.setCellValue(String.format("Y%d", j));
            cell.setCellStyle(styles.get("header"));
            col++;
        }
        for (int j=0; j < colunasSaida; j++) {
            cell = row.createCell(col);
            cell.setCellValue(String.format("O%d", j));
            cell.setCellStyle(styles.get("header"));
            col++;
        }

        // fixa a primeira linha e a primeira coluna (cabeçalho)
        // sheet.createFreezePane(1, 1);
        lin++;

        // dados
        double[][] entrada = getValidacao().getAmostrasRede().getEntrada();
        double[][] saidaDesejada = getValidacao().getSaidaDesejada();
        double[][] saidaObtida = getValidacao().getSaidaObtida();

        for (int i = 0; i < linhas; i++) {
            row = sheet.createRow(lin);
            col = 0;
            cell = row.createCell(col);
            cell.setCellValue(i);
            cell.setCellStyle(styles.get("value"));

            col++;
            for (int j = 0; j < colunasEntrada; j++) {
                cell = row.createCell(col);
                cell.setCellValue(entrada[i][j]);
                cell.setCellStyle(styles.get("value"));
                col++;
            }
            for (int j = 0; j < colunasSaida; j++) {
                cell = row.createCell(col);
                cell.setCellValue(saidaDesejada[i][j]);
                cell.setCellStyle(styles.get("value"));
                col++;
            }
            for (int j = 0; j < colunasSaida; j++) {
                cell = row.createCell(col);
                cell.setCellValue(saidaObtida[i][j]);
                cell.setCellStyle(styles.get("value"));
                col++;
            }
            lin++;
        }

        // salva
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
    }

    private static Map<String, CellStyle> createStyles(Workbook workbook) {
        
        Map<String, CellStyle> styles = new HashMap<>();

        CellStyle style;
        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(workbook);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        styles.put("header", style);

        DataFormat dataFormat = workbook.createDataFormat();        

        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setDataFormat(dataFormat.getFormat("#,###0.000"));
        styles.put("value", style);

        return styles;
    }

    private static CellStyle createBorderedStyle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
}
