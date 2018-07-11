package zemiA;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*import org.apache.commons.io.FileUtils;*/
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;

public class ZemiAMain {
	private static List<String> sourceDirectories = new ArrayList<>();
	private static List<String> classpathEntries = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public static void main(final String[] args) {

		if (classpathEntries.isEmpty()) {
			final String systemLibraries = System.getProperty("java.class.path");
			//final String externalLibraries = config.hasLIBRARY() ? config.getLIBRARY() : "";
			final String externalLibraries = "";
			classpathEntries.addAll(Arrays.asList(systemLibraries.split(File.pathSeparator)));
			classpathEntries.addAll(Arrays.asList(externalLibraries.split(File.pathSeparator)));
		}

		try {
			//File dir = Select.FileSelect();
			//List<File> files = Select.extractFiles(dir, 0);
			File dir = new File("src/main/java/zemiA/");
			String text=null;
			String input = null;
			File inputFile = null;
			if(dir != null) {
				//for (File inf : files) {
				for (File inf : dir.listFiles()) {
					if(inf.getName().endsWith(".java")) {
						if(text == null) {
							input = dir.toString();
							inputFile = inf;
							text=readAll(inf.getAbsolutePath());
						}
						else text+=readAll(inf.getAbsolutePath());
					}
				}
				final Document document = new Document(text);
				
				
				if (text!=null) {
					final ASTParser parser = ASTParser.newParser(AST.JLS10);
					//parser.setSource(String.join(System.lineSeparator(), lines).toCharArray());
					parser.setSource(document.get().toCharArray());
					parser.setUnitName(input);
					parser.setKind(ASTParser.K_COMPILATION_UNIT);
					parser.setResolveBindings(true);
					parser.setBindingsRecovery(true);
					parser.setStatementsRecovery(true);
	
					if (sourceDirectories.isEmpty()) {
						sourceDirectories.add(inputFile.getParentFile().getAbsolutePath());
					}
	
					parser.setEnvironment(classpathEntries.toArray(new String[0]),
							sourceDirectories.toArray(new String[0]), null, true);
	
					final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
					options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
					options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
					options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
	
					parser.setCompilerOptions(options);
	
					final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
					//final AST ast = unit.getAST();
					//final ASTRewrite rewriter = ASTRewrite.create(ast);
					unit.recordModifications();
	
					//final ZemiAVisitor visitor = new ZemiAVisitor();
					final ZemiAVisitor visitor = new ZemiAVisitor();
					unit.accept(visitor);
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}


	private static String readAll(final String path) throws IOException {
		return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
				.collect(Collectors.joining(System.getProperty("line.separator")));
	}
}

