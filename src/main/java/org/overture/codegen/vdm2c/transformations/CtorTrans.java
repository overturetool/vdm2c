package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.addArgument;
import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newCast;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newReturnStm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;

import java.util.Collections;
import java.util.LinkedList;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;
import org.overture.codegen.ir.expressions.AAddrEqualsBinaryExpCG;
import org.overture.codegen.ir.expressions.ANullExpCG;
import org.overture.codegen.ir.name.ATokenNameCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.AIfStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class CtorTrans extends DepthFirstAnalysisAdaptor
{
	// private static final String _CTOR = "_ctor";

	public TransAssistantCG assist;

	final static String retPrefix = "ctor_";

	public CtorTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		if (node.getIsConstructor())
		{
			node.setStatic(true);// make available in the class header

			// rename
			node.setName(NameMangler.mangle(node));

			// correct types and args
			node.getMethodType().setResult(newTvpType());
			SClassDeclCG cDef = node.getAncestor(SClassDeclCG.class);
			addArgument("this", newExternalType(cDef.getName() + "CLASS"), 0, node.getFormalParams());

			// modify body to include memory allocation
			String bufName = "__buf";

			AIfStmCG ifStm = new AIfStmCG();
			
			AAddrEqualsBinaryExpCG addrEquals = new AAddrEqualsBinaryExpCG();
			addrEquals.setLeft(createIdentifier("this", null));
			addrEquals.setRight(new ANullExpCG());
			
			ifStm.setIfExp(addrEquals);

			ABlockStmCG initClassBlock = new ABlockStmCG();
			initClassBlock.setScoped(true);

			initClassBlock.getStatements().add(newAssignment(createIdentifier(bufName, null), newApply("new")));
			initClassBlock.getStatements().add(newAssignment(createIdentifier("this", null), newApply("TO_CLASS_PTR", createIdentifier(bufName, null), createIdentifier(cDef.getName(), null))));
			ifStm.setThenStm(initClassBlock);

			ABlockStmCG replBlock = new ABlockStmCG();
			replBlock.setScoped(true);
			replBlock.getLocalDefs().add(newDeclarationAssignment(bufName, newTvpType(), new ANullExpCG(), null));
			replBlock.getStatements().add(ifStm);

			// add constructor body
			replBlock.getStatements().add(node.getBody());

			// debug
			replBlock.getStatements().add(exp2Stm(newApply("printf", createIdentifier("\"calling constructor: "
					+ cDef.getName() + "\\n\"", null))));

			// call super constructors in reverse order
			if (cDef != null && !cDef.getSuperNames().isEmpty())
			{
				@SuppressWarnings("unchecked")
				LinkedList<ATokenNameCG> supers = (LinkedList<ATokenNameCG>) cDef.getSuperNames().clone();

				// invert order first super overrides last super
				Collections.reverse(supers);

				for (ATokenNameCG superName : supers)
				{

					for (SClassDeclCG def : assist.getInfo().getClasses())
					{
						if (def.getName().equals(superName.getName()))
						{
							for (AMethodDeclCG m : def.getMethods())
							{
								if (m.getIsConstructor()
										&& m.getFormalParams().size() == 1)
								{
									replBlock.getStatements().add(exp2Stm(newApply(NameMangler.mangle(m), newCast(def.getName()
											+ "CLASS", newApply("CLASS_CAST", createIdentifier("this", null), createIdentifier(cDef.getName(), null), createIdentifier(def.getName(), null))))));
								}
							}
						}
					}
				}

			}

			replBlock.getStatements().add(newReturnStm(createIdentifier(bufName, null)));

			node.setBody(replBlock);

		}
		super.caseAMethodDeclCG(node);
	}

}