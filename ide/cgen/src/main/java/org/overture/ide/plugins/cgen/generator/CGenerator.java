package org.overture.ide.plugins.cgen.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2c.CGen;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.utils.CGenUtil;
import org.overture.codegen.vdm2c.utils.NameMangler;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.plugins.cgen.CodeGenConsole;
import org.overture.ide.plugins.cgen.ICodeGenConstants;
import org.overture.ide.plugins.cgen.commands.Vdm2CCommand;
import org.overture.ide.plugins.cgen.util.PluginVdm2CUtil;

public class CGenerator
{
	final IVdmProject vdmProject;

	private AssistantManager assistantManager;
	
	public CGenerator(IVdmProject vdmProject)
	{
		this.vdmProject = vdmProject;
		this.assistantManager = new AssistantManager();
	}

	public void generate(File cCodeOutputFolder) throws CoreException,
	AnalysisException
	{
		File eclipseProjectFolder = PluginVdm2CUtil.getEclipseProjectFolder(vdmProject);

		// Clean folder with generated Java code
		GeneralUtils.deleteFolderContents(eclipseProjectFolder, true);

		final CGen vdm2c = new CGen();

		final IVdmModel model = vdmProject.getModel();

		CodeGenConsole.GetInstance().println("Project dialect: "
				+ PluginVdm2CUtil.dialect2Str(vdmProject.getDialect()));

		if (vdmProject.getDialect() == Dialect.VDM_RT)
		{
			CodeGenConsole.GetInstance().println("The current version of the C code generator does not fully support the timing and distributed aspects of VDM-RT.");
			CodeGenConsole.GetInstance().println("Please refer to the Overture User Manual for a discussion of supported language features.");
			CodeGenConsole.GetInstance().println("");
		}
		
		// Generate user specified classes
		GeneratedData data = vdm2c.generate(PluginVdm2CUtil.getNodes(model.getSourceUnits()));
		
		try {
			vdm2c.genCSourceFiles(cCodeOutputFolder, data.getClasses());
		} catch (Exception e) {
		
			CodeGenConsole.GetInstance().printErrorln("Problems encountered while generating C sources: " + e.getMessage());
			e.printStackTrace();
		}
		
		outputUserspecifiedModules(cCodeOutputFolder, data.getClasses());

		CodeGenConsole.GetInstance().println("Code generation completed successfully.");
		CodeGenConsole.GetInstance().println("Copying native library files."); // mvn install in vdm2c and
		// mvn package here makes
		// this work
		// Copy files from vdmclib.jar.
		CGenUtil.copyNativeLibFiles(Vdm2CCommand.class.getClassLoader().getResourceAsStream("jars/vdmclib.jar"),
				new File(cCodeOutputFolder + File.separator + "nativelib"));

		//Emit empty main.c file so that the generated project compiles.
		emitMainFile(new File(cCodeOutputFolder + File.separator + "main.c"));
		//Emit file containing the mapping between model names and mangled names as #defines.
		emitMangledNamesHeaderFile(new File(cCodeOutputFolder + File.separator + "MangledNames.h"));


	}
	
	private void outputUserspecifiedModules(File outputFolder,
			List<GeneratedModule> userspecifiedClasses)
	{
		for (GeneratedModule generatedModule : userspecifiedClasses)
		{
			if (generatedModule.hasMergeErrors())
			{
				CodeGenConsole.GetInstance().printErrorln(String.format("Could not generate C code for class %s. Following errors were found:", generatedModule.getName()));

				List<Exception> mergeErrors = generatedModule.getMergeErrors();

				for (Exception error : mergeErrors)
				{
					CodeGenConsole.GetInstance().printErrorln(error.toString());
				}
			} else if (!generatedModule.canBeGenerated())
			{
				CodeGenConsole.GetInstance().println("Could not code generate class: "
						+ generatedModule.getName() + ".");
				
				if(generatedModule.hasUnsupportedIrNodes())
				{
					LocationAssistantIR locationAssistant = assistantManager.getLocationAssistant();

					List<VdmNodeInfo> unsupportedInIr = locationAssistant.getVdmNodeInfoLocationSorted(generatedModule.getUnsupportedInIr());
					CodeGenConsole.GetInstance().println("Following VDM constructs are not supported by the code generator:");

					for (VdmNodeInfo  nodeInfo : unsupportedInIr)
					{
						String message = formatNodeString(nodeInfo, locationAssistant);
						CodeGenConsole.GetInstance().println(message);

						addMarkers(nodeInfo, locationAssistant);
					}
				}
				
				if(generatedModule.hasUnsupportedTargLangNodes())
				{
					Set<IrNodeInfo> unsupportedInTargLang = generatedModule.getUnsupportedInTargLang();
					CodeGenConsole.GetInstance().println("Following constructs are not supported by the code generator:");

					for (IrNodeInfo  nodeInfo : unsupportedInTargLang)
					{
						CodeGenConsole.GetInstance().println(nodeInfo.toString());
					}
				}
				
			} else
			{
				if (!(generatedModule.getIrNode() instanceof AClassHeaderDeclIR)) {
					continue;
				}

				File hFile = new File(outputFolder, generatedModule.getName() + ".h");
				File cFile = new File(outputFolder, generatedModule.getName() + ".c");

				CodeGenConsole.GetInstance().println("Generated class: " + generatedModule.getName());
				CodeGenConsole.GetInstance().println("Source file: " + hFile.getAbsolutePath());
				CodeGenConsole.GetInstance().println("Source file: " + cFile.getAbsolutePath());

				Set<IrNodeInfo> warnings = generatedModule.getTransformationWarnings();

				if (!warnings.isEmpty()) {
					CodeGenConsole.GetInstance()
							.println("The following warnings were found for class " + generatedModule.getName() + ":");

					for (IrNodeInfo nodeInfo : warnings) {
						CodeGenConsole.GetInstance().println(nodeInfo.getReason());
					}
				}
			}

			CodeGenConsole.GetInstance().println("");
		}
	}
	
	public static IFile convert(File file)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile iFile = workspace.getRoot().getFileForLocation(location);

		return iFile;
	}
	
	public static void addMarkers(VdmNodeInfo nodeInfo,
			LocationAssistantIR locationAssistant)
	{
		if (nodeInfo == null)
		{
			return;
		}

		INode node = nodeInfo.getNode();

		ILexLocation location = locationAssistant.findLocation(node);

		if (location == null)
		{
			return;
		}

		IFile ifile = convert(location.getFile());

		String reason = nodeInfo.getReason();

		String message = "Code generation support not implemented: "
				+ node.toString();
		message += reason != null ? ". Reason: " + reason : "";

		FileUtility.addMarker(ifile, message, location, IMarker.PRIORITY_NORMAL, ICodeGenConstants.PLUGIN_ID, -1);
	}
	
	public static String limitStr(String str)
	{
		if (str == null)
		{
			return "";
		}

		int length = str.length();
		final int limit = 100;

		String subString = null;

		if (length <= limit)
		{
			subString = str.substring(0, length);
		} else
		{
			subString = str.substring(0, limit) + "...";
		}

		return subString.replaceAll("\\s+", " ");
	}

	public static String formatNodeString(VdmNodeInfo nodeInfo,
			LocationAssistantIR locationAssistant)
	{
		INode node = nodeInfo.getNode();
		StringBuilder messageSb = new StringBuilder();
		messageSb.append(limitStr(node.toString()));
		messageSb.append(" (" + node.getClass().getSimpleName() + ")");

		ILexLocation location = locationAssistant.findLocation(node);
		if (location != null)
		{
			messageSb.append(" " + location.toShortString() + " in " + location.getFile().getAbsolutePath());
		}

		String reason = nodeInfo.getReason();
		if (reason != null)
		{
			messageSb.append(". Reason: " + reason);
		}

		return messageSb.toString();
	}



	private void emitMangledNamesHeaderFile(File outfile)
	{
		BufferedWriter fileWriter;

		try {
			fileWriter = new BufferedWriter(new FileWriter(outfile, true));

			for(Map.Entry<String, String> entry : NameMangler.mangledNames.entrySet())
			{
				fileWriter.append("#define " + entry.getKey() + " " + entry.getValue() + "\n");
			}
			
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void emitMainFile(File outfile)
	{
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outfile));
			fileWriter.write("int main()\n{\n\treturn 0;\n}\n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
