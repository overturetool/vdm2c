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
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;

public class CGen extends CodeGenBase
{
	final File outputFolder;

	public CGen(File outputFolder)
	{
		this.outputFolder = outputFolder;
	}

	@Override
	protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses)
			throws AnalysisException
	{
		statuses = replaceSystemClassWithClass(statuses);

		generateClassHeaders(statuses);

		applyTransformations(statuses);

		VTableGenerator.generate(IRStatus.extract(statuses, AClassHeaderDeclIR.class));

		CFormat my_formatter = consFormatter(statuses);
		writeHeaders(outputFolder, statuses, my_formatter);
		writeClasses(outputFolder, statuses, my_formatter);

		/**
		 * FIXME: PVJ: This method does not return the generated data as it should. Instead the method writes the
		 * generated code to the file system and returns an empty data structure. The date structure below is empty!
		 */
		List<GeneratedModule> generated = new LinkedList<GeneratedModule>();
		GeneratedData data = new GeneratedData();
		data.setClasses(generated);

		return data;
	}

	private List<IRStatus<PIR>> replaceSystemClassWithClass(
			List<IRStatus<PIR>> statuses)
	{
		IRStatus<PIR> status = null;
		for (IRStatus<PIR> irStatus : statuses)
		{
			if (irStatus.getIrNode() instanceof ASystemClassDeclIR)
			{
				status = irStatus;

			}
		}

		if (status != null)
		{
			ASystemClassDeclIR systemDef = (ASystemClassDeclIR) status.getIrNode();
			ADefaultClassDeclIR cDef = new ADefaultClassDeclIR();
			cDef.setName(systemDef.getName());
			for (AFieldDeclIR f : systemDef.getFields())
			{
				if (f.getType() instanceof AClassTypeIR)
				{
					AClassTypeIR type = (AClassTypeIR) f.getType();
					if (type.getName().equals("CPU")
							|| type.getName().equals("BUS"))
						continue;
				}

				// if(f.getType() instanceof abus instanceof ABusClassDeclIR || f instanceof ACpuClassDeclIR)
				// continue;
				cDef.getFields().add(f.clone());
			}
			// FIXME: add and filter the constructur for RT calls on cpus and busses

			status.setIrNode(cDef);
		}

		return statuses;
	}

	private void applyTransformations(final List<IRStatus<PIR>> statuses)
	{
		List<DepthFirstAnalysisAdaptor> transformations = new CTransSeries(this).consAnalyses();
		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
			{
				try
				{
					generator.applyPartialTransformation(status, trans);

				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage());
					Logger.getLog().printErrorln("Skipping class..");
					e.printStackTrace();
				}
			}
		}
	}

	private CFormat consFormatter(final List<IRStatus<PIR>> statuses)
	{
		CFormat my_formatter = new CFormat(generator.getIRInfo(), new IHeaderFinder()
		{
			@Override
			public AClassHeaderDeclIR getHeader(SClassDeclIR def)
			{
				for (IRStatus<PIR> irStatus : statuses)
				{
					if (irStatus.getIrNode() instanceof AClassHeaderDeclIR)
					{
						AClassHeaderDeclIR header = (AClassHeaderDeclIR) irStatus.getIrNode();
						if (header.getOriginalDef() == def)
						{
							return header;
						}
					}
				}
				return null;
			}
		});
		return my_formatter;
	}

	public void writeClasses(File outputFolder,
			final List<IRStatus<PIR>> statuses, CFormat my_formatter)
	{
		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
		{
			// StringWriter writer = new StringWriter();
			ADefaultClassDeclIR classCg = status.getIrNode();

			try
			{
				writeFile(classCg, classCg.getName(), "c", my_formatter, outputFolder);
				// printClass(classCg, my_formatter, outputFolder);
				// generateClassHeader(classCg, my_formatter, outputFolder);
			} catch (org.overture.codegen.ir.analysis.AnalysisException e1)
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
			final List<IRStatus<PIR>> statuses, CFormat my_formatter)
	{
		for (IRStatus<AClassHeaderDeclIR> status : IRStatus.extract(statuses, AClassHeaderDeclIR.class))
		{
			// StringWriter writer = new StringWriter();
			AClassHeaderDeclIR header = status.getIrNode();
			try
			{
				writeFile(header, header.getName(), "h", my_formatter, outputFolder);
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
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

	public void generateClassHeaders(final List<IRStatus<PIR>> statuses)
	{
		try
		{
			statuses.addAll(new ClassHeaderGenerator().generateClassHeaders(IRStatus.extract(statuses, ADefaultClassDeclIR.class)));
		} catch (org.overture.codegen.ir.analysis.AnalysisException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	private void writeFile(INode node, String name, String extension,
			CFormat my_formatter, File output_dir)
			throws org.overture.codegen.ir.analysis.AnalysisException,
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
		if (System.getProperty("user.name").equals("mot"))
		{
			Runtime.getRuntime().exec("clang-format -i -style='{BreakBeforeBraces:GNU}' '"
					+ file.getAbsolutePath() + "'");
		}
	}

	private StringWriter emitCode(INode node, CFormat my_formatter)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(my_formatter.GetMergeVisitor(), writer);// Why StringWriter?
		return writer;
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