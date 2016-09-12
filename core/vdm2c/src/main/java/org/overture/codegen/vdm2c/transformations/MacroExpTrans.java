package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newLocalDefinition;

import java.util.List;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroExpTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(MacroExpTrans.class);
	public TransAssistantIR assist;

	final static String retPrefix = "embeding_";

	public MacroExpTrans(TransAssistantIR assist)
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
				AIdentifierVarExpIR id_class = (AIdentifierVarExpIR) node.getArgs().get(0);
				System.out.println("A call");
				id_exp.setName("DIST_CALL_" + id_class.getName()); // change all the call function
			}
		}
		
		//System.out.println(node.getRoot());
		
	}

}
