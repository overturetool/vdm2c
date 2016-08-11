package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newLocalDefinition;

import java.util.List;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AMkBasicExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ARecordModExpIR;
import org.overture.codegen.ir.patterns.ARecordPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordDefsToClassDefsTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(RecordDefsToClassDefsTrans.class);
	public TransAssistantIR assist;

	final static String classNamePrefix = "";

	public RecordDefsToClassDefsTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}
	
	@Override
	public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAFieldDeclIR(node);

		//Change this field declaration's type from a record type to a class type.
		if(node.getType() instanceof ARecordTypeIR)
		{
			for(SClassDeclIR c : this.assist.getInfo().getClasses())
			{
				if(c instanceof ADefaultClassDeclIR)
				{
					if(((ADefaultClassDeclIR)c).getName().equals(((ARecordTypeIR)node.getType()).getName().getName()))
					{
						SourceNode typeSource = node.getType().getSourceNode();
						node.setType(new AClassTypeIR());
						((AClassTypeIR)node.getType()).setName(((ADefaultClassDeclIR)c).getName());
						((AClassTypeIR)node.getType()).setOptional(((AClassTypeIR)node.getType()).getOptional());
						((AClassTypeIR)node.getType()).setSourceNode(typeSource);
						
						return;
					}				
				}
			}
		}
	}
}