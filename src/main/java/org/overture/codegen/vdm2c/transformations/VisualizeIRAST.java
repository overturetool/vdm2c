package org.overture.codegen.vdm2c.transformations;

import java.io.File;

import org.overture.ast.preview.GraphViz.GraphVizException;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.ast.preview.Dot;

public class VisualizeIRAST extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	public VisualizeIRAST(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		INode tmpnode = node;
		while (tmpnode.parent() != null)
		{
			tmpnode = tmpnode.parent();
		}
		try
		{
			Dot.makeImage(tmpnode, new File("/home/mot/Desktop/AST.svg"));
		} catch (GraphVizException e)
		{
		}
	}
}