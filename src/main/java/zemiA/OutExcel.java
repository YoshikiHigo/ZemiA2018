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
			sheet.setColumnWidth(0, 8192);
			sheet.setColumnWidth(1, 8192);

			Row row = sheet.createRow(i++);
			Cell a = row.createCell(0);  // Excel上、「A1」の場所
			Cell b = row.createCell(1);
			a.setCellValue("Class name");
			b.setCellValue(Data.getName());

			row = sheet.createRow(i++);
			a = row.createCell(0);
			b = row.createCell(1);
			a.setCellValue("Parent Class name");
			if(Data.getParentBindig() != null) b.setCellValue(Data.getParentBindig().getBinaryName());
			else b.setCellValue("has no parent");

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
			if(Data.getBOvR() != -1)
				b.setCellValue(Data.getBOvR());
			else b.setCellValue("parent is a third-party class");

			row = sheet.createRow(i++);
			a = row.createCell(0);
			b = row.createCell(1);

			a.setCellValue("NAS");
			if(Data.getNAS() != -1)
				b.setCellValue(Data.getNAS());
			else b.setCellValue("parent is a third-party class");

			row = sheet.createRow(i++);
			a = row.createCell(0);
			b = row.createCell(1);
			a.setCellValue("PNAS");
			if(Data.getPNAS() != -1)
				b.setCellValue(Data.getPNAS());
			else b.setCellValue("parent is a third-party class");

			row = sheet.createRow(i++);
			a = row.createCell(0);
			b = row.createCell(1);
			a.setCellValue("BUR");
			if(Data.getBUR() != -1)
				b.setCellValue(Data.getBUR());
			else b.setCellValue("parent is a third-party class");

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

			for(MethodInformation mData : Data.getMethodInformation()) {
				i++;

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("methodName");
				b.setCellValue(mData.getName());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("CINT");
				b.setCellValue(mData.getCINT());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("CDISP");
				b.setCellValue(mData.getCDISP());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("MAXNESTING");
				b.setCellValue(mData.getMaxNesting());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("CM");
				b.setCellValue(mData.getCM());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("CC");
				b.setCellValue(mData.getCC());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("intensive coupling");
				b.setCellValue(mData.isIntensiveCoupling());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("dispersed coupling");
				b.setCellValue(mData.isDispersedCoupling());

				row = sheet.createRow(i++);
				a = row.createCell(0);
				b = row.createCell(1);
				a.setCellValue("shotgun surgery");
				b.setCellValue(mData.isShotgunSurgery());


			}



		}

		// ここから出力処理
		FileOutputStream out = null;
		try {
			// 出力先のファイルを指定
			out = new FileOutputStream("ZemiAData.xlsx");
			// 上記で作成したブックを出力先に書き込み
			book.write(out);
			//finallyでcloseすると上書きできなかった
			out.close();
			book.close();
		} catch (FileNotFoundException e) {
			//System.out.println(e.getStackTrace());
		}
	}
}