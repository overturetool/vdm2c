package org.overture.ide.plugins.cgen.generator;

import java.io.File;
import java.io.FileOutputStream;
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

	public void generate(File cCodeOutputFolder) throws CoreException, AnalysisException
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

		// for (GeneratedModule module : generatedData.getClasses())
		// {
		//
		// if (module.canBeGenerated())
		// {
		// CodeGenConsole.GetInstance().println(module.getContent());
		// CodeGenConsole.GetInstance().println(module.getUnsupportedInIr());
		// CodeGenConsole.GetInstance().println(module.getMergeErrors());
		// CodeGenConsole.GetInstance().println(module.getUnsupportedInTargLang());
		// }
		// }

		// File libFolder = PluginVdm2CUtil.getCodeGenRuntimeLibFolder(vdmProject);
		//
		// try
		// {
		// PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.CODEGEN_RUNTIME_BIN_FILE, libFolder);
		// outputRuntimeBinaries(libFolder);
		// }
		// catch(Exception e)
		// {
		// CodeGenConsole.GetInstance().printErrorln("Problems copying the Java code generator runtime library to "
		// + libFolder.getAbsolutePath());
		// CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
		// }
		//
		// This should be where the VDM C lib gets copied.
		// try
		// {
		// PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.CODEGEN_RUNTIME_SOURCES_FILE, libFolder);
		// outputRuntimeSources(libFolder);
		// }
		// catch(Exception e)
		// {
		// CodeGenConsole.GetInstance().printErrorln("Problems copying the Java code generator runtime library sources to "
		// + libFolder.getAbsolutePath());
		// CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
		// }

		// try
		// {
		// PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.ECLIPSE_RES_FILES_FOLDER + "/"
		// + PluginVdm2CUtil.ECLIPSE_PROJECT_TEMPLATE_FILE, PluginVdm2CUtil.ECLIPSE_PROJECT_FILE,
		// eclipseProjectFolder);
		//
		// GeneralCodeGenUtils.replaceInFile(new File(eclipseProjectFolder,
		// PluginVdm2CUtil.ECLIPSE_PROJECT_FILE), "%s", project.getName());
		//
		//
		// PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.ECLIPSE_RES_FILES_FOLDER + "/"
		// + PluginVdm2CUtil.ECLIPSE_CLASSPATH_TEMPLATE_FILE, PluginVdm2CUtil.ECLIPSE_CLASSPATH_FILE,
		// eclipseProjectFolder);
		//
		// // Always imports codegen-runtime.jar
		// String classPathEntries = PluginVdm2CUtil.RUNTIME_CLASSPATH_ENTRY;
		//
		//
		// GeneralCodeGenUtils.replaceInFile(new File(eclipseProjectFolder,
		// PluginVdm2CUtil.ECLIPSE_CLASSPATH_FILE), "%s", classPathEntries);
		//
		//
		CodeGenConsole.GetInstance().println("Code generation completed successfully.");
		CodeGenConsole.GetInstance().println("Copying native library files."); // mvn install in vdm2c and
																				// mvn package here makes
																				// this work
		// Copy files from vdmclib.jar.
		copyNativeLibFiles(new File(cCodeOutputFolder + File.separator
				+ "nativelib"));
		//
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// CodeGenConsole.GetInstance().printErrorln("Problems generating the eclipse project with the generated Java code");
		// CodeGenConsole.GetInstance().printErrorln("Reason: "
		// + e.getMessage());
		// }
		//
		// // Output any warnings such as problems with the user's launch configuration
		// outputWarnings(generatedData.getWarnings());
		//
		//
		// // Summarize the code generation process
		// int noOfClasses = generatedData.getClasses().size();
		//
		// String msg = String.format("...finished Java code generation (generated %s %s).",
		// noOfClasses,
		// noOfClasses == 1 ? "class" : "classes");
		//
		// CodeGenConsole.GetInstance().println(msg);
		//

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
				outputFile = new File(outfolder.toString()
						+ File.separator
						+ filejarentry.getName().replace("src" + File.separator, ""));

				if (filejarentry.getName().contains("META"))
				{
					filejarentry = jarstream.getNextJarEntry();
					continue;
				}
				if (filejarentry.isDirectory())
				{
					outputFile.mkdir();
					filejarentry = jarstream.getNextJarEntry();
					continue;
				}
				if (filejarentry.getName().contains("SampleMakefile"))
				{
					outputFile = new File(outputFile.getAbsolutePath().replace("nativelib" + File.separator,  ""));
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
