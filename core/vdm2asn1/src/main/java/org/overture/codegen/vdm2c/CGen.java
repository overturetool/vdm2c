package org.overture.codegen.vdm2c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
//import org.overture.codegen.vdm2c.sourceformat.ISourceFileFormatter;

public class CGen extends CodeGenBase
{
	private List<File> emittedFiles = new LinkedList<>();
//	private ISourceFileFormatter formatter;

	public static Map<String, Boolean> hasTimeMap = null;

	public CGen()
	{
	}

	@Override
	protected void preProcessAst(List<org.overture.ast.node.INode> ast)
			throws AnalysisException
	{
		super.preProcessAst(ast);
	}

	public List<File> getEmittedFiles()
	{
		return emittedFiles;
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

		Boolean dist_gen = CGenMain.distGen;

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

		//generateClassHeaders(canBeGenerated);
		applyTransformations(canBeGenerated);

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

		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);

		return data;
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
		CFormat my_formatter = new CFormat(generator.getIRInfo());
		return my_formatter;
	}

	public String getFileExtension(GeneratedModule generatedClass) {
		String extension;

		if (generatedClass.getIrNode() instanceof AClassHeaderDeclIR) {
			extension = "h";
		} else {
			extension = "ans";
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

		File file = new File(output_dir, fileName);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));

		emittedFiles.add(new File(fileName));
		output.write(module.getContent());
		output.close();

//		if(formatter!=null)
//		{
//			formatter.format(file);
//		}
	}


//	public void setSourceCodeFormatter(ISourceFileFormatter formatter)
//	{
//		this.formatter = formatter;
//	}
}