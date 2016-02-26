package org.overture.codegen.vdm2c.sourceformat;

import java.io.File;
import java.io.IOException;

public class ClangFormat implements ISourceFileFormatter
{

	@Override
	public void format(File path)
	{
		try
		{
			ProcessBuilder pb = new ProcessBuilder("bash","-c","clang-format -i -style='{BreakBeforeBraces: GNU}' "+ path.getAbsolutePath());
			pb.environment().put("PATH", "/usr/local/bin:"
					+ System.getenv("PATH"));

			 pb.start();

		} catch (IOException e)
		{
			e.printStackTrace();
			// ignore
		}

	}

}
