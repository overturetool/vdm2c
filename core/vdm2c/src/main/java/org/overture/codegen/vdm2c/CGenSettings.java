package org.overture.codegen.vdm2c;

public class CGenSettings
{
	private boolean useGarbageCollection = false;

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
}
