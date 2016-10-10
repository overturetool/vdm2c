package org.overture.codegen.vdm2c;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ATimeExp;

public class TimeFinder extends DepthFirstAnalysisAdaptor
{
	private boolean found = false;
	
	public static Map<String, Boolean> computeTimeMap(List<SClassDefinition> ast)
	{
		Map<String, Boolean> hasTimeMap = new HashMap<>();
		
		for(SClassDefinition n : ast)
		{
			TimeFinder finder = new TimeFinder();
			
			try
			{
				n.apply(finder);
			}
			catch(AnalysisException e)
			{
				// The visitor was stopped
			}
			
			hasTimeMap.put(n.getName().getName(), finder.found);
		}
		
		return hasTimeMap;
	}
	
	@Override
	public void caseATimeExp(ATimeExp node) throws AnalysisException
	{
		found = true;
		throw new AnalysisException();
	}
}