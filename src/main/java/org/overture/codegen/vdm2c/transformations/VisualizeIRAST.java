package org.overture.codegen.vdm2c.transformations;

import org.overture.ast.preview.GraphViz.GraphVizException;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.ast.preview.Dot;
import java.io.File;

public class VisualizeIRAST extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	public VisualizeIRAST(TransAssistantCG assist)
	{
		this.assist = assist;
	}
	
	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		org.overture.codegen.cgast.INode tmpnode = node;
		while(tmpnode.parent() != null)
		{tmpnode = tmpnode.parent();
		}
		try
		{Dot.makeImage(tmpnode, new File("/home/mot/Desktop/AST.svg"));}
		catch(GraphVizException e){}
	}
}