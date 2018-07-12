package zemiA;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OutExcel {

  OutExcel(List<ClassInformation> allDeclaratedClasses) throws IOException {

    Workbook book = new XSSFWorkbook();
    int i = 0;

    for(ClassInformation Data : allDeclaratedClasses) {
    	
    	Sheet sheet = book.createSheet(Data.getName());
	    i = 0;
	    Row row = sheet.createRow(i++);
	    Cell a = row.createCell(0);  // Excel上、「A1」の場所
	    Cell b = row.createCell(1);
	    a.setCellValue("Class name");
	    b.setCellValue(Data.getName());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("Parent Class name"); 
	    b.setCellValue(Data.getParentBindig().getBinaryName());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("NOM");
	    b.setCellValue(Data.getNOM());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("WMC"); 
	    b.setCellValue(Data.getWMC());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("AMW"); 
	    b.setCellValue(Data.getAMW());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("NProtM"); 
	    b.setCellValue(Data.getNProtM());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("BOvR"); 
	    b.setCellValue(Data.getBOvR());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("NAS"); 
	    b.setCellValue(Data.getNAS());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("PNAS"); 
	    b.setCellValue(Data.getPNAS());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("BUR"); 
	    b.setCellValue(Data.getBUR());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("Refused Parent Bequest"); 
	    b.setCellValue(Data.isRPB());
	    
	    row = sheet.createRow(i++);
	    a = row.createCell(0);
	    b = row.createCell(1);
	    a.setCellValue("Tradition Breaker"); 
	    b.setCellValue(Data.isTB());
	    
    }
    
    // ここから出力処理
    FileOutputStream out = null;
    try {
	// 出力先のファイルを指定
	out = new FileOutputStream("ZemiAData.xlsx");
	// 上記で作成したブックを出力先に書き込み
	book.write(out);
    
    } catch (FileNotFoundException e) {
	System.out.println(e.getStackTrace());

    } finally {
	// 最後はちゃんと閉じておきます
	out.close();
	book.close();
    }
	  
  }
}