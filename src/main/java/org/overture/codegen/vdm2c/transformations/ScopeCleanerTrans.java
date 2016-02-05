package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newLocalDefinition;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ScopeCleanerTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	public ScopeCleanerTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		if (node.getScoped() == null)
		{
			node.setScoped(false);
		}

		super.caseABlockStmCG(node);

		if (node.getLocalDefs().isEmpty() && node.getStatements().isEmpty())
		{
			node.parent().removeChild(node);
		}

		// remove unnecessary scopes
		if (node.getLocalDefs().isEmpty()
				&& node.parent() instanceof ABlockStmCG)
		{
			// merge
			ABlockStmCG block = (ABlockStmCG) node.parent();
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
			if (node.parent() instanceof ABlockStmCG)
			{
				ABlockStmCG block = (ABlockStmCG) node.parent();
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