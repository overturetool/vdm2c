package org.overture.codegen.vdm2c.distribution.transformations;
import java.util.LinkedList;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
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
				
				AIntLiteralExpIR a = new AIntLiteralExpIR();
				a.setValue((long) args_len);
				
				// Add number of function arguments to second last position
				node.getArgs().add(node.getArgs().size() - 1, a);
				
				AIdentifierVarExpIR id_class = (AIdentifierVarExpIR) node.getArgs().get(0);
				id_exp.setName("DIST_CALL" + id_class.getName());
			}
		}		
	}

}

