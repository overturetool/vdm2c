package org.overture.codegen.vdm2c;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class CMakeUtil
{
	private static String OS = System.getProperty("os.name").toLowerCase();
	final File vdmLibPath;
	final File template;

	final String TEMPLATE_PROJECT_NAME = "##PROJECT_NAME##";
	private boolean verbose = true;

	public CMakeUtil(File vdmLibPath, File template, boolean verbose)
	{
		this.vdmLibPath = vdmLibPath;
		this.template = template;
		this.verbose = verbose;
	}

	public void createTestProject(String name, File root) throws IOException
	{
		String cmakeTemplate = FileUtils.readFileToString(template, "UTF-8");
		cmakeTemplate = cmakeTemplate.replace("##PROJECT_NAME##", name);
		cmakeTemplate = cmakeTemplate.replace("## INSERT_GOOGLE_TEST_SUB_DIRECTORY ##", String.format("add_subdirectory(\"%s\" \"%s\")",new File("../../c/third_party/googletest".replace('/', File.separatorChar)).getCanonicalPath(),new File("target/google-test-build".replace('/',File.separatorChar)).getCanonicalPath()));
		FileUtils.writeStringToFile(new File(root, "CMakeLists.txt"), cmakeTemplate);

		FileUtils.copyFile(new File("src/test/resources/DownloadProject.cmake"), new File(root, "DownloadProject.cmake"));
		FileUtils.copyFile(new File("src/test/resources/DownloadProject.CMakeLists.cmake.in"), new File(root, "DownloadProject.CMakeLists.cmake.in"));
		
		
		copyReplaceProjectName(name,new File("src/test/resources/project"),new File(root,".project"));
		copyReplaceProjectName(name,new File("src/test/resources/cproject"),new File(root,".cproject"));
		
		File settings = new File(root,".settings");
		settings.mkdirs();
		FileUtils.copyFile(new File("src/test/resources/c_settings.xml"), new File(root, "language.settings.xml"));
	}
	
	void copyReplaceProjectName(String name, File source, File dest) throws IOException
	{
		String cmakeTemplate = FileUtils.readFileToString(source, "UTF-8");
		cmakeTemplate = cmakeTemplate.replace("##PROJECT_NAME##", name);
		FileUtils.writeStringToFile(dest, cmakeTemplate.trim());
	}

	public static class CMakeGenerateException extends Exception
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final String errors;

		public CMakeGenerateException(String errors)
		{
			super(errors);
			this.errors = errors;
		}
	}

	public boolean generate(File root) throws IOException,
			InterruptedException, CMakeGenerateException
	{
		String cmake = "cmake";

		if (isMac())
		{
			cmake = "/usr/local/bin/cmake";
		}

		ProcessBuilder pb = new ProcessBuilder(cmake,"-DCMAKE_BUILD_TYPE=Debug", String.format("-DVDM_LIB_PATH=%s", vdmLibPath.getAbsolutePath()), ".");
		pb.directory(root);

		return runProcess(pb, verbose);

	}

	public boolean make(File root, String... goal) throws IOException,
			InterruptedException, CMakeGenerateException
	{
		String make = "make";

		ProcessBuilder pb = new ProcessBuilder(make, "-j3");
		for (String string : goal)
		{
			pb.command().add(string);
		}
		pb.directory(root);

		return runProcess(pb, verbose);

	}

	public boolean run(File root, String cmd, boolean verbose)
			throws IOException, InterruptedException, CMakeGenerateException
	{

		String name = cmd;

		if (isWindows())
		{
			name = name + ".exe";
		} else
		{
			name = "./" + name;
		}

		ProcessBuilder pb = new ProcessBuilder(name);
		pb.directory(root);

		return runProcess(pb, verbose);

	}

	public boolean runProcess(ProcessBuilder pb, boolean verbose)
			throws IOException, InterruptedException, CMakeGenerateException
	{
		final Process p = pb.start();

		if (verbose)
		{
			Thread outThread = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));

					String tmp = null;
					try
					{
						while ((tmp = out.readLine()) != null)
						{
							System.out.println(tmp);
						}
					} catch (IOException e)
					{
						return;
					}
				}
			});

			outThread.setDaemon(true);
			outThread.start();
		}

		p.waitFor();
		
		List<String> errors = IOUtils.readLines(p.getErrorStream());

		boolean res = p.exitValue() == 0;

		if (!res)
		{
			if (!errors.isEmpty())
			{
				StringBuffer sb = new StringBuffer();
				for (String string : errors)
				{
					sb.append(string);
					sb.append("\n");
				}
				throw new CMakeGenerateException(sb.toString());
			}
		}
		return res;
	}

	public static boolean isMac()
	{

		return OS.indexOf("mac") >= 0;

	}

	public static boolean isWindows()
	{

		return OS.indexOf("win") >= 0;

	}

	public static boolean isUnix()
	{

		return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0
				|| OS.indexOf("aix") > 0;

	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}
