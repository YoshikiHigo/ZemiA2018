package zemiA;
	import javax.swing.*;
	import java.io.File;
import java.util.ArrayList;
import java.util.List;

	public class Select{	
		static File FileSelect() {
			File file = null;
			JFileChooser filechooser = new JFileChooser("c:¥¥temp");
			filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int selected = filechooser.showOpenDialog(null);
		    if (selected == JFileChooser.APPROVE_OPTION){
		      file = filechooser.getSelectedFile();
		    }
			return file;
		}
		
		static List<File> extractFiles(File file) {
			List<File> files = new ArrayList<File>();
			if(file.isDirectory()) {
				
			for (File inf : file.listFiles()) {
				 if (inf.isFile()) files.add(inf);
				 else if (inf.isDirectory()) {
		                for (File cinf : inf.listFiles()) files.addAll(extractFiles(cinf));
		            }
			}
			
			}else files.add(file);
			return files;
			
		}
	}

