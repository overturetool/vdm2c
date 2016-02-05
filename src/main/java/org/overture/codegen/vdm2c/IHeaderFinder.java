package org.overture.codegen.vdm2c;

import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;

public interface IHeaderFinder
{
	public AClassHeaderDeclIR getHeader(SClassDeclIR def);
}
