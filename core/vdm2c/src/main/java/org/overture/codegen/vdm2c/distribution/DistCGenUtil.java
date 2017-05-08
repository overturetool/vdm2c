package org.overture.codegen.vdm2c.distribution;

import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;

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
		
		return par1;
	}
	
	public static AIdentifierVarExpIR createIdExpIntTyp(String name)
	{
		AIdentifierVarExpIR id = new AIdentifierVarExpIR();
		id.setIsLambda(false);
		id.setIsLocal(true);
		id.setName(name);
		id.setType(new AIntNumericBasicTypeIR());
		
		return id;
	}
	
	public static AIdentifierVarExpIR createIdExpNullTyp(String name)
	{
		AIdentifierVarExpIR id = new AIdentifierVarExpIR();
		id.setIsLambda(false);
		id.setIsLocal(true);
		id.setName(name);
		id.setType(null);
		
		return id;
	}
	
	public static AIdentifierVarExpIR createIdExpTVPTyp(String name)
	{
		AIdentifierVarExpIR id = new AIdentifierVarExpIR();
		id.setIsLambda(false);
		id.setIsLocal(true);
		id.setName(name);
		AExternalTypeIR tyPat = new AExternalTypeIR();
		tyPat.setName("TVP");
		id.setType(tyPat);
		
		return id;
	}
	
}
