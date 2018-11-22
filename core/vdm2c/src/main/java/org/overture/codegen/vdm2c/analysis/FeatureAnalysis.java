package org.overture.codegen.vdm2c.analysis;

import java.util.Arrays;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.ir.IRConstants;

abstract public class FeatureAnalysis extends DepthFirstAnalysisAdaptor
{
	public static final List<String> LIB_NAMES = Arrays.asList(IRConstants.CLASS_NAMES_USED_IN_VDM_PP_RT);
	
	public boolean hasFeature(List<SClassDefinition> ast)
	{
		try
		{
			for(SClassDefinition c : ast)
			{
				if(!isVdmLibrary(c)){
					
					c.apply(this);
				}
			}
		}
		catch(AnalysisException e)
		{
			// Found feature
			return true;
		}
		
		// Did not find feature
		return false;
	}
	
	private boolean isVdmLibrary(SClassDefinition c)
	{
		return LIB_NAMES.contains(c.getName().getName());
	}

	public void foundFeature() throws AnalysisException
	{
		// Stop the analysis
		throw new AnalysisException("Feature found!");
	}
}
