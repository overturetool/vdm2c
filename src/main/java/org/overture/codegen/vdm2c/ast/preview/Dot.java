package org.overture.codegen.vdm2c.ast.preview;

import java.io.File;

import org.overture.ast.preview.GraphViz;
import org.overture.ast.preview.GraphViz.GraphVizException;
import org.overture.codegen.cgast.INode;

public class Dot
{
	public static void makeImage(File dotPath, INode node, String type,
			File output) throws GraphVizException
	{
		DotGraphVisitor visitor = new DotGraphVisitor();
		try
		{
			node.apply(visitor, null);
		} catch (Throwable e)
		{
			// Ignore
		}
		GraphViz gv = new GraphViz(dotPath);
		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), output);
	}

	public static void makeImage(INode node, File output)
			throws GraphVizException
	{
		makeImage(new File("dot"), node, "svg", output);
	}
}
