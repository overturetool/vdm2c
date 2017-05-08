package org.overture.codegen.vdm2c.distribution.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallFuncMacroExpTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(CallFuncMacroExpTrans.class);
	public TransAssistantIR assist;

	final static String retPrefix = "embeding_";

	public CallFuncMacroExpTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAMacroApplyExpIR(AMacroApplyExpIR node)
			throws AnalysisException
	{ 
		
		if(node.getRoot() instanceof AIdentifierVarExpIR){
			AIdentifierVarExpIR id_exp = (AIdentifierVarExpIR) node.getRoot();
			
			if(id_exp.getName().equals("CALL_FUNC")){
				int args_len = node.getArgs().size() - 4;
				AIntLiteralExpIR nrArgs = new AIntLiteralExpIR();
				nrArgs.setValue((long) args_len);
				
				// get super type name, and add to 3 position
				AIdentifierVarExpIR supTy = (AIdentifierVarExpIR) node.getArgs().get(1).clone();
				supTy.setName("CLASS_ID_" + supTy.getName() + "_ID");
				node.getArgs().add(3, supTy);
				
				// Add number of function arguments to second last position
				node.getArgs().add(4, nrArgs);

				// Transform name to distribution macro
				id_exp.setName("DIST_CALL");
			}
		}		
	}

}

