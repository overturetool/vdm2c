package org.overture.codegen.vdm2c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.STypeIR;
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
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2c.analysis.ClassAssocAnalysis;
import org.overture.codegen.vdm2c.distribution.CDistTransSeries;
import org.overture.codegen.vdm2c.distribution.DistCGenUtil;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.analysis.FeatureAnalysisResult;
import org.overture.codegen.vdm2c.ast.CGenClonableString;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.ADistCallDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.ADistRecordCallDeclIR;
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
	
	private List<String> rec_names = new LinkedList<>();

	public static final String FEATURE_FILE_NAME = "VdmModelFeatures.h";

	public static final String CLASS_ASSOC_FILE_NAME = "VdmClassHierarchy.h";

	private boolean distGen = false;

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
		classAssocArray = ClassAssocAnalysis.runAnalysis(getClasses(ast), getInfo().getDeclAssistant());
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
				rec_names.add(recDecl.getName());

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

	private boolean filter(String name){

		if(name.equals("Test") ||
				name.equals("TestCase") ||
				name.equals("TestSuite") ||
				name.equals("TestListener") ||
				name.equals("TestResult") ||
				name.equals("TestRunner") ||
				name.equals("Throwable") ||
				name.equals("Error") ||
				name.equals("AssertionFailedError") ||
				name.equals("Assert")||
				name.equals("IO"))
			return false;


		return true;
	}

	@Override
	protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses)
			throws AnalysisException
	{
		List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();

		SystemArchitectureAnalysis.setDistFlag(statuses, this);

		Boolean dist_gen = getDistGen() && this.getCGenSettings().usesDistributionCG();

		if(dist_gen){
			// Architecture Analysis
			SystemArchitectureAnalysis sysAnalysis = new SystemArchitectureAnalysis();
			sysAnalysis.analyseSystem(statuses);
		}

		statuses = replaceSystemClassWithClass(statuses);

		if(dist_gen){
			//1. Add individual system definition pr. CPU
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
					else if(filter(r.getIrNode().getName())){
						//System.out.println(r.getIrNode().getName());
						SystemArchitectureAnalysis.systemClasses.add(r.getIrNodeName().toString());
					}
				}
			}

			//2. Add initialisation function for each CPU
			SystemArchitectureAnalysis.addCpuInitMethod(statuses);
		}

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

		genDistCalls(canBeGenerated, IRStatus.extract(canBeGenerated, ADefaultClassDeclIR.class));
		
		// Distribution Transformations
		if(dist_gen){
			genDistRecordCalls(canBeGenerated, IRStatus.extract(canBeGenerated, ADefaultClassDeclIR.class));
			applyDistTransformations(canBeGenerated);
		}

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

	private void applyDistTransformations(final List<IRStatus<PIR>> statuses)
	{
		List<DepthFirstAnalysisAdaptor> transformations = new CDistTransSeries(this).consAnalyses();

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
		writeFile(module, output_dir, module.getName());
	}

	public void writeFile(GeneratedModule module, File output_dir, String name)
			throws org.overture.codegen.ir.analysis.AnalysisException,
			IOException
	{
		output_dir.mkdirs();

		String fileName = name + "."  + getFileExtension(module);

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


	public void emitDistCode(GeneratedData data, File outputDir) throws Exception {
		for (String cpuName : SystemArchitectureAnalysis.distributionMap.keySet()) {

			CGen cGen = this;

			File outputD = new File(outputDir.getAbsolutePath() + "/" + cpuName);

			cGen.emitFeatureFile(outputD, CGen.FEATURE_FILE_NAME);
			cGen.emitClassAssocFile(outputD, CGen.CLASS_ASSOC_FILE_NAME);

			for (GeneratedModule generatedClass : data.getClasses()) {

				org.overture.codegen.ir.INode node = generatedClass.getIrNode();

				if (node instanceof ADefaultClassDeclIR) {
					ADefaultClassDeclIR st = (ADefaultClassDeclIR) node;

					Object tag = st.getTag();

					if (tag != null)
						if (st.getTag().equals(cpuName))
							cGen.writeFile(generatedClass, outputD, st.getName());

					if (!(SystemArchitectureAnalysis.distributionMap.keySet().contains(st.toString()))
							&& !(SystemArchitectureAnalysis.systemName.equals(st.toString())))
						cGen.writeFile(generatedClass, outputD);

				} else if(node instanceof AClassHeaderDeclIR) {

					AClassHeaderDeclIR st = (AClassHeaderDeclIR) node;

					Object tag = st.getTag();

					if (tag != null)
						if (st.getTag().equals(cpuName))
							cGen.writeFile(generatedClass, outputD, st.getName());

					if (!(SystemArchitectureAnalysis.distributionMap.keySet().contains(st.toString()))
							&& !(SystemArchitectureAnalysis.systemName.equals(st.toString())))
						cGen.writeFile(generatedClass, outputD);
				}
				else if (node instanceof ADistCallDeclIR){
					ADistCallDeclIR st = (ADistCallDeclIR) node;
					//cGen.writeFile(generatedClass, outputD);
					
					String bus = st.getTag().toString();
					
					if(SystemArchitectureAnalysis.connectionMapStr.get(bus).contains(cpuName)){
						if(st.getIsHeader())
							writeFile(outputD, bus + ".h", generatedClass.getContent());
						else
							writeFile(outputD, bus + ".c", generatedClass.getContent());
					}
				}
				else if (node instanceof ADistRecordCallDeclIR){
					ADistRecordCallDeclIR st = (ADistRecordCallDeclIR) node;
					
					if(st.getIsHeader()) writeFile(outputD, "records" + ".h", generatedClass.getContent());
					else writeFile(outputD, "records" + ".c", generatedClass.getContent());
				}
			}

			//			for(String bus : SystemArchitectureAnalysis.connectionMapStr.keySet()){
			//				for(String currCPU : SystemArchitectureAnalysis.connectionMapStr.get(bus).contains("cpu")){
			//					
			//				}
			//			}

		}
	}

	private void genDistRecordCalls(List<IRStatus<PIR>> canBeGenerated, List<IRStatus<ADefaultClassDeclIR>> classes) {

		if (rec_names.isEmpty()) return;
		ADistRecordCallDeclIR distCall1 = new ADistRecordCallDeclIR();
		//ADistRecordCallDeclIR distCall2 = new ADistRecordCallDeclIR();

		if(!classes.isEmpty()) {
			IRStatus<ADefaultClassDeclIR> c = classes.get(0);

			distCall1.setSourceNode(c.getIrNode().getSourceNode());
			distCall1.setIsHeader(false);			
			for(String recName : rec_names){
				// Create a new method: 
				AMethodDeclIR m = new AMethodDeclIR();

				m.setAsync(false);
				m.setAbstract(false);
				m.setAccess("public");
				m.setImplicit(false);
				m.setIsConstructor(false);
				m.setStatic(false);
				m.setName(recName);
				
				distCall1.getRecNames().add(m);
			}
			
			ADistRecordCallDeclIR distCall2 = distCall1.clone();
			distCall2.setIsHeader(true);
			//CGenClonableString rt = new CGenClonableString("Hej");
			//CGenClonableString rt2 = new CGenClonableString("Hello");

			
			//distCall1.getRecNames().add(rt);
			
				String bus = "recordss";
				// Create C file
				//ADistCallDeclIR distCall = genDistCall(c.getIrNode(), bus + "CG", false);
				String nodeName = bus;// +// "_dist";
				distCall1.setTag(bus);
				IRStatus irStatus = new IRStatus(c.getVdmNode(), nodeName, distCall1, c.getUnsupportedInIr(), c.getTransformationWarnings());
				canBeGenerated.add(irStatus);

				// Create H file
				//distCall = genDistCall(c.getIrNode(), bus + "CG", true);
				distCall2.setTag(bus);
				irStatus = new IRStatus(c.getVdmNode(), nodeName, distCall2, c.getUnsupportedInIr(), c.getTransformationWarnings());
				canBeGenerated.add(irStatus);

		}
	}
	
	private void genDistCalls(List<IRStatus<PIR>> canBeGenerated, List<IRStatus<ADefaultClassDeclIR>> classes) {

		if(!classes.isEmpty()) {
			IRStatus<ADefaultClassDeclIR> c = classes.get(0);

			for (String bus : SystemArchitectureAnalysis.connectionMapStr.keySet()) {

				// Create C file
				ADistCallDeclIR distCall = genDistCall(c.getIrNode(), bus + "CG", false);
				String nodeName = bus;// +// "_dist";
				distCall.setTag(bus);
				IRStatus irStatus = new IRStatus(c.getVdmNode(), nodeName, distCall, c.getUnsupportedInIr(), c.getTransformationWarnings());
				canBeGenerated.add(irStatus);

				// Create H file
				distCall = genDistCall(c.getIrNode(), bus + "CG", true);
				distCall.setTag(bus);
				irStatus = new IRStatus(c.getVdmNode(), nodeName, distCall, c.getUnsupportedInIr(), c.getTransformationWarnings());
				canBeGenerated.add(irStatus);
			}
		}
	}

	private ADistCallDeclIR genDistCall(ADefaultClassDeclIR c, String name, Boolean isHeader) {

		ADistCallDeclIR distCall = new ADistCallDeclIR();
		distCall.setIsHeader(isHeader);
		distCall.setSourceNode(c.getSourceNode());

		// Create a new method: 
		AMethodDeclIR m = new AMethodDeclIR();

		m.setAsync(false);
		m.setAbstract(false);
		m.setAccess("public");
		m.setImplicit(false);
		m.setIsConstructor(false);
		m.setStatic(false);
		m.setName(name);

		LinkedList<AFormalParamLocalParamIR> par = new LinkedList<AFormalParamLocalParamIR>();

		// Add method arguments
		AFormalParamLocalParamIR par1 = DistCGenUtil.addMethodParameter("objID", "int");
		par.add(par1);

		AFormalParamLocalParamIR par2 = DistCGenUtil.addMethodParameter("funID", "int");
		par.add(par2);

		AFormalParamLocalParamIR par3 = DistCGenUtil.addMethodParameter("supID", "int");
		par.add(par3);

		AFormalParamLocalParamIR par4 = DistCGenUtil.addMethodParameter("nrArgs", "int");
		par.add(par4);

		AFormalParamLocalParamIR par5 = DistCGenUtil.addMethodParameter("", "...");
		par.add(par5);

		// The method return type
		AExternalTypeIR tyRet = new AExternalTypeIR();
		tyRet.setName("TVP");

		AMethodTypeIR mTy = new AMethodTypeIR();

		LinkedList<STypeIR> paramsTy = new LinkedList<STypeIR>();
		paramsTy.add(new AIntNumericBasicTypeIR());
		paramsTy.add(new AIntNumericBasicTypeIR());

		mTy.setParams(paramsTy);
		mTy.setResult(tyRet);

		m.setMethodType(mTy.clone());
		m.setFormalParams(par);

		// New method body
		ABlockStmIR body = new ABlockStmIR();
		body.setScoped(false);	

		m.setBody(body);

		if(isHeader)
			m.setBody(null);

		//distCall.cl
		distCall.getMethods().add(m);

		//		for(AMethodDeclIR m : c.getMethods())
		//		{
		//			distCall.getMethods().add(m.clone());
		//		}

		return distCall;
	}


	public void setSourceCodeFormatter(ISourceFileFormatter formatter)
	{
		this.formatter = formatter;
	}

	public void setDistGen(boolean distGen)
	{
		this.distGen = distGen;
	}

	public boolean getDistGen()
	{
		return this.distGen;
	}
}