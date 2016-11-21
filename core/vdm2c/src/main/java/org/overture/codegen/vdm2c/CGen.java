package org.overture.codegen.vdm2c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.time.StopWatch;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.ir.CodeGenBase;
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
		List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();
		
		statuses = replaceSystemClassWithClass(statuses);
		statuses = ignoreVDMUnitTests(statuses);
		List<IRStatus<PIR>> recClasses = makeRecsOuterClasses(statuses);
		statuses.addAll(recClasses);
		
		for(IRStatus<ADefaultClassDeclIR> r : IRStatus.extract(recClasses, ADefaultClassDeclIR.class))
		{
			getInfo().getClasses().add(r.getIrNode());
		}
		
		List<IRStatus<PIR>> canBeGenerated = new LinkedList<>();
		
		for (IRStatus<PIR> status : statuses)
		{
			if (status.canBeGenerated())
			{
				canBeGenerated.add(status);
			} else
			{
				genModules.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status)));
			}
		}

		for(IRStatus<ADefaultClassDeclIR> r : IRStatus.extract(canBeGenerated, ADefaultClassDeclIR.class))
		{
			try
			{
				generator.applyPartialTransformation(r, new AddFieldTrans(transAssistant));
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				e.printStackTrace();
			}
		}
		
		generateClassHeaders(canBeGenerated);
		applyTransformations(canBeGenerated);

		VTableGenerator.generate(IRStatus.extract(canBeGenerated, AClassHeaderDeclIR.class));

		CFormat my_formatter = consFormatter(canBeGenerated);
		
		
		for (IRStatus<PIR> status : canBeGenerated)
		{
			org.overture.ast.node.INode vdmClass = status.getVdmNode();

			try
			{
				if (shouldGenerateVdmNode(vdmClass))
				{
					genModules.add(genIrModule(my_formatter.GetMergeVisitor(), status));

				}

			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				log.error("Error generating code for class "
						+ status.getIrNodeName() + ": " + e.getMessage());
				log.error("Skipping class..");
				e.printStackTrace();
			}
		}
		
		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);

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

	//TODO: PVJ use the emitCode method
	public void writeFile(GeneratedModule module, String extension, File output_dir)
					throws org.overture.codegen.ir.analysis.AnalysisException,
					IOException
	{
		output_dir.mkdirs();

		String fileName = module.getName() + (extension == null ? "" : "." + extension);

		File file = new File(output_dir, fileName);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(module.getContent());
		output.close();

		if(formatter!=null)
		{
			formatter.format(file);
		}
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