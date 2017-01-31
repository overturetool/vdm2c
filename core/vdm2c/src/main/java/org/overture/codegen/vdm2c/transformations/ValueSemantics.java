package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;

public class ValueSemantics {

	public static SExpIR clone(SExpIR exp) {
		if (exp instanceof SVarExpIR) {
			AApplyExpIR vdmCloneApply = CTransUtil.newApply("vdmClone", exp);
			vdmCloneApply.setType(exp.getType().clone());
			return vdmCloneApply;
		} else {
			// Don't clone literals
			return exp;
		}
	}

	public static SExpIR free(String name, SourceNode source)
	{
		AIdentifierVarExpIR newIdentifier = CTransUtil.newIdentifier(name, source);
		return CTransUtil.newApply("vdmFree", newIdentifier);
	}
}