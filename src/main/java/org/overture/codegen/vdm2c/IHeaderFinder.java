package org.overture.codegen.vdm2c;

import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclCG;

public interface IHeaderFinder
{
	public AClassHeaderDeclCG getHeader(SClassDeclCG def);
}