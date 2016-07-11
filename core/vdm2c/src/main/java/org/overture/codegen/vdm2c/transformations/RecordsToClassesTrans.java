package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newLocalDefinition;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifierPattern;

import java.util.List;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIRBase;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordsToClassesTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(RecordsToClassesTrans.class);
	public TransAssistantIR assist;

	final static String classNamePrefix = "";

	public RecordsToClassesTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}
	
	//For each record type definition, this creates a new class declaration and
	//adds it to the list of classes in this model.  The new classes will be
	//generated in the usual way.
	@Override
	public void caseARecordDeclIR(ARecordDeclIR node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseARecordDeclIR(node);
		
		//Construct the class definition.
		ADefaultClassDeclIR recClass = new ADefaultClassDeclIR();
		recClass.setSourceNode(node.getSourceNode());
		recClass.setName(classNamePrefix + node.getName());
		recClass.getFields().addAll(node.getFields());
		
		//Equip the new class with the appropriate constructor.
		AMethodDeclIR ctor = new AMethodDeclIR();
		AMethodTypeIR ctorMethodType = new AMethodTypeIR();
		ABlockStmIR ctorBody = new ABlockStmIR();
		
		ctor.setMethodType(ctorMethodType);
		
		for(AFieldDeclIR f : recClass.getFields())
		{
			ctor.getFormalParams().add(new AFormalParamLocalParamIR());
			
			ctor.getFormalParams().get(ctor.getFormalParams().size() - 1).setPattern(newIdentifierPattern("param_" + f.getName()));
			ctor.getFormalParams().get(ctor.getFormalParams().size() - 1).setType(f.getType().clone());
			
			ctorMethodType.getParams().add(f.getType().clone());
			
			
			ctorBody.getStatements().add(newAssignment(
					newIdentifier(f.getName(), f.getSourceNode()),
					newIdentifier(ctor.getFormalParams().get(ctor.getFormalParams().size() - 1).getPattern().toString(), f.getSourceNode())));	
		}
		
		ctorMethodType.setResult(new AClassTypeIR());
		((AClassTypeIR)ctorMethodType.getResult()).setName(recClass.getName());
		ctor.setIsConstructor(true);
		ctor.setAbstract(false);
		ctor.setAccess("public");
		ctor.setName(recClass.getName());
		ctor.setBody(ctorBody);
		
		
		recClass.getMethods().add(ctor);
		this.assist.getInfo().getClasses().add(recClass);
	}
}