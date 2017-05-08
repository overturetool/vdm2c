package org.overture.codegen.vdm2c.distribution;

import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.AExternalTypeIR;

public class DistCGenUtil {

	/**
	 * Function to add a parameter to a method node
	 */
	public static AFormalParamLocalParamIR addMethodParameter(String name, String type)
	{
		AFormalParamLocalParamIR par1 = new AFormalParamLocalParamIR();
		AIdentifierPatternIR idPat1 = new AIdentifierPatternIR();
		idPat1.setName(name);
		par1.setPattern(idPat1);
		AExternalTypeIR tyPat = new AExternalTypeIR();
		tyPat.setName(type);
		par1.setType(tyPat);
		//par.add(par1);
		
		return par1;
	}
}
