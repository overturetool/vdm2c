package org.overture.codegen.vdm2c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.time.StopWatch;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2c.distribution.CDistTransSeries;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.sourceformat.ISourceFileFormatter;
import org.overture.codegen.vdm2c.transformations.AddFieldTrans;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGen extends CodeGenBase
{
	final static Logger logger = LoggerFactory.getLogger(CGen.class);
	final File outputFolder;
	private ISourceFileFormatter formatter;

	public static Map<String, Boolean> hasTimeMap = null;

	public CGen(File outputFolder)
	{
		this.outputFolder = outputFolder;
	}

	@Override
	protected void preProcessAst(List<org.overture.ast.node.INode> ast)
			throws AnalysisException
	{
		super.preProcessAst(ast);
		hasTimeMap = TimeFinder.computeTimeMap(getClasses(ast));
	}

	public List<IRStatus<PIR>> makeRecsOuterClasses(List<IRStatus<PIR>> ast)
	{
		List<IRStatus<PIR>> extraClasses = new LinkedList<IRStatus<PIR>>();

		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(ast, ADefaultClassDeclIR.class))
		{
			ADefaultClassDeclIR clazz = status.getIrNode();

			List<ARecordDeclIR> recDecls = new LinkedList<ARecordDeclIR>();

			for (ATypeDeclIR d : clazz.getTypeDecls())
			{
				if (d.getDecl() instanceof ARecordDeclIR)
				{
					recDecls.add((ARecordDeclIR) d.getDecl());
				}
			}

			// Note that we do not remove the type declarations (or records) from the class.

			// For each of the records we will make a top-level class
			for (ARecordDeclIR recDecl : recDecls)
			{
				ADefaultClassDeclIR recClass = new ADefaultClassDeclIR();

				recClass.setMetaData(recDecl.getMetaData());
				recClass.setAbstract(false);
				recClass.setAccess(IRConstants.PUBLIC);
				recClass.setSourceNode(recDecl.getSourceNode());
				recClass.setStatic(false);
				recClass.setName(recDecl.getName());

				if (recDecl.getInvariant() != null)
				{
					recClass.setInvariant(recDecl.getInvariant().clone());
				}

				// Copy the record fields to the class
				List<AFieldDeclIR> fields = new LinkedList<AFieldDeclIR>();
				for (AFieldDeclIR f : recDecl.getFields())
				{
					AFieldDeclIR newField = f.clone();
					fields.add(newField);
				}

				recClass.setFields(fields);

				AClassTypeIR retType = new AClassTypeIR();
				retType.setName(recClass.getName());

				AMethodTypeIR ctorMethodType = new AMethodTypeIR();
				ctorMethodType.setResult(retType);

				ABlockStmIR ctorBody = new ABlockStmIR();

				AMethodDeclIR ctor = new AMethodDeclIR();
				ctor.setIsConstructor(true);
				ctor.setAbstract(false);
				ctor.setAccess(IRConstants.PUBLIC);
				ctor.setName(recClass.getName());
				ctor.setBody(ctorBody);


				ctor.setMethodType(ctorMethodType);

				for(AFieldDeclIR f : recClass.getFields())
				{
					AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
					String name = "param_" + f.getName();
					param.setPattern(CTransUtil.newIdentifierPattern(name));
					param.setType(f.getType().clone());

					ctor.getFormalParams().add(param);
					ctorMethodType.getParams().add(f.getType().clone());

					AIdentifierVarExpIR target = CTransUtil.newIdentifier(f.getName(), f.getSourceNode());
					target.setType(f.getType().clone());
					target.setIsLocal(false);

					AIdentifierVarExpIR assignVal = CTransUtil.newIdentifier(name, f.getSourceNode());
					assignVal.setType(f.getType().clone());

					ctorBody.getStatements().add(CTransUtil.newAssignment(
							target, assignVal));	
				}

				recClass.getMethods().add(ctor);

				extraClasses.add(new IRStatus<PIR>(recClass.getSourceNode().getVdmNode(), recClass.getName(), recClass, new HashSet<VdmNodeInfo>()));
			}
		}

		return extraClasses;
	}

	@Override
	protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses)
			throws AnalysisException
	{

		/** Distribution Analysis **/
		SystemArchitectureAnalysis sysAnalysis = new SystemArchitectureAnalysis();

		sysAnalysis.analyseSystem(statuses);

		sysAnalysis.generateDM();

		Map<String, LinkedList<Boolean>> dm = SystemArchitectureAnalysis.DM;

		statuses = replaceSystemClassWithClass(statuses);

		// Add individual system definition pr. CPU
		for (IRStatus<ADefaultClassDeclIR> r : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
		{
			for(String cpuName : SystemArchitectureAnalysis.distributionMap.keySet()){				
				if (r.getIrNode().getName().equals(SystemArchitectureAnalysis.systemName))
				{
					ADefaultClassDeclIR dep = r.getIrNode().clone();
					dep.setTag(cpuName);
					IRStatus<PIR> stat = new IRStatus<PIR>(null, cpuName, dep, new HashSet<VdmNodeInfo>(), new HashSet<IrNodeInfo>());
					statuses.add(stat);
				}
			}
		}


		statuses = ignoreVDMUnitTests(statuses);
		List<IRStatus<PIR>> recClasses = makeRecsOuterClasses(statuses);
		statuses.addAll(recClasses);

		for (IRStatus<ADefaultClassDeclIR> r : IRStatus.extract(recClasses, ADefaultClassDeclIR.class))
			statuses = replaceSystemClassWithClass(statuses);
		//statuses = ignoreVDMUnitTests(statuses);
		//List<IRStatus<PIR>> recClasses = makeRecsOuterClasses(statuses);
		//statuses.addAll(recClasses);

		for(IRStatus<ADefaultClassDeclIR> r : IRStatus.extract(recClasses, ADefaultClassDeclIR.class))
		{
			getInfo().getClasses().add(r.getIrNode());
		}

		for(IRStatus<ADefaultClassDeclIR> r : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
		{
			try
			{
				generator.applyPartialTransformation(r, new AddFieldTrans(transAssistant));
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				e.printStackTrace();
			}
		}

		generateClassHeaders(statuses);
		applyTransformations(statuses);

		VTableGenerator.generate(IRStatus.extract(statuses, AClassHeaderDeclIR.class));

		CFormat my_formatter = consFormatter(statuses);
		writeHeaders(outputFolder, statuses, my_formatter);
		writeClasses(outputFolder, statuses, my_formatter);

		/** Distribution Transformations **/
		applyDistTransformations(statuses);

		/** Create a new folder for each CPU **/
		for (String cpuName : SystemArchitectureAnalysis.distributionMap.keySet())
		{
			File outputDir = new File(outputFolder.getName() + "/" + cpuName);

			// Print the unique system class representation

			List<IRStatus<PIR>> printStatuses = new LinkedList<IRStatus<PIR>>();

			for(IRStatus<PIR> st: statuses){
				if(st.getIrNodeName().equals(cpuName))
					printStatuses.add(st);
				if(!(SystemArchitectureAnalysis.distributionMap.keySet().contains(st.getIrNodeName())) && 
						!(SystemArchitectureAnalysis.systemName.equals(st.getIrNodeName())))
					printStatuses.add(st);
			}

			writeHeaders(outputDir, printStatuses, my_formatter);
			writeClasses(outputDir, printStatuses, my_formatter);
		}

		/**
		 * FIXME: PVJ: This method does not return the generated data as it should. Instead the method writes the
		 * generated code to the file system and returns an empty data structure. The date structure below is empty!
		 */
		List<GeneratedModule> generated = new LinkedList<GeneratedModule>();
		GeneratedData data = new GeneratedData();
		data.setClasses(generated);

		return data;
	}

	private List<IRStatus<PIR>> ignoreVDMUnitTests(
			List<IRStatus<PIR>> statuses)
	{
		//IRStatus<PIR> status = null;
		List<IRStatus<PIR>> newstatuses = new LinkedList<IRStatus<PIR>>();

		for (IRStatus<PIR> irStatus : statuses)
		{
			newstatuses.add(irStatus);

			//First remove all VDMUnited.vdmrt-related classes.
			if (irStatus.getIrNode() instanceof ADefaultClassDeclIR)
			{
				if(irStatus.getIrNodeName().equals("Test") ||
						irStatus.getIrNodeName().equals("TestCase") ||
						irStatus.getIrNodeName().equals("TestSuite") ||
						irStatus.getIrNodeName().equals("TestListener") ||
						irStatus.getIrNodeName().equals("TestResult") ||
						irStatus.getIrNodeName().equals("TestRunner") ||
						irStatus.getIrNodeName().equals("Throwable") ||
						irStatus.getIrNodeName().equals("Error") ||
						irStatus.getIrNodeName().equals("AssertionFailedError") ||
						irStatus.getIrNodeName().equals("Assert"))
				{
					newstatuses.remove(irStatus);
					continue;
				}

				//Next remove all test cases.
				if(!((ADefaultClassDeclIR)irStatus.getIrNode()).getSuperNames().isEmpty())
				{
					for(ATokenNameIR i : ((ADefaultClassDeclIR)irStatus.getIrNode()).getSuperNames())
					{
						if(i.getName().equals("TestCase"))
						{
							newstatuses.remove(irStatus);		
						}
					}
					continue;
				}
			}
		}

		return newstatuses;
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
			cDef.setSourceNode(systemDef.getSourceNode());
			cDef.setName(systemDef.getName());
			for (AFieldDeclIR f : systemDef.getFields())
			{
				if (f.getType() instanceof AClassTypeIR)
				{
					AClassTypeIR type = (AClassTypeIR) f.getType();
					if (type.getName().equals("CPU")
							|| type.getName().equals("BUS"))
					{
						continue;
					}
				}

				cDef.getFields().add(f.clone());
			}

			for(AMethodDeclIR i : systemDef.getMethods())
			{
				cDef.getMethods().add(i.clone());
			}

			status.setIrNode(cDef);
		}

		return statuses;
	}

	private void applyTransformations(final List<IRStatus<PIR>> statuses)
	{
		List<DepthFirstAnalysisAdaptor> transformations = new CTransSeries(this).consAnalyses();

		final StopWatch stopwatch = new StopWatch();
		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			logger.debug("Applying transformation: {}", trans.getClass().getSimpleName());
			stopwatch.reset();
			stopwatch.start();
			for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
			{
				try
				{
					generator.applyPartialTransformation(status, trans);
				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
				{
					logger.error("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage() + ". Skipping class..");
					e.printStackTrace();
				}
			}
			stopwatch.stop();
			logger.debug("Completed transformation: {}. Time elapsed: {}", trans.getClass().getSimpleName(), stopwatch);
		}
	}

	private void applyDistTransformations(final List<IRStatus<PIR>> statuses)
	{
		List<DepthFirstAnalysisAdaptor> transformations = new CDistTransSeries(this).consAnalyses();

		final StopWatch stopwatch = new StopWatch();
		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			logger.debug("Applying transformation: {}", trans.getClass().getSimpleName());
			stopwatch.reset();
			stopwatch.start();
			for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
			{
				try
				{
					generator.applyPartialTransformation(status, trans);
				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
				{
					logger.error("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage()
							+ ". Skipping class..");
					e.printStackTrace();
				}
			}
			stopwatch.stop();
			logger.debug("Completed transformation: {}. Time elapsed: {}", trans.getClass().getSimpleName(), stopwatch);
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
		logger.debug("Writing C source files");
		final StopWatch stopwatch = new StopWatch();
		final StopWatch stopwatchClass = new StopWatch();
		stopwatch.start();
		for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
		{
			// StringWriter writer = new StringWriter();
			ADefaultClassDeclIR classCg = status.getIrNode();

			try
			{
				stopwatchClass.reset();
				stopwatchClass.start();
				// logger.trace("Emitting code and writing: {}", classCg.getName());
				writeFile(classCg, classCg.getName(), "c", my_formatter, outputFolder);
				stopwatchClass.stop();
				logger.trace("Emitted code for class: {} in: {}", classCg.getName(), stopwatchClass);
			} catch (org.overture.codegen.ir.analysis.AnalysisException e1)
			{
				e1.printStackTrace();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}

		}
		stopwatch.stop();
		logger.debug("Writing C source files. Completed in: {}", stopwatch);
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

		if(formatter!=null)
		{
			formatter.format(file);
		}

	}

	private StringWriter emitCode(INode node, CFormat my_formatter)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		StringWriter writer = new StringWriter();
		my_formatter.GetMergeVisitor().init();
		node.apply(my_formatter.GetMergeVisitor(), writer);// Why StringWriter?
		if (my_formatter.GetMergeVisitor().hasMergeErrors())
		{
			// throw new
			// org.overture.codegen.ir.analysis.AnalysisException(my_formatter.GetMergeVisitor().getMergeErrors().get(0));
			for (Exception e : my_formatter.GetMergeVisitor().getMergeErrors())
			{
				logger.error("Merge error:", e);
			}

		}
		if (my_formatter.GetMergeVisitor().hasUnsupportedTargLangNodes())
		{
			for (IrNodeInfo n : my_formatter.GetMergeVisitor().getUnsupportedInTargLang())
			{
				logger.warn("Merge reached unsupported template: {}", n);
			}

			// throw new
			// org.overture.codegen.ir.analysis.AnalysisException(my_formatter.GetMergeVisitor().getUnsupportedInTargLang().iterator().next().toString());
		}
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

	public void setSourceCodeFormatter(ISourceFileFormatter formatter)
	{
		this.formatter = formatter;
	}
}