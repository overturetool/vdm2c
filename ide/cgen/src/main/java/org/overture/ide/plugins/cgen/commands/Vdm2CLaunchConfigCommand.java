package org.overture.ide.plugins.cgen.commands;

import java.util.List;

import org.eclipse.core.resources.IProject;
//import org.overture.codegen.vdm2c.JavaSettings;
//import org.overture.ide.plugins.cgen.CodeGenConsole;
import org.overture.ide.plugins.cgen.util.LaunchConfigData;
import org.overture.ide.plugins.cgen.util.PluginVdm2CUtil;

public class Vdm2CLaunchConfigCommand// extends Vdm2CCommand
{
//	@Override
//	public JavaSettings getJavaSettings(IProject project,
//			List<String> classesToSkip)
//	{
//		List<LaunchConfigData> launchConfigs = PluginVdm2CUtil.getProjectLaunchConfigs(project);
//
//		if (!launchConfigs.isEmpty())
//		{
//			String entryExp = PluginVdm2CUtil.dialog(launchConfigs);
//
//			if (entryExp != null)
//			{
//				JavaSettings javaSettings = super.getJavaSettings(project, classesToSkip);
//				javaSettings.setVdmEntryExp(entryExp);
//				return javaSettings;
//			}
//			else
//			{
//				CodeGenConsole.GetInstance().println("Process cancelled by user.");
//			}
//		} else
//		{
//			CodeGenConsole.GetInstance().println(PluginVdm2CUtil.WARNING
//					+ " No launch configuration could be found for this project.\n");
//			CodeGenConsole.GetInstance().println("Cancelling launch configuration based code generation...\n");
//		}
//
//		return null;
//	}
}
