package org.overture.codegen.vdm2c;

public class CGenSettings
{
	private boolean useGarbageCollection = false;
	
	private boolean useDistributionCG = true;

	public CGenSettings()
	{
		super();
	}

	public boolean usesGarbageCollection()
	{
		return useGarbageCollection;
	}

	public void setUseGarbageCollection(boolean useGarbageCollection)
	{
		this.useGarbageCollection = useGarbageCollection;
	}
	
	public boolean usesDistributionCG()
	{
		return useDistributionCG;
	}

	public void setUseDistributionCG(boolean useDistributionCG)
	{
		this.useDistributionCG = useDistributionCG;
	}
}
