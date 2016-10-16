package org.overture.codegen.vdm2c.distribution;

/*
 import static org.overture.codegen.ir.CodeGenBase.EVAL_METHOD_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.INTERFACE_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.PARAM_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.TEMPLATE_TYPE_PREFIX;

 import static org.overture.codegen.ir.CodeGenBase;
 */
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.CallObjStmTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.funcvalues.FuncValPrefixes;
import org.overture.codegen.trans.patterns.PatternVarPrefixes;
import org.overture.codegen.vdm2c.CGen;
import org.overture.codegen.vdm2c.VarPrefixManager;
import org.overture.codegen.vdm2c.distribution.transformations.CallFuncMacroExpTrans;
import org.overture.codegen.vdm2c.distribution.transformations.DistTransTest;
import org.overture.codegen.vdm2c.distribution.transformations.GenerateDepObjId;

public class CDistTransSeries
{

	private CGen codeGen;

	public CDistTransSeries(CGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses()
	{
		// Data and functionality to support the transformations
		// IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		// TempVarPrefixes varPrefixes = codeGen.getTempVarPrefixes();
		// ITempVarGen nameGen = irInfo.getTempVarNameGen();
		// TraceNames traceNamePrefixes = codeGen.getTracePrefixes();
		TransAssistantIR transAssistant = codeGen.getTransAssistant();
		// IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		// Set up order of transformations
		List<DepthFirstAnalysisAdaptor> transformations = new LinkedList<DepthFirstAnalysisAdaptor>();

		/**
		 * Phase #0 - initial construction / merge of nodes<br/>
		 * - Do weeding here
		 */
		// Construct the transformations
		//transformations.add(new FuncTrans(transAssistant));
		//transformations.add(new FieldInitializerExtractorTrans(transAssistant));
		//transformations.add(new RemoveRTConstructs(transAssistant));

		// Data and functionality to support the transformations
		IRInfo info = codeGen.getIRGenerator().getIRInfo();
		VarPrefixManager varMan = new VarPrefixManager();
		IterationVarPrefixes iteVarPrefixes = varMan.getIteVarPrefixes();
		Exp2StmVarPrefixes exp2stmPrefixes = varMan.getExp2stmPrefixes();
		FuncValPrefixes funcValPrefixes = varMan.getFuncValPrefixes();
		PatternVarPrefixes patternPrefixes = varMan.getPatternPrefixes();
		// UnionTypeVarPrefixes unionTypePrefixes = varMan.getUnionTypePrefixes();
		// List<INode> cloneFreeNodes = codeGen.getJavaFormat().getValueSemantics().getCloneFreeNodes();

		// IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(varMan.postCheckMethodName());

		// Construct the transformations
		//transformations.add(new AtomicStmTrans(transAssistant, varMan.atomicTmpVar()));
		//transformations.add(new DivideTrans(info));
		//transformations.add(new CallObjStmTrans(info));

		transformations.add(new DistTransTest(transAssistant));
		transformations.add(new CallFuncMacroExpTrans(transAssistant));
		transformations.add(new GenerateDepObjId(transAssistant));
		
		return transformations;
	}

}
