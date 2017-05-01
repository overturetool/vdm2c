package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AStmExpIR;
import org.overture.codegen.vdm2c.extast.statements.AC89forLoopStmIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C89ForLoopTrans extends DepthFirstAnalysisCAdaptor {

	protected final static Logger logger = LoggerFactory.getLogger(C89ForLoopTrans.class);

	private TransAssistantIR assist;

	public C89ForLoopTrans(TransAssistantIR assist) {
		this.assist = assist;
	}

	@Override
	public void caseAForLoopStmIR(AForLoopStmIR node) throws AnalysisException {

		super.caseAForLoopStmIR(node);

		if (node.getInit().getPattern() instanceof AIdentifierPatternIR) {
			
			String name = ((AIdentifierPatternIR) node.getInit().getPattern()).getName();
			
			AVarDeclIR decl = node.getInit().clone();

			AAssignToExpStmIR init = new AAssignToExpStmIR();
			init.setSourceNode(node.getSourceNode());
			init.setTarget(assist.getInfo().getExpAssistant().consIdVar(name, decl.getType().clone()));
			init.setExp(decl.getExp().clone());
			
			AStmExpIR expInit = new AStmExpIR();
			expInit.setSourceNode(init.getSourceNode());
			expInit.setStm(init);

			decl.setExp(null);
			
			AC89forLoopStmIR loopC89 = new AC89forLoopStmIR();
			loopC89.setBody(node.getBody().clone());
			loopC89.setCond(node.getCond().clone());
			loopC89.setInc(node.getInc().clone());
			loopC89.setInit(expInit);
			loopC89.setLoopVar(decl);

			assist.replaceNodeWith(node, loopC89);
		} else {

			logger.error("Expected %s to be an identifier pattern.", node.getInit());
		}
	}
}
