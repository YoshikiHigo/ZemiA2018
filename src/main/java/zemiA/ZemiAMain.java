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

		String srcDirectory = "src/main/java/zemiA/";

		for(String file: new File(srcDirectory).list()) {
			final File inputFile = new File(srcDirectory+file);

			if (classpathEntries.isEmpty()) {
				final String systemLibraries = System.getProperty("java.class.path");
				//final String externalLibraries = config.hasLIBRARY() ? config.getLIBRARY() : "";
				final String externalLibraries = "";
				classpathEntries.addAll(Arrays.asList(systemLibraries.split(File.pathSeparator)));
				classpathEntries.addAll(Arrays.asList(externalLibraries.split(File.pathSeparator)));
			}

			try {

				/*String text = FileUtils.readFileToString(inputFile, Charset.defaultCharset());*/
				// charset: encoding

				if (file.endsWith(".java")) {
					String text = readAll(inputFile.getAbsolutePath());
					final Document document = new Document(text);

					final ASTParser parser = ASTParser.newParser(AST.JLS10);
					//parser.setSource(String.join(System.lineSeparator(), lines).toCharArray());
					parser.setSource(document.get().toCharArray());
					parser.setUnitName(file);
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

					final ZemiAVisitor visitor = new ZemiAVisitor();
					unit.accept(visitor);

					printImformations(visitor);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private static String readAll(final String path) throws IOException {
		return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
				.collect(Collectors.joining(System.getProperty("line.separator")));
	}

	private static void printImformations(ZemiAVisitor visitor) {
		System.out.println("Class name: "+ visitor.getClassName());
		System.out.println("CINT: " + visitor.getCINT());
		System.out.println("CDISP: " + visitor.getCDISP());
		System.out.println("MAXNESTING: " + visitor.getMaxNesting());
		System.out.println("intensive coupling: " + visitor.isIntensiveCoupling());
		System.out.println("dispersed coupling: " + visitor.isDispersedCoupling());
		System.out.println("");
	}
}




