package org.overture.ide.plugins.cgen.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.eclipse.core.runtime.CoreException;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2c.CGen;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.cgen.CodeGenConsole;
import org.overture.ide.plugins.cgen.commands.Vdm2CCommand;
import org.overture.ide.plugins.cgen.util.PluginVdm2CUtil;

public class CGenerator
{
	final IVdmProject vdmProject;

	public CGenerator(IVdmProject vdmProject)
	{
		this.vdmProject = vdmProject;
	}

	public void generate(File cCodeOutputFolder) throws CoreException,
	AnalysisException
	{
		File eclipseProjectFolder = PluginVdm2CUtil.getEclipseProjectFolder(vdmProject);

		// Clean folder with generated Java code
		GeneralUtils.deleteFolderContents(eclipseProjectFolder, true);

		final CGen vdm2c = new CGen(cCodeOutputFolder);

		final IVdmModel model = vdmProject.getModel();

		// Generate user specified classes
		vdm2c.generate(PluginVdm2CUtil.getNodes(model.getSourceUnits()));

		CodeGenConsole.GetInstance().println("Project dialect: "
				+ PluginVdm2CUtil.dialect2Str(vdmProject.getDialect()));

		if (vdmProject.getDialect() == Dialect.VDM_RT)
		{
			CodeGenConsole.GetInstance().println("The current version of the C code generator does not fully support the timing and distributed aspects of VDM-RT.");
			CodeGenConsole.GetInstance().println("Please refer to the Overture User Manual for a discussion of supported language features.");
		}

		CodeGenConsole.GetInstance().println("Code generation completed successfully.");
		CodeGenConsole.GetInstance().println("Copying native library files."); // mvn install in vdm2c and
		// mvn package here makes
		// this work
		// Copy files from vdmclib.jar.
		copyNativeLibFiles(new File(cCodeOutputFolder + File.separator
				+ "nativelib"));
//		emitMainFile(new File(cCodeOutputFolder + File.separator + "main.c"));

	}

	private void emitMainFile(File outfile)
	{
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outfile));
			fileWriter.write("void main(){sldkfj}\n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void copyNativeLibFiles(File outfolder)
	{
		File outputFile = null;
		InputStream jarfile = null;
		FileOutputStream fos = null;
		JarInputStream jarstream = null;
		JarEntry filejarentry = null;

		if (!outfolder.exists())
		{
			outfolder.mkdir();
		}

		try
		{
			jarfile = Vdm2CCommand.class.getClassLoader().getResourceAsStream("jars/vdmclib.jar");
			jarstream = new JarInputStream(jarfile);
			filejarentry = jarstream.getNextJarEntry();

			// Simply step through the JAR containing the library files and extract only the code files.
			// These are copied to the source output folder.
			while (filejarentry != null)
			{
				if(!filejarentry.getName().contains("src/main") ||
						filejarentry.getName().contains("META"))
				{
					filejarentry = jarstream.getNextJarEntry();
					continue;
				}

				outputFile = new File(outfolder.toString()
						+ File.separator
						+ filejarentry.getName().replace("src/main" + File.separator, ""));

				if (filejarentry.isDirectory())
				{
					outputFile.mkdir();
					filejarentry = jarstream.getNextJarEntry();
					continue;
				}
				if (filejarentry.getName().contains("ProjectCMakeLists") ||
						filejarentry.getName().contains("main.c") ||
						filejarentry.getName().contains("README"))
				{
					outputFile = new File(outputFile.getAbsolutePath().replace("nativelib"
							+ File.separator, ""));
				}

				fos = new java.io.FileOutputStream(outputFile);

				while (jarstream.available() > 0)
				{
					int b = jarstream.read();
					if (b >= 0)
					{
						fos.write(b);
					}
				}
				fos.flush();
				fos.close();
				jarstream.closeEntry();
				filejarentry = jarstream.getNextJarEntry();

			}
			jarstream.close();
			jarfile.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
