package org.overture.codegen.vdm2c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.overture.codegen.vdm2c.analysis.ClassAssocAnalysis;
import org.overture.codegen.vdm2c.analysis.FeatureAnalysisResult;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.sourceformat.ISourceFileFormatter;
import org.overture.codegen.vdm2c.transformations.AddFieldTrans;
import org.overture.codegen.vdm2c.utils.CTransUtil;

public class CGen extends CodeGenBase
{
	private List<File> emittedFiles = new LinkedList<>();
	private ISourceFileFormatter formatter;
	
	public static Map<String, Boolean> hasTimeMap = null;

	private CGenSettings cGenSettings;
	
	private FeatureAnalysisResult featureAnalysis;

	private String classAssocArray;
	
	private List<String> headers = new LinkedList<>();
	
	public static final String FEATURE_FILE_NAME = "VdmModelFeatures.h";
	public static final String CLASS_ASSOC_FILE_NAME = "VdmClassHierarchy.h";
	
	public CGen()
	{
		this.cGenSettings = new CGenSettings();
	}
	
	public CGenSettings getCGenSettings()
	{
		return cGenSettings;
	}

	public List<String> getHeaders()
	{
		return headers;
	}
	
	@Override
	protected void preProcessAst(List<org.overture.ast.node.INode> ast)
			throws AnalysisException
	{
		super.preProcessAst(ast);
		hasTimeMap = TimeFinder.computeTimeMap(getClasses(ast));
		featureAnalysis = FeatureAnalysisResult.runAnalysis(getClasses(ast), cGenSettings.usesGarbageCollection());
		classAssocArray = ClassAssocAnalysis.runAnalysis(getClasses(ast));
	}
	
	public List<File> getEmittedFiles()
	{
		return emittedFiles;
	}
	
	public FeatureAnalysisResult getFeatureAnalysisResult()
	{
		return featureAnalysis;
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
				
				AMethodDeclIR defCtor = new AMethodDeclIR();
				defCtor.setIsConstructor(true);
				defCtor.setAbstract(false);
				defCtor.setAccess(IRConstants.PUBLIC);
				defCtor.setName(recClass.getName());
				defCtor.setMethodType(ctorMethodType.clone());
				defCtor.setBody(new ABlockStmIR());
				
				recClass.getMethods().add(defCtor);

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
			try {
				genModules.add(genIrModule(my_formatter.GetMergeVisitor(), status));

			} catch (org.overture.codegen.ir.analysis.AnalysisException e) {
				log.error("Error generating code for class " + status.getIrNodeName() + ": " + e.getMessage());
				log.error("Skipping class..");
				e.printStackTrace();
			}
		}
		
		filterHeaders(genModules);
		
		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);

		return data;
	}

	private void filterHeaders(List<GeneratedModule> genModules) {

		List<GeneratedModule> headersToRemove = new LinkedList<>();
		
		for(GeneratedModule g : genModules)
		{
			if(g.getIrNode() instanceof AClassHeaderDeclIR)
			{
				for(GeneratedModule o : genModules)
				{
					if(g != o && g.getName().equals(o.getName()))
					{
						// 'o' must be the C source
						if(o.hasUnsupportedIrNodes() || o.hasMergeErrors() || o.hasUnsupportedTargLangNodes())
						{
							headersToRemove.add(g);
						}
					}
				}
			}
		}
		
		genModules.removeAll(headersToRemove);
	}

	private List<IRStatus<PIR>> ignoreVDMUnitTests(
			List<IRStatus<PIR>> statuses)
	{
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

		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			for (IRStatus<ADefaultClassDeclIR> status : IRStatus.extract(statuses, ADefaultClassDeclIR.class))
			{
				try
				{
					generator.applyPartialTransformation(status, trans);
				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
				{
					log.error("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage() + ". Skipping class..");
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

	protected void generateClassHeaders(final List<IRStatus<PIR>> statuses)
	{
		try
		{
			Collection<? extends IRStatus<PIR>> classHeaders = new ClassHeaderGenerator().generateClassHeaders(IRStatus.extract(statuses, ADefaultClassDeclIR.class));
			
			for (IRStatus<PIR> h : classHeaders) {
				headers.add(h.getIrNodeName());
			}
			
			statuses.addAll(classHeaders);
		} catch (org.overture.codegen.ir.analysis.AnalysisException e)
		{
			log.error("Could not generate class headers: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String getFileExtension(GeneratedModule generatedClass) {
		String extension;

		if (generatedClass.getIrNode() instanceof AClassHeaderDeclIR) {
			extension = "h";
		} else {
			extension = "c";
		}
		return extension;
	}
	
	public void genCSourceFiles(File root,
			List<GeneratedModule> generatedClasses) throws Exception, IOException
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			if (classCg.canBeGenerated())
			{
				writeFile(classCg, root);
			}
		}
	}

	//TODO: PVJ use the emitCode method
	public void writeFile(GeneratedModule module, File output_dir)
					throws org.overture.codegen.ir.analysis.AnalysisException,
					IOException
	{
		output_dir.mkdirs();

		String fileName = module.getName() + "."  + getFileExtension(module);

		String content = module.getContent();

		writeFile(output_dir, fileName, content);
	}

	private void writeFile(File output_dir, String fileName, String content)
			throws IOException
	{
		output_dir.mkdirs();
		
		File file = new File(output_dir, fileName);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));

		emittedFiles.add(new File(fileName));
		output.write(content);
		output.close();

		if(formatter!=null)
		{
			formatter.format(file);
		}
	}

	public void emitClassAssocFile(File outputDir, String classAssocFile) throws Exception
	{
		if(classAssocArray != null)
		{
			writeFile(outputDir, classAssocFile, classAssocArray);
		}
		else
		{
			throw new Exception("No feature analysis result found! Did you run the code generator?");
		}
	}
	
	public void emitFeatureFile(File outputDir, String featureFileName) throws Exception
	{
		if(featureAnalysis != null)
		{
			writeFile(outputDir, featureFileName, featureAnalysis.toString());
		}
		else
		{
			throw new Exception("No feature analysis result found! Did you run the code generator?");
		}
	}

	public void setSourceCodeFormatter(ISourceFileFormatter formatter)
	{
		this.formatter = formatter;
	}
}