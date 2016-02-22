package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newLocalDefinition;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class ScopeCleanerTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public ScopeCleanerTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	static boolean hasDeclerations(ABlockStmIR block)
	{
		if (!block.getLocalDefs().isEmpty())
		{
			return true;
		}

		for (SStmIR stm : block.getStatements())
		{
			if (stm instanceof ALocalVariableDeclarationStmIR)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void caseABlockStmIR(ABlockStmIR node) throws AnalysisException
	{
		if (node.getScoped() == null)
		{
			node.setScoped(false);
		}

		super.caseABlockStmIR(node);

		if (node.getLocalDefs().isEmpty() && node.getStatements().isEmpty())
		{
			node.parent().removeChild(node);
		}

		// one block has one other block and no decls.
		if (node.getStatements().size() == 1
				&& node.getStatements().get(0) instanceof ABlockStmIR)
		{
			ABlockStmIR nestedBlock = (ABlockStmIR) node.getStatements().get(0);
			if (!hasDeclerations(node) || !hasDeclerations(nestedBlock))
			{
				node.getLocalDefs().addAll(nestedBlock.getLocalDefs());
				node.getStatements().addAll(nestedBlock.getStatements());
				node.removeChild(nestedBlock);
			}
		}

		// convert all declerations to statements with local definitions
		for (int i = node.getLocalDefs().size() - 1; i >= 0; i--)
		{
			node.getStatements().add(0, newLocalDefinition(node.getLocalDefs().get(i)));
		}

		// remove unnecessary scopes
		if (node.getLocalDefs().isEmpty()
				&& node.parent() instanceof ABlockStmIR)
		{
			// merge
			ABlockStmIR block = (ABlockStmIR) node.parent();
			for (int i = 0; i < block.getStatements().size(); i++)
			{
				if (block.getStatements().get(i) == node)
				{
					// this statement index is i
					for (int j = node.getStatements().size() - 1; j >= 0; j--)
					{
						block.getStatements().add(i, node.getStatements().get(j));
					}
					node.parent().removeChild(node);
					break;
				}
			}
		}

		if (!node.getScoped())
		{
			if (node.parent() instanceof ABlockStmIR)
			{
				ABlockStmIR block = (ABlockStmIR) node.parent();
				for (int i = 0; i < block.getStatements().size(); i++)
				{
					if (block.getStatements().get(i) == node)
					{
						// this statement index is i

						// move stmts
						for (int j = node.getStatements().size() - 1; j >= 0; j--)
						{
							block.getStatements().add(i, node.getStatements().get(j));
						}

						// move defs
						for (int j = node.getLocalDefs().size() - 1; j >= 0; j--)
						{
							block.getStatements().add(i, newLocalDefinition(node.getLocalDefs().get(j)));
						}

						node.parent().removeChild(node);
						break;
					}
				}
			}
		}

	}
}