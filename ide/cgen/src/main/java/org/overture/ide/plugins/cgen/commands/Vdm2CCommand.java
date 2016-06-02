/*
 * #%~
 * Code Generator Plugin
 * %%
 * Copyright (C) 2008 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.cgen.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.Preferences;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.utils.AnalysisExceptionIR;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2c.CGen;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.cgen.Activator;
import org.overture.ide.plugins.cgen.CodeGenConsole;
import org.overture.ide.plugins.cgen.ICodeGenConstants;
import org.overture.ide.plugins.cgen.util.PluginVdm2CUtil;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

public class Vdm2CCommand extends AbstractHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// Validate project
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
		{
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		Object firstElement = structuredSelection.getFirstElement();

		if (!(firstElement instanceof IProject))
		{
			return null;
		}

		final IProject project = (IProject) firstElement;
		final IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		try
		{
			Settings.release = vdmProject.getLanguageVersion();
			Settings.dialect = vdmProject.getDialect();
		} catch (CoreException e)
		{
			Activator.log("Problems setting VDM language version and dialect", e);
			e.printStackTrace();
		}

		CodeGenConsole.GetInstance().activate();
		CodeGenConsole.GetInstance().clearConsole();

		deleteMarkers(project);

		final IVdmModel model = vdmProject.getModel();

		if (model == null)
		{
			CodeGenConsole.GetInstance().println("Could not get model for project: "
					+ project.getName());
			return null;
		}

		if (!model.isParseCorrect())
		{
			CodeGenConsole.GetInstance().println("Could not parse model: "
					+ project.getName());
			return null;
		}

		if (!model.isTypeChecked())
		{
			VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), vdmProject);
		}

		if (!model.isTypeCorrect())
		{
			CodeGenConsole.GetInstance().println("Could not type check model: "
					+ project.getName());
			return null;
		}

		CodeGenConsole.GetInstance().println("Starting VDM to C code generation.");

		Job codeGenerate = new Job("VDM to C code generation")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				// Begin code generation
				try
				{
					File eclipseProjectFolder = PluginVdm2CUtil.getEclipseProjectFolder(vdmProject);
					File cCodeOutputFolder = PluginVdm2CUtil.getCCodeOutputFolder(vdmProject);

					// Clean folder with generated Java code
					GeneralUtils.deleteFolderContents(eclipseProjectFolder, true);

					final CGen vdm2c = new CGen(cCodeOutputFolder);

					// Generate user specified classes
					GeneratedData generatedData = vdm2c.generate(PluginVdm2CUtil.getNodes(model.getSourceUnits()));

					CodeGenConsole.GetInstance().println("Project dialect: " + PluginVdm2CUtil.dialect2Str(vdmProject.getDialect()));

					if(vdmProject.getDialect() == Dialect.VDM_RT)
					{
						CodeGenConsole.GetInstance().println("The current version of the C code generator does not fully support the timing and distributed aspects of VDM-RT.");
						CodeGenConsole.GetInstance().println("Please refer to the Overture User Manual for a discussion of supported language features.");
					}


					//					for (GeneratedModule module : generatedData.getClasses())
					//					{
					//
					//						if (module.canBeGenerated())
					//						{
					//							CodeGenConsole.GetInstance().println(module.getContent());
					//							CodeGenConsole.GetInstance().println(module.getUnsupportedInIr());
					//							CodeGenConsole.GetInstance().println(module.getMergeErrors());
					//							CodeGenConsole.GetInstance().println(module.getUnsupportedInTargLang());
					//						}
					//					}

					//					File libFolder = PluginVdm2CUtil.getCodeGenRuntimeLibFolder(vdmProject);
					//					
					//					try
					//					{
					//						PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.CODEGEN_RUNTIME_BIN_FILE, libFolder);
					//						outputRuntimeBinaries(libFolder);
					//					}
					//					catch(Exception e)
					//					{
					//						CodeGenConsole.GetInstance().printErrorln("Problems copying the Java code generator runtime library to " + libFolder.getAbsolutePath());
					//						CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
					//					}
					//					
					//This should be where the VDM C lib gets copied.
					//					try
					//					{
					//						PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.CODEGEN_RUNTIME_SOURCES_FILE, libFolder);
					//						outputRuntimeSources(libFolder);
					//					}
					//					catch(Exception e)
					//					{
					//						CodeGenConsole.GetInstance().printErrorln("Problems copying the Java code generator runtime library sources to " + libFolder.getAbsolutePath());
					//						CodeGenConsole.GetInstance().printErrorln("Reason: " + e.getMessage());
					//					}



					//					try
					//					{
					//						PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.ECLIPSE_RES_FILES_FOLDER +  "/"
					//								+ PluginVdm2CUtil.ECLIPSE_PROJECT_TEMPLATE_FILE, PluginVdm2CUtil.ECLIPSE_PROJECT_FILE, eclipseProjectFolder);
					//						
					//						GeneralCodeGenUtils.replaceInFile(new File(eclipseProjectFolder, PluginVdm2CUtil.ECLIPSE_PROJECT_FILE), "%s", project.getName());
					//						
					//						
					//						PluginVdm2CUtil.copyCodeGenFile(PluginVdm2CUtil.ECLIPSE_RES_FILES_FOLDER +  "/"
					//								+ PluginVdm2CUtil.ECLIPSE_CLASSPATH_TEMPLATE_FILE, PluginVdm2CUtil.ECLIPSE_CLASSPATH_FILE, eclipseProjectFolder);
					//						
					//						// Always imports codegen-runtime.jar
					//						String classPathEntries =  PluginVdm2CUtil.RUNTIME_CLASSPATH_ENTRY;
					//						
					//						
					//						GeneralCodeGenUtils.replaceInFile(new File(eclipseProjectFolder, PluginVdm2CUtil.ECLIPSE_CLASSPATH_FILE), "%s", classPathEntries);
					//						
					//						
					CodeGenConsole.GetInstance().println("Code generation completed successfully.");
					CodeGenConsole.GetInstance().println("Copying native library files."); //mvn install in vdm2c and mvn package here makes this work
					//Copy files from vdmclib.jar.
					copyNativeLibFiles(cCodeOutputFolder);
					//
					//					} catch (Exception e)
					//					{
					//						e.printStackTrace();
					//						CodeGenConsole.GetInstance().printErrorln("Problems generating the eclipse project with the generated Java code");
					//						CodeGenConsole.GetInstance().printErrorln("Reason: "
					//								+ e.getMessage());
					//					}
					//					
					//					// Output any warnings such as problems with the user's launch configuration
					//					outputWarnings(generatedData.getWarnings());
					//
					//					
					//					// Summarize the code generation process
					//					int noOfClasses = generatedData.getClasses().size();
					//					
					//					String msg = String.format("...finished Java code generation (generated %s %s).", 
					//							noOfClasses, 
					//							noOfClasses == 1 ? "class" : "classes");
					//					
					//					CodeGenConsole.GetInstance().println(msg);
					//
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					//
				} catch (AnalysisExceptionIR ex)
				{
					CodeGenConsole.GetInstance().println("Could not code generate VDM model: "
							+ ex.getMessage());
				} catch (Exception ex)
				{
					handleUnexpectedException(ex);
				}

				return Status.OK_STATUS;
			}
		};

		codeGenerate.schedule();

		return null;
	}

	private void copyNativeLibFiles(File outfolder)
	{
		File outputFile = null;
		InputStream jarfile = null;
		FileOutputStream fos = null;
		JarInputStream jarstream = null;
		JarEntry filejarentry = null;

		try 
		{
			jarfile = Vdm2CCommand.class.getClassLoader().getResourceAsStream("jars/vdmclib.jar");
			jarstream = new JarInputStream(jarfile);
			filejarentry = jarstream.getNextJarEntry();

			//Simply step through the JAR containing the library files and extract only the code files.
			//These are copied to the source output folder.
			while (filejarentry != null)
			{
				outputFile = new File(outfolder.toString() + File.separator + ".." + File.separator + filejarentry.getName());

				if(filejarentry.getName().contains("META"))
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

				fos = new java.io.FileOutputStream(outputFile);

				while (jarstream.available() > 0) 
				{
					int b = jarstream.read();
					if(b >= 0)
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
		{e.printStackTrace();}
	}

	public IRSettings getIrSettings(final IProject project)
	{
		Preferences preferences = getPrefs();

		boolean generateCharSeqsAsStrings = preferences.getBoolean(ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRINGS, ICodeGenConstants.GENERATE_CHAR_SEQUENCES_AS_STRING_DEFAULT);
		boolean generateConcMechanisms = preferences.getBoolean(ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS, ICodeGenConstants.GENERATE_CONCURRENCY_MECHANISMS_DEFAULT);

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(generateCharSeqsAsStrings);
		irSettings.setGenerateConc(generateConcMechanisms);

		return irSettings;
	}

	private Preferences getPrefs()
	{
		Preferences preferences = InstanceScope.INSTANCE.getNode(ICodeGenConstants.PLUGIN_ID);
		return preferences;
	}

	//	public JavaSettings getJavaSettings(final IProject project, List<String> classesToSkip)
	//	{
	//		Preferences preferences = getPrefs();
	//		
	//		boolean disableCloning = preferences.getBoolean(ICodeGenConstants.DISABLE_CLONING, ICodeGenConstants.DISABLE_CLONING_DEFAULT);
	//		String javaPackage = preferences.get(ICodeGenConstants.JAVA_PACKAGE, ICodeGenConstants.JAVA_PACKAGE_DEFAULT);
	//		boolean genVdmLoc = preferences.getBoolean(ICodeGenConstants.GENERATE_VDM_LOCATIONS_INFO, ICodeGenConstants.GENERATE_VDM_LOCATIONS_INFO_DEFAULT);
	//		
	//		JavaSettings javaSettings = new JavaSettings();
	//		javaSettings.setDisableCloning(disableCloning);
	//		javaSettings.setModulesToSkip(classesToSkip);
	//		javaSettings.setJavaRootPackage(javaPackage);
	//		javaSettings.setPrintVdmLocations(genVdmLoc);
	//		
	//		if (!JavaCodeGenUtil.isValidJavaPackage(javaSettings.getJavaRootPackage()))
	//		{
	//			javaSettings.setJavaRootPackage(project.getName());
	//		}
	//		
	//		return javaSettings;
	//	}

	private void deleteMarkers(IProject project)
	{
		if (project == null)
		{
			return;
		}

		try
		{
			project.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
		} catch (CoreException ex)
		{
			Activator.log("Could not delete markers for project: "
					+ project.toString(), ex);
			ex.printStackTrace();
		}
	}

	//	private void outputWarnings(List<String> warnings)
	//	{
	//		if(warnings != null && !warnings.isEmpty())
	//		{
	//			for(String warning : warnings)
	//			{
	//				CodeGenConsole.GetInstance().println(PluginVdm2CUtil.WARNING + " " + warning);
	//			}
	//			
	//			CodeGenConsole.GetInstance().printErrorln("");
	//		}
	//	}

	//	private void outputRuntimeBinaries(File outputFolder)
	//	{
	//		File runtime = new File(outputFolder, PluginVdm2CUtil.CODEGEN_RUNTIME_BIN_FILE);
	//		CodeGenConsole.GetInstance().println("Copied the Java code generator runtime library to " + runtime.getAbsolutePath());
	//	}
	//	
	//	private void outputRuntimeSources(File outputFolder)
	//	{
	//		File runtime = new File(outputFolder, PluginVdm2CUtil.CODEGEN_RUNTIME_SOURCES_FILE);
	//		CodeGenConsole.GetInstance().println("Copied the Java code generator runtime library sources to " + runtime.getAbsolutePath() + "\n");
	//	}
	//	
	//	private void outputUserspecifiedModules(File outputFolder,
	//			List<GeneratedModule> userspecifiedClasses)
	//	{
	//		for (GeneratedModule generatedModule : userspecifiedClasses)
	//		{
	//			if (generatedModule.hasMergeErrors())
	//			{
	//				CodeGenConsole.GetInstance().printErrorln(String.format("Could not generate Java for class %s. Following errors were found:", generatedModule.getName()));
	//
	//				List<Exception> mergeErrors = generatedModule.getMergeErrors();
	//
	//				for (Exception error : mergeErrors)
	//				{
	//					CodeGenConsole.GetInstance().printErrorln(error.toString());
	//				}
	//			} else if (!generatedModule.canBeGenerated())
	//			{
	//				CodeGenConsole.GetInstance().println("Could not code generate class: "
	//						+ generatedModule.getName() + ".");
	//				
	//				if(generatedModule.hasUnsupportedIrNodes())
	//				{
	//					LocationAssistantIR locationAssistant = assistantManager.getLocationAssistant();
	//
	//					List<VdmNodeInfo> unsupportedInIr = locationAssistant.getVdmNodeInfoLocationSorted(generatedModule.getUnsupportedInIr());
	//					CodeGenConsole.GetInstance().println("Following VDM constructs are not supported by the code generator:");
	//
	//					for (VdmNodeInfo  nodeInfo : unsupportedInIr)
	//					{
	//						String message = PluginVdm2CUtil.formatNodeString(nodeInfo, locationAssistant);
	//						CodeGenConsole.GetInstance().println(message);
	//
	//						PluginVdm2CUtil.addMarkers(nodeInfo, locationAssistant);
	//					}
	//				}
	//				
	//				if(generatedModule.hasUnsupportedTargLangNodes())
	//				{
	//					Set<IrNodeInfo> unsupportedInTargLang = generatedModule.getUnsupportedInTargLang();
	//					CodeGenConsole.GetInstance().println("Following constructs are not supported by the code generator:");
	//
	//					for (IrNodeInfo  nodeInfo : unsupportedInTargLang)
	//					{
	//						CodeGenConsole.GetInstance().println(nodeInfo.toString());
	//					}
	//				}
	//				
	//			} else
	//			{
	//				File javaFile = new File(outputFolder, generatedModule.getName()
	//						+ IJavaConstants.JAVA_FILE_EXTENSION);
	//				CodeGenConsole.GetInstance().println("Generated class: "
	//						+ generatedModule.getName());
	//				CodeGenConsole.GetInstance().println("Java source file: "
	//						+ javaFile.getAbsolutePath());
	//				
	//				Set<IrNodeInfo> warnings = generatedModule.getTransformationWarnings();
	//				
	//				if(!warnings.isEmpty())
	//				{
	//					CodeGenConsole.GetInstance().println("The following warnings were found for class " + generatedModule.getName() + ":");
	//
	//					for (IrNodeInfo  nodeInfo : warnings)
	//					{
	//						CodeGenConsole.GetInstance().println(nodeInfo.getReason());
	//					}
	//				}
	//
	//			}
	//
	//			CodeGenConsole.GetInstance().println("");
	//		}
	//	}
	//
	//	private void outputQuotes(IVdmProject vdmProject, File outputFolder,
	//			JavaCodeGen vdm2c, List<GeneratedModule> quotes) throws CoreException
	//	{
	//		if (quotes != null && !quotes.isEmpty())
	//		{
	//			for(GeneratedModule q : quotes)
	//			{
	//				vdm2c.genJavaSourceFile(outputFolder, q);
	//			}
	//
	//			CodeGenConsole.GetInstance().println("Quotes generated to folder: "
	//					+ outputFolder.getAbsolutePath());
	//			CodeGenConsole.GetInstance().println("");
	//		}
	//	}

	private void handleUnexpectedException(Exception ex)
	{
		String errorMessage = 
				"Unexpected problem encountered when attempting to code generate the VDM model.\n"
						+ "The details of this problem have been reported in the Error Log.";

		Activator.log(errorMessage, ex);
		CodeGenConsole.GetInstance().printErrorln(errorMessage);
		ex.printStackTrace();
	}

	//	private void handleInvalidNames(InvalidNamesResult invalidNames)
	//	{
	//		String message = "The model either uses words that are reserved by Java, declares VDM types"
	//				+ " that uses Java type names or uses variable names that potentially"
	//				+ " conflict with code generated temporary variable names";
	//
	//		CodeGenConsole.GetInstance().println("Warning: " + message);
	//
	//		String violationStr = GeneralCodeGenUtils.constructNameViolationsString(invalidNames);
	//		CodeGenConsole.GetInstance().println(violationStr);
	//
	//		Set<Violation> typeNameViolations = invalidNames.getTypenameViolations();
	//		PluginVdm2CUtil.addMarkers("Type name violation", typeNameViolations);
	//
	//		Set<Violation> reservedWordViolations = invalidNames.getReservedWordViolations();
	//		PluginVdm2CUtil.addMarkers("Reserved word violations", reservedWordViolations);
	//	}
}
