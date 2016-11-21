package org.overture.codegen.vdm2c;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2c.sourceformat.ISourceFileFormatter;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class CGenMain
{
	public static boolean print = false;
	
	public static void main(String[] args)
	{
		Settings.dialect = Dialect.VDM_RT;
		
		LogManager.getRootLogger().setLevel(Level.ERROR);
		
		// create Options object
		Options options = new Options();

		// add t option
		Option verboseOpt = Option.builder("q").longOpt("quiet").desc("Do not print processing information").build();
		Option sourceOpt = Option.builder("sf").longOpt("folder").desc("Path to a source folder containing VDM files").hasArg().build();
		Option formatOpt = Option.builder("fm").longOpt("formatter").desc("Name of the formatter which should be loaded from the class path").hasArg().build();
		Option destOpt = Option.builder("dest").longOpt("destination").desc("Output directory").hasArg().required().build();
		Option helpOpt = Option.builder("h").longOpt("help").desc("Show this description").build();

		options.addOption(verboseOpt);
		options.addOption(sourceOpt);
		options.addOption(destOpt);
		options.addOption(helpOpt);
		options.addOption(formatOpt);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try
		{
			cmd = parser.parse(options, args);
		} catch (ParseException e1)
		{
			error("Parsing failed.  Reason: " + e1.getMessage());
			showHelp(options);
			return;
		}

		List<File> files = new LinkedList<File>();
		File outputDir = null;
		ISourceFileFormatter formatter = null;

		print = !cmd.hasOption(verboseOpt.getOpt());

		if (cmd.hasOption(helpOpt.getOpt()))
		{
			showHelp(options);
			return;
		}

		if (cmd.hasOption(formatOpt.getOpt()))
		{
			String formatterClassName = cmd.getOptionValue(formatOpt.getOpt());
			try
			{
				Class<?> formatterClass = Class.forName(formatterClassName);
				try
				{
					formatter = (ISourceFileFormatter) formatterClass.newInstance();
				} catch (InstantiationException e)
				{
					error(String.format("Unable to invoke default constructor for formatter '%s'", formatterClassName));
					return;
				} catch (IllegalAccessException e)
				{
					error(String.format("Unable to access class for formatter '%s'", formatterClassName));
					return;
				}
			} catch (ClassNotFoundException e)
			{
				error(String.format("Formatter '%s' not found in class path", formatterClassName));
				return;
			}
		}

		if (cmd.hasOption(sourceOpt.getOpt()))
		{
			File path = new File(cmd.getOptionValue(sourceOpt.getOpt()));

			if (!path.isDirectory())
			{
				usage(options, sourceOpt, path + " is not a directory");
			}

			List<File> filterFiles = filterFiles(GeneralUtils.getFilesRecursively(path));
			
			if(filterFiles == null || filterFiles.isEmpty())
			{
				usage(options, sourceOpt, "No VDM-RT source files found in " + path);
			}
			
			files.addAll(filterFiles);

		}

		if (cmd.hasOption(destOpt.getOpt()))
		{
			File outputPath = new File(cmd.getOptionValue(destOpt.getOpt()).replace('/', File.separatorChar));
			outputPath.mkdirs();
			if (!outputPath.isDirectory())
			{
				usage(options, destOpt, outputDir + " is not a directory");
			}
			outputDir = outputPath;

		} else
		{
			outputDir = new File("target/cgen".replace('/', File.separatorChar));

		}
		final String[] remainingArguments = cmd.getArgs();

		for (String s : remainingArguments)
		{
			File f = new File(s);
			if (f.exists() && f.isFile())
			{
				files.add(f);
			} else
			{
				error("Not a file: " + s);
				return;
			}
		}

		try
		{
			TypeCheckResult<List<SClassDefinition>> res = TypeCheckerUtil.typeCheckRt(files);

			if (!res.parserResult.errors.isEmpty())
			{
				error(res.parserResult.getErrorString());
				return;
			}

			if (!res.errors.isEmpty())
			{
				error(res.getErrorString());
				return;
			}

			List<SClassDefinition> ast = res.result;

			CGen cGen = new CGen(outputDir);
			if(formatter!=null)
			{
				cGen.setSourceCodeFormatter(formatter);
			}

			List<INode> filter = CGen.filter(ast, org.overture.ast.node.INode.class);
			
			GeneratedData data = cGen.generate(filter);
			
			println("C code generated to folder: "
					+ outputDir.getAbsolutePath());

			if (print)
			{
				for (GeneratedModule module : data.getClasses())
				{

					if (module.canBeGenerated())
					{
						println(module.getContent());
						println(module.getUnsupportedInIr());
						println(module.getMergeErrors());
						println(module.getUnsupportedInTargLang());
					}
				}
			}

		} catch (AnalysisException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void showHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("cgen", options);
	}

	public static List<File> filterFiles(List<File> files)
	{
		List<File> filtered = new LinkedList<File>();

		for (File f : files)
		{
			if (isRtFile(f))
			{
				filtered.add(f);
			}
		}

		return filtered;
	}

	private static boolean isRtFile(File f)
	{
		return f.getName().endsWith(".vdmrt") || f.getName().endsWith(".vrt");
	}

	private static void usage(Options options, Option opt, String string)
	{
		error("Error in argument: " + opt.getOpt() + " - "
				+ string);
		showHelp(options);
		System.exit(1);
	}
	
	public static void error(String msg) {
		System.err.println(msg);
	}
	
	//TODO: Update to accept strings
	public static void println(Object msg)
	{
		if(print)
		{
			System.out.println(msg);
		}
	}
}
