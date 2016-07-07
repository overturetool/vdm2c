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
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordsToClassesTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(RecordsToClassesTrans.class);
	public TransAssistantIR assist;

	final static String classNamePrefix = "record_type_";

	public RecordsToClassesTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}
	
	@Override
	public void caseARecordDeclIR(ARecordDeclIR node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseARecordDeclIR(node);
		
		ADefaultClassDeclIR recClass = new ADefaultClassDeclIR();
		recClass.setSourceNode(node.getSourceNode());
		recClass.setName(classNamePrefix + node.getName());
		recClass.getFields().addAll(node.getFields());
		this.assist.getInfo().getClasses().add(recClass);
	}
}
