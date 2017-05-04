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
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractEmbeddedCreationsTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(ExtractEmbeddedCreationsTrans.class);
	public TransAssistantIR assist;

	final static String retPrefix = "embeding_";

	public ExtractEmbeddedCreationsTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAMacroApplyExpIR(AMacroApplyExpIR node)
			throws AnalysisException
	{
		if (node.getRoot() instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR root = (AIdentifierVarExpIR) node.getRoot();

			String name = root.getName();
			if (CTransUtil.CALL_FUNC.equals(name) || CTransUtil.CALL_FUNC_PTR.equals(name))
			{
				// 4th id is a parameter
				List<SExpIR> args = node.getArgs();
				if (args.size() > 4)
				{
					for (int i = 4; i < args.size(); i++)
					{
						SExpIR exp = args.get(i);

						if (exp instanceof AIdentifierVarExpIR)
						{
							// all good
						} else
						{
							// this is embedding rewrite it

							// locate nearest block
							ABlockStmIR block = node.getAncestor(ABlockStmIR.class);
							if (block == null)
							{
								// no block found make one
								SStmIR nearestStm = node.getAncestor(SStmIR.class);

								block = new ABlockStmIR();
								assist.replaceNodeWith(nearestStm, block);
								block.getStatements().add(nearestStm);

							}

							// locate this node ancestor index in the block
							INode ancesterStm = node;
							int nodeIndex = -1;
							while (!ancesterStm.parent().equals(block))
							{
								ancesterStm = ancesterStm.parent();
							}

							for (int j = 0; j < block.getStatements().size(); j++)
							{
								if (block.getStatements().get(j) == ancesterStm)
								{
									nodeIndex = j;
								}
							}

							if (nodeIndex == -1)
							{
								logger.error("Unable to find block statement index for this node. {}", node);
							}

							logger.trace("Extracting embeded argument at arg index {} to outer block at index {}. Arg is: {}", i, nodeIndex, exp);
							String tmpVar = assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);

							AVarDeclIR decl = newDeclarationAssignment(tmpVar, exp.getType().clone(), exp, exp.getSourceNode());

							block.getStatements().add(nodeIndex, newLocalDefinition(decl));
							args.add(i, createIdentifier(tmpVar, exp.getSourceNode()));
						}
					}
				}
			}
		}
	}

}
