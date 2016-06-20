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
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.utils.AnalysisExceptionIR;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.cgen.Activator;
import org.overture.ide.plugins.cgen.CodeGenConsole;
import org.overture.ide.plugins.cgen.ICodeGenConstants;
import org.overture.ide.plugins.cgen.generator.CGenerator;
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
					File cCodeOutputFolder = PluginVdm2CUtil.getCCodeOutputFolder(vdmProject);

					CGenerator generator = new CGenerator(vdmProject);

					generator.generate(cCodeOutputFolder);

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

	private void handleUnexpectedException(Exception ex)
	{
		String errorMessage = "Unexpected problem encountered when attempting to code generate the VDM model.\n"
				+ "The details of this problem have been reported in the Error Log.";

		Activator.log(errorMessage, ex);
		CodeGenConsole.GetInstance().printErrorln(errorMessage);
		ex.printStackTrace();
	}

}
