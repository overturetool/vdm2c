package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class AddFieldTrans extends DepthFirstAnalysisCAdaptor
{
	private TransAssistantIR assist;

	private static final String NUM_FIELDS_PREFIX = "numFields_";
	
	public AddFieldTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		if (node.getSuperNames().isEmpty())
		{
			AIntLiteralExpIR initExp = assist.getInfo().getExpAssistant().consIntLiteral(node.getFields().size());
			
			AFieldDeclIR stateField = new AFieldDeclIR();
			stateField.setAccess(IRConstants.PUBLIC);
			stateField.setType(new ANat1NumericBasicTypeIR());
			stateField.setStatic(true);
			stateField.setFinal(true);
			stateField.setVolatile(false);
			stateField.setName(assist.getInfo().getTempVarNameGen().nextVarName(NUM_FIELDS_PREFIX));
			stateField.setInitial(initExp);
			
			node.getFields().addFirst(stateField);
		}
	}
}
