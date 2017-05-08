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
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class TransformRemoteObject extends DepthFirstAnalysisCAdaptor
{



	public TransformRemoteObject(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		int obj_id = 0;

		ADefaultClassDeclIR cl = node.getAncestor(ADefaultClassDeclIR.class);

		if(cl.getTag()==null) return;

		// Get the static initializer
		if (node.getName().equals(SystemArchitectureAnalysis.systemName
				+ "_static_init"))
		{
			
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

				LinkedList<Boolean> cpuDM = SystemArchitectureAnalysis.DM.get(cl.getTag().toString());

				if (initFunName.contains(m.getName()))
				{
					obj_id = obj_id + 1;
					if(!cpuDM.get(obj_id)){
						ABlockStmIR bd = (ABlockStmIR) m.getBody();

						ALocalVariableDeclarationStmIR fst = (ALocalVariableDeclarationStmIR) bd.getStatements().get(0);

						AApplyExpIR appExp = (AApplyExpIR) fst.getDecleration().getExp();

						((AIdentifierVarExpIR) appExp.getRoot()).setName("newInt");
						
						appExp.getArgs().clear();
						
						AIntLiteralExpIR val = new AIntLiteralExpIR();
						val.setType(new ANatNumericBasicTypeIR());
						val.setValue((long) obj_id);
						appExp.getArgs().add(val);	
					}
				}
			}
		}
	}

}
