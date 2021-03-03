package ru.dvorobiev.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import ru.dvorobiev.model.DataSignal;
import ru.dvorobiev.model.DataSignalDAO;

/** класс для вывода информации в Excel */
public class ReportExcel {
    /** объект рабочей книги */
    public HSSFWorkbook workbook;

    public HSSFSheet sheet;
    public List<DataSignal> list;
    /** Сообщение об ошибке */
    public String errMessage;
    /** Путь с именем файла данных по умолчанию ./(name_sheet).xls */
    public String path;

    public static final int OK = 0;
    public static final int ERR = -1;
    public static final int EMPTY = -2;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /** @param name_sheet : имя листа рабочей книги */
    public ReportExcel(String name_sheet) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(name_sheet);
        this.list = DataSignalDAO.dataSignalList();
        path = new String();
        path = String.format("./%s.xls", name_sheet);
    }

    /**
     * Метод формирует отчет в Excel
     *
     * @throws : IOException
     * @return : OK, ERR - в случае исключения, EMPTY - нет данных
     */
    public int CreateReport() {
        int rownum = 0;
        Cell cell;
        Row row;

        HSSFCellStyle style = createStyleForTitle(workbook);
        row = sheet.createRow(rownum);
        // Date
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("DataTime");
        cell.setCellStyle(style);
        // miliseconds
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("ms");
        cell.setCellStyle(style);
        // Id_Node
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("ID Node");
        cell.setCellStyle(style);
        // Id_Obj
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("ID Object");
        cell.setCellStyle(style);
        // Value
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("valueSource");
        cell.setCellStyle(style);
        // Value
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("value");
        cell.setCellStyle(style);

        // Data
        for (DataSignal item : list) {
            rownum++;
            row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(item.getDateString(item.getDate()));
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue(item.getTime());
            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(item.getNodeId());
            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(item.getObjectId());
            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(item.getValueSource());
            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(item.getValue());
        }
        if (rownum > 0) {
            try {
                File file = new File(String.format("%s", path));
                file.getParentFile().mkdirs();

                FileOutputStream outFile = new FileOutputStream(file);
                workbook.write(outFile);
                errMessage = String.format("Created file: %s", file.getAbsolutePath());
                return OK;
            } catch (IOException e) {
                errMessage = e.getMessage();
                return ERR;
            }
        }
        errMessage = String.format("Count rows: %d. Report not create.", rownum);
        return EMPTY;
    }

    /**
     * Задаем необходимый стиль для работы в документе
     *
     * @param workbook: путь и имя файла с выгрухкой данных
     * @return style: вохвращем объект стиля документа
     */
    private HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }
}
