package zemiA;
	import javax.swing.*;
	import java.io.File;

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
	}

