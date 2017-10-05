package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.LinkedList;

import org.overture.ast.types.AIntNumericBasicType;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class GenerateDepObjId extends DepthFirstAnalysisCAdaptor
{

	

	public GenerateDepObjId(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		int obj_id = 0;

		ADefaultClassDeclIR cl = node.getAncestor(ADefaultClassDeclIR.class);

		// LinkedList<AMethodDeclIR> mthL = cl.getMethods();

		// Get the static initializer
		if (node.getName().equals(SystemArchitectureAnalysis.systemName
				+ "_static_init"))
		{
			//System.out.println("Dist transformation, method name: "
			//	+ node.getName());

			LinkedList<String> initFunName = new LinkedList<>();

			// Get body

			if (node.getBody() instanceof ABlockStmIR)
			{
				ABlockStmIR body = (ABlockStmIR) node.getBody();

				// get statements
				for (SStmIR stm : body.getStatements())
				{
					if (stm instanceof AAssignToExpStmIR)
					{
						AAssignToExpStmIR astm = (AAssignToExpStmIR) stm;
						if (astm.getExp() instanceof AApplyExpIR)
						{
							AApplyExpIR apply = (AApplyExpIR) astm.getExp();
							String nm = apply.getRoot().toString();
							initFunName.add(nm);
						}
					}
				}

			}

			for (AMethodDeclIR m : cl.getMethods())
			{
				if (initFunName.contains(m.getName()))
				{
					obj_id = obj_id + 1;
					if (node.getBody() instanceof ABlockStmIR)
					{
						ABlockStmIR mBody = (ABlockStmIR) m.getBody();

						ALocalVariableDeclarationStmIR firstElem = (ALocalVariableDeclarationStmIR) mBody.getStatements().get(0);

						// Build new assignment expression
						AAssignToExpStmIR sec = new AAssignToExpStmIR();

						// 1. Set exp
						AIntLiteralExpIR id = new AIntLiteralExpIR();
						ANat1NumericBasicTypeIR ty = new ANat1NumericBasicTypeIR();
						id.setType(ty);
						id.setValue((long) obj_id);
						sec.setExp(id);

						// 2. Set target
						AIdentifierVarExpIR ta = new AIdentifierVarExpIR();
						ta.setIsLocal(true);
						ta.setName(firstElem.getDecleration().getPattern().toString() + "->id");
						ta.setType(new AIntNumericBasicTypeIR());
						sec.setTarget(ta);

						// Add it to function body
						mBody.getStatements().add(1, sec);
					}
				}
			}
		}
	}

	// @Override
	// public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
	// throws AnalysisException
	// {
	// System.out.println("Hi..");
	// //AClassHeaderDeclIR par = node.getAncestor(AClassHeaderDeclIR.class);
	// //par.apply(this);
	// //System.out.println("Class name: " + node.getName());
	// }

	// @Override
	// public void caseAArrayDeclIR(AArrayDeclIR node) throws AnalysisException
	// {
	// // TODO Auto-generated method stub
	// super.caseAArrayDeclIR(node);
	//
	// System.out.println(node.getName());
	//
	//
	// }

}
