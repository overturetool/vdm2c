package org.overture.codegen.vdm2c;

import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;

public class ConstructionUtils
{

	public static AApplyExpIR consUtilCall(String utils_name, String memberName,
			STypeIR returnType)
	{
		AExplicitVarExpIR member = new AExplicitVarExpIR();

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(returnType.clone());
		member.setType(methodType);
		member.setIsLambda(false);
		member.setIsLocal(false);
		// member.setIsStatic(true);

		AExternalTypeIR classType = new AExternalTypeIR();
		classType.setName(utils_name);
		member.setClassType(classType);
		member.setName(memberName);
		AApplyExpIR call = new AApplyExpIR();

		call.setType(returnType.clone());
		call.setRoot(member);

		return call;
	}

}
