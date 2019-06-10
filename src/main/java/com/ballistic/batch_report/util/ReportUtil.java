package com.ballistic.batch_report.util;

import com.ballistic.batch_report.model.vo.StockPriceVo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by AdMaxim on 5/31/2019.
 */
@Component
@Scope("prototype")
public class ReportUtil {

    public static final Logger logger = LogManager.getLogger(ReportUtil.class);

    private final static String TEMPLATE_PATH = "templates/report.xlsx";
    private final static String REMOTE_PATH = "C:/Users/AdMaxim/Desktop/upload/";
    private final static String REPORT_FOLDER = "report/";

    private final static String UNDER_SORE = "_";
    private final static String EXTENSION = ".xlsx";
    private final static String PNG = "PNG";
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private final static Integer WIDTH = 250;
    private final static Integer HEIGHT = 250;

    /*
    This method takes the text to be encoded, the width and height of the QR Code,
    and returns the QR Code in the form of a byte array.
    If you’re developing a web application and want to return the QR Code image as a response to an http request.
    You can return the byte array in the body of your http response.
    */
    public byte[] getQRCodeImage(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, PNG, pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    public File generateStockPriceReportDailyExcl(StockPriceVo stockPriceVo) throws IOException, WriterException {
        String fileName = null;
        String fileUploadPath = null;
        try {
            if(stockPriceVo != null) {
                if(stockPriceVo.getUuId() != null) {
                    fileName = stockPriceVo.getUuId()+UNDER_SORE+System.currentTimeMillis()+EXTENSION;
                    fileUploadPath = REMOTE_PATH + REPORT_FOLDER + fileName;
                    // 0st get the resource from path
                    URL url = Thread.currentThread().getContextClassLoader().getResource(TEMPLATE_PATH);
                    File file = new File(url.getPath());
                    // 1st copy template.
                    FileOutputStream fileOut = new FileOutputStream(fileUploadPath);
                    Files.copy(file.toPath(), fileOut);
                    // 2nd insert data to newly copied file. So that template coludn't be changed.
                    XSSFWorkbook workbook = new XSSFWorkbook(new File(fileUploadPath));
                    XSSFFont font = workbook.createFont();
                    font.setBold(true);
                    XSSFSheet reportSheet = workbook.getSheetAt(0);
                    // 3rd create the message for qr-code
                    String stockPriceQrMessage = this.getStockPriceQrMessage(stockPriceVo);
                    // 4th convert the message into the qr-code byte
                    byte[] qrCode = this.getQRCodeImage(stockPriceQrMessage);
                    Integer pictureIda = workbook.addPicture(qrCode, Workbook.PICTURE_TYPE_PNG);

                    writeToExcelCell(reportSheet, 2, 1, stockPriceVo.getUuId());
                    writeToExcelCell(reportSheet, 2, 2, stockPriceVo.getCreateDate());
                    writeToExcelCell(reportSheet, 2, 3, stockPriceVo.getOpenPrice());
                    writeToExcelCell(reportSheet, 2, 4, stockPriceVo.getHighPrice());
                    writeToExcelCell(reportSheet, 2, 5, stockPriceVo.getLowPrice());
                    writeToExcelCell(reportSheet, 2, 6, stockPriceVo.getClosePrice());
                    writeToExcelCell(reportSheet, 2, 7, stockPriceVo.getWap());
                    writeToExcelCell(reportSheet, 2, 8, stockPriceVo.getNoOfShares());
                    writeToExcelCell(reportSheet, 2, 9, stockPriceVo.getNoOfTrades());
                    writeToExcelCell(reportSheet, 2, 10, stockPriceVo.getTotalTurnover());
                    writeToExcelCell(reportSheet, 2, 11, stockPriceVo.getDeliverableQuantity());
                    writeToExcelCell(reportSheet, 2, 12, stockPriceVo.getDeliQtyToTradedQty());
                    writeToExcelCell(reportSheet, 2, 13, stockPriceVo.getSpreadHighLow());
                    writeToExcelCell(reportSheet, 2, 14, stockPriceVo.getClosePrice());
                    writeToImageExcelCell(reportSheet, 0, 15, pictureIda); // add the image
                    // total number price
                    writeToExcelCell(reportSheet, 4, 3, stockPriceVo.getOpenPrice());
                    writeToExcelCell(reportSheet, 4, 4, stockPriceVo.getHighPrice());
                    writeToExcelCell(reportSheet, 4, 5, stockPriceVo.getLowPrice());
                    writeToExcelCell(reportSheet, 4, 6, stockPriceVo.getClosePrice());
                    writeToExcelCell(reportSheet, 4, 8, stockPriceVo.getNoOfShares());
                    writeToExcelCell(reportSheet, 4, 9, stockPriceVo.getNoOfTrades());

                    workbook.write(fileOut);
                    fileOut.close();
                    workbook.close();
                    return new File(fileUploadPath); // read the resource
                } else {
                    throw new NullPointerException("Stock-Price Id Null No Report Create");
                }
            } else {
                throw new NullPointerException("Stock-Price Null No Report Create");
            }
        } catch (Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
        return null;
    }

    private void writeToExcelCell(XSSFSheet sheet, int rowNo, int cellNo, Object valueStr) {
        try {
            Row row = sheet.getRow(rowNo);
            Cell cell = row.createCell(cellNo);
            CellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
            CreationHelper createHelper = row.getSheet().getWorkbook().getCreationHelper();

            if(valueStr instanceof String) {
                cell.setCellType(CellType.STRING);
                cell.setCellValue((String) valueStr);
            } else if (valueStr instanceof Integer || valueStr instanceof Double || valueStr instanceof Date) {
                if(valueStr instanceof Integer) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Integer)valueStr);
                } else if (valueStr instanceof Double) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Double)valueStr);
                } else if (valueStr instanceof Date) {
                    short dateFormat = createHelper.createDataFormat().getFormat(DATE_FORMAT);
                    cellStyle.setDataFormat(dateFormat);
                    cell.setCellValue((Date)valueStr);
                    cell.setCellStyle(cellStyle);
                }
            }
        } catch(Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
    }

    private void writeToImageExcelCell(XSSFSheet sheet, int rowNo, int cellNo, Object valueStr) {
        try {
            XSSFDrawing drawing = sheet.createDrawingPatriarch(); /* Create the drawing container */
            XSSFClientAnchor my_anchor = new XSSFClientAnchor(); /* Create an anchor point */
            my_anchor.setCol1(cellNo); /* Define top left corner, and we can resize picture suitable from there */
            my_anchor.setRow1(rowNo); /* Invoke createPicture and pass the anchor point and ID */
            XSSFPicture  my_picture = drawing.createPicture(my_anchor, (Integer)valueStr);
            my_picture.resize(); /* Call resize method, which resizes the image */
        } catch(Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
    }

    private String getStockPriceQrMessage(StockPriceVo stockPriceVo) {
        return "{" +
                "uuId:" + stockPriceVo.getUuId() + "," + "openPrice:" + stockPriceVo.getOpenPrice() + "," +
                "highPrice:" + stockPriceVo.getHighPrice() + "," + "lowPrice:" + stockPriceVo.getLowPrice() + "," +
                "closePrice:" + stockPriceVo.getClosePrice() + "," + "wap:" + stockPriceVo.getWap() + "," +
                "noOfShares:" + stockPriceVo.getNoOfShares() + "," + "noOfTrades:" + stockPriceVo.getNoOfTrades() + "," +
                "totalTurnover:" + stockPriceVo.getTotalTurnover() + "," + "deliverableQuantity:" + stockPriceVo.getDeliverableQuantity() + "," +
                "deliQtyToTradedQty:" + stockPriceVo.getDeliQtyToTradedQty() + "," + "spreadHighLow:" + stockPriceVo.getSpreadHighLow() + "," +
                "spreadCloseOpen:" + stockPriceVo.getSpreadCloseOpen() + "," + "createDate:" + stockPriceVo.getCreateDate()
                +
                "}";
    }


//    public static void main(String args[]) throws IOException, WriterException {
//        StockPriceVo stockPriceVo = new StockPriceVo();
//        stockPriceVo.setUuId(UUID.randomUUID().toString());
//        stockPriceVo.setOpenPrice(1236.12d);
//        stockPriceVo.setHighPrice(1236.12d);
//        stockPriceVo.setLowPrice(-1236.12d);
//        stockPriceVo.setClosePrice(12360.12d);
//        stockPriceVo.setWap(12360.12d);
//        stockPriceVo.setNoOfShares(1200);
//        stockPriceVo.setNoOfTrades(1600);
//        stockPriceVo.setTotalTurnover(100d);
//        stockPriceVo.setDeliverableQuantity(100);
//        stockPriceVo.setDeliQtyToTradedQty(148d);
//        stockPriceVo.setSpreadHighLow(126d);
//        stockPriceVo.setSpreadCloseOpen(1269d);
//        stockPriceVo.setCreateDate(new Date());
//        for (int i=0; i<100; i++){
//            ReportUtil reportUtil = new ReportUtil();
//            reportUtil.generateStockPriceReportDailyExcl(stockPriceVo);
//            System.out.println("Pakistan Zindabad");
//        }
//
//    }
//
}
