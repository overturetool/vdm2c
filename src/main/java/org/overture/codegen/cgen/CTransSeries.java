package org.overture.codegen.cgen;

/*
 import static org.overture.codegen.ir.CodeGenBase.EVAL_METHOD_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.INTERFACE_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.PARAM_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.TEMPLATE_TYPE_PREFIX;

 import static org.overture.codegen.ir.CodeGenBase;
 */
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgen.transformations.AddThisArgToMethodsTrans;
import org.overture.codegen.cgen.transformations.CallRewriteTrans;
import org.overture.codegen.cgen.transformations.CtorTrans;
import org.overture.codegen.cgen.transformations.DontcareParameterRenamingTrans;
import org.overture.codegen.cgen.transformations.ExtractRetValTrans;
import org.overture.codegen.cgen.transformations.FieldIdentifierToFieldGetApplyTrans;
import org.overture.codegen.cgen.transformations.MangleMethodNamesTrans;
import org.overture.codegen.cgen.transformations.NewRewriteTrans;
//import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.letexps.FuncTrans;

public class CTransSeries
{

	private CGen codeGen;

	public CTransSeries(CGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses(
			List<ADefaultClassDeclCG> classes,
			FuncValAssistant functionValueAssistant)
	{
		// Data and functionality to support the transformations
		// IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		// TempVarPrefixes varPrefixes = codeGen.getTempVarPrefixes();
		// ITempVarGen nameGen = irInfo.getTempVarNameGen();
		// TraceNames traceNamePrefixes = codeGen.getTracePrefixes();
		TransAssistantCG transAssistant = codeGen.getTransAssistant();
		// IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		// Set up order of transformations
		List<DepthFirstAnalysisAdaptor> transformations = new LinkedList<DepthFirstAnalysisAdaptor>();

		// Construct the transformations
		transformations.add(new FuncTrans(transAssistant));

		/* C transformations */

		transformations.add(new AddThisArgToMethodsTrans(transAssistant));
		transformations.add(new MangleMethodNamesTrans(transAssistant));
	
		transformations.add(new CallRewriteTrans(transAssistant));
		transformations.add(new ExtractRetValTrans(transAssistant));
		transformations.add(new FieldIdentifierToFieldGetApplyTrans(transAssistant));
		transformations.add(new CtorTrans(transAssistant));
		transformations.add(new NewRewriteTrans(transAssistant));
		transformations.add(new DontcareParameterRenamingTrans(transAssistant));
		return transformations;
	}

}
