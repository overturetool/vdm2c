package org.overture.codegen.vdm2c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclCG;

public class CGen extends CodeGenBase
{
	final File outputFolder;

	public CGen(File outputFolder)
	{
		this.outputFolder = outputFolder;
	}

	public GeneratedData generateCFromVdm(final List<IRStatus<PCG>> statuses,
			File outputFolder) throws AnalysisException
	{
		List<IRStatus<AModuleDeclCG>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclCG.class);
		List<IRStatus<PCG>> modulesAsNodes = IRStatus.extract(moduleStatuses);

		List<IRStatus<ADefaultClassDeclCG>> classStatuses = IRStatus.extract(modulesAsNodes, ADefaultClassDeclCG.class);
		classStatuses.addAll(IRStatus.extract(statuses, ADefaultClassDeclCG.class));
		List<ADefaultClassDeclCG> classes = getClassDecls(classStatuses);
		FuncValAssistant functionValueAssistant = new FuncValAssistant();

		// Transform IR
		CTransSeries xTransSeries = new CTransSeries(this);
		List<DepthFirstAnalysisAdaptor> transformations = xTransSeries.consAnalyses(classes, functionValueAssistant);

		// Generate IR to syntax (generate code)

		GeneratedData data = new GeneratedData();

		CFormat my_formatter = new CFormat(generator.getIRInfo(), new IHeaderFinder()
		{

			@Override
			public AClassHeaderDeclCG getHeader(SClassDeclCG def)
			{
				for (IRStatus<PCG> irStatus : statuses)
				{
					if (irStatus.getIrNode() instanceof AClassHeaderDeclCG)
					{
						AClassHeaderDeclCG header = (AClassHeaderDeclCG) irStatus.getIrNode();
						if (header.getOriginalDef() == def)
						{
							return header;
						}
					}
				}
				return null;
			}
		});

		// TODO: what is this?
		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(statuses, ADefaultClassDeclCG.class))
			{
				try
				{
					if (!getInfo().getDeclAssistant().isLibraryName(status.getIrNodeName()))
					{
						generator.applyPartialTransformation(status, trans);
					}

				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage());
					Logger.getLog().printErrorln("Skipping class..");
					e.printStackTrace();
				}
			}
		}

		generateClassHeaders(statuses);

		VTableGenerator.generate(IRStatus.extract(statuses, AClassHeaderDeclCG.class));

		writeHeaders(outputFolder, statuses, my_formatter);

		writeClasses(outputFolder, statuses, my_formatter);

		List<GeneratedModule> generated = new LinkedList<GeneratedModule>();
		data.setClasses(generated);

		return data;
	}

	public void writeClasses(File outputFolder,
			final List<IRStatus<PCG>> statuses,
			CFormat my_formatter)
	{
		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(statuses, ADefaultClassDeclCG.class))
		{
			// StringWriter writer = new StringWriter();
			ADefaultClassDeclCG classCg = status.getIrNode();

			try
			{
				writeFile(classCg, classCg.getName(), "c", my_formatter, outputFolder);
				// printClass(classCg, my_formatter, outputFolder);
				// generateClassHeader(classCg, my_formatter, outputFolder);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	public void writeHeaders(File outputFolder,
			final List<IRStatus<PCG>> statuses,
			CFormat my_formatter)
	{
		for (IRStatus<AClassHeaderDeclCG> status : IRStatus.extract(statuses, AClassHeaderDeclCG.class))
		{
			// StringWriter writer = new StringWriter();
			AClassHeaderDeclCG header = status.getIrNode();
			try
			{
				writeFile(header, header.getName(), "h", my_formatter, outputFolder);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void generateClassHeaders(
			final List<IRStatus<PCG>> statuses)
	{
		try
		{
			statuses.addAll(new ClassHeaderGenerator().generateClassHeaders(IRStatus.extract(statuses, ADefaultClassDeclCG.class)));
		} catch (org.overture.codegen.cgast.analysis.AnalysisException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	private List<ADefaultClassDeclCG> getClassDecls(
			List<IRStatus<ADefaultClassDeclCG>> statuses)
	{
		List<ADefaultClassDeclCG> classDecls = new LinkedList<ADefaultClassDeclCG>();

		for (IRStatus<ADefaultClassDeclCG> status : statuses)
		{
			classDecls.add(status.getIrNode());
		}

		return classDecls;
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	private void writeFile(INode node, String name, String extension,
			CFormat my_formatter, File output_dir)
			throws org.overture.codegen.cgast.analysis.AnalysisException,
			IOException
	{
		StringWriter writer = emitCode(node, my_formatter);

		output_dir.mkdirs();

		String fileName = name + (extension == null ? "" : "." + extension);

		File file = new File(output_dir, fileName);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(writer.toString());
		output.close();

		// FIXME: remove once finished developing the basic stuff
		if (System.getProperty("user.name").equals("kel"))
		{
			Runtime.getRuntime().exec("/usr/local/bin/clang-format -i -style='{BreakBeforeBraces: GNU}' "
					+ file.getAbsolutePath());
		}
	}

	private StringWriter emitCode(INode node, CFormat my_formatter)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(my_formatter.GetMergeVisitor(), writer);// Why StringWriter?
		return writer;
	}

	@Override
	protected GeneratedData genVdmToTargetLang(
			List<IRStatus<PCG>> irStatus) throws AnalysisException
	{
		return generateCFromVdm(irStatus, outputFolder);
	}

	/**
	 * Generic filter method for AST lists. It works both up and down.
	 * 
	 * @param ast
	 *            the list of input objects
	 * @param clz
	 *            the class which the returned list should be of
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> filter(List ast, Class<T> clz)
	{
		List<T> filtered = new Vector<T>();
		for (Object t : ast)
		{
			if (clz.isAssignableFrom(t.getClass()))
			{
				filtered.add((T) t);
			}
		}
		return filtered;

	}

}