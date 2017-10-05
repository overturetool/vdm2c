package org.overture.codegen.vdm2c.utils;

import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class CGenUtil {

	public static final String C_NULL = "NULL";
	/**
	 * Extract the native library sources to some output folder
	 * 
	 * @param jarfile The vdmclib JAR file that contains the native library sources
	 * @param outfolder The output folder
	 */
	public static void copyDistributionLibFiles(InputStream jarfile, File outfolder)
	{
		if (!outfolder.exists())
		{
			outfolder.mkdir();
		}

		try
		{
			JarInputStream jarstream = new JarInputStream(jarfile);
			JarEntry filejarentry = jarstream.getNextJarEntry();

			// Simply step through the JAR containing the library files and extract only the code files.
			// These are copied to the source output folder.
			while (filejarentry != null)
			{
				if(!filejarentry.getName().contains("src/main") ||
						filejarentry.getName().contains("META") || filejarentry.isDirectory())
				{
					filejarentry = jarstream.getNextJarEntry();
					continue;
				}

				File outputFile = new File(outfolder.toString()
						+ File.separator
						+ filejarentry.getName().replace("src/main/", ""));

				if (filejarentry.getName().contains("ProjectCMakeLists") ||
						filejarentry.getName().contains("main.c") ||
						filejarentry.getName().contains("README"))
				{
					outputFile = new File(outputFile.getAbsolutePath().replace("distributionLib"
							+ File.separator, ""));
				}
				
				if(outputFile.getParentFile() != null)
				{
					outputFile.getParentFile().mkdirs();
				}

				FileOutputStream fos = new java.io.FileOutputStream(outputFile);

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
	
	public static void copyNativeLibFiles(InputStream jarfile, File outfolder)
	{
		if (!outfolder.exists())
		{
			outfolder.mkdir();
		}

		try
		{
			JarInputStream jarstream = new JarInputStream(jarfile);
			JarEntry filejarentry = jarstream.getNextJarEntry();

			// Simply step through the JAR containing the library files and extract only the code files.
			// These are copied to the source output folder.
			while (filejarentry != null)
			{
				if(!filejarentry.getName().contains("src/main") ||
						filejarentry.getName().contains("META") || filejarentry.isDirectory())
				{
					filejarentry = jarstream.getNextJarEntry();
					continue;
				}

				File outputFile = new File(outfolder.toString()
						+ File.separator
						+ filejarentry.getName().replace("src/main/", ""));

				if (filejarentry.getName().contains("ProjectCMakeLists") ||
						filejarentry.getName().contains("main.c") ||
						filejarentry.getName().contains("README"))
				{
					outputFile = new File(outputFile.getAbsolutePath().replace("nativelib"
							+ File.separator, ""));
				}
				
				if(outputFile.getParentFile() != null)
				{
					outputFile.getParentFile().mkdirs();
				}

				FileOutputStream fos = new java.io.FileOutputStream(outputFile);

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

	public static AExternalExpIR consCNull()
	{
		AExternalExpIR cNull = new AExternalExpIR();
		cNull.setType(new AUnknownTypeIR());
		cNull.setTargetLangExp(C_NULL);

		return  cNull;
	}
}
