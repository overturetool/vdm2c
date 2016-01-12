package org.overture.codegen.cgen;

import org.overture.cgc.extast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;

public interface IHeaderFinder
{
	public AClassHeaderDeclCG getHeader(SClassDeclCG def);
}
