package org.overture.codegen.vdm2c.sourceformat;

import java.io.File;

public interface ISourceFileFormatter
{
	/**
	 * Format the source file in place at the given location
	 * 
	 * @param path the path to the source file which should be formatted
	 */
	void format(File path);
}
