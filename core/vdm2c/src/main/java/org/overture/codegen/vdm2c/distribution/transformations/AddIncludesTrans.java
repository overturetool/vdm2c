package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AExpStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.ast.CGenClonableString;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class AddIncludesTrans extends DepthFirstAnalysisCAdaptor
{

	public AddIncludesTrans(TransAssistantIR transformationAssistant)
	{
	}

	//	@Override
	//	public void caseAClassHeaderDeclIR(AClassHeaderDeclIR node)
	//			throws AnalysisException
	//	{	
	//		System.out.println();
	//		
	//		
	//		List<CGenClonableString> includes = (List<CGenClonableString>) node.getIncludes();
	//		
	//		CGenClonableString std_arge = new CGenClonableString("distCall");
	//		includes.add(std_arge);
	////		node.getIncludes().add(std_arge);
	//		
	//		
	//		
	////		includes.add(std_arge);
	//		
	////		node.setin
	//		
	//	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		ADefaultClassDeclIR cl = node;

		//if(node.getTag()!=null){

		//String cpu = cl.getTag().toString();

		AClassHeaderDeclIR header = node.getAncestor(AClassHeaderDeclIR.class);

		List<CGenClonableString> includes = (List<CGenClonableString>) header.getIncludes();

		CGenClonableString std_arge = new CGenClonableString("distCall");
		includes.add(std_arge);

		if(!cl.getName().equals(SystemArchitectureAnalysis.systemName)){
			CGenClonableString sysName = new CGenClonableString(SystemArchitectureAnalysis.systemName);
			includes.add(sysName);
		}

		header.setIncludes(includes);

		//header.getIncludes()


		//}
	}


}
