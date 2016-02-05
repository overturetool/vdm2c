package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newReturnStm;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;
import org.overture.codegen.ir.types.AMethodTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class InitializerExtractorTrans extends DepthFirstAnalysisAdaptor
{
	private static final String FIELD_INITIALIZER = "fieldInitializer";
	public TransAssistantCG assist;

	public InitializerExtractorTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		SExpCG initial = node.getInitial();
		if(initial!=null)
		{
			if(initial.getType()==null)
			{
				node.setInitial(null);
				return;}
			STypeCG type = initial.getType().clone();
//			SExpCG exp = ndoe.get
			SStmCG body = newReturnStm(initial);
			
			AMethodDeclCG method = new AMethodDeclCG();
			method.setAbstract(false);
			method.setAsync(false);
			method.setImplicit(false);
			method.setStatic(false);
			method.setIsConstructor(false);
			method.setTag(new Vdm2cTag().addMethodTag(MethodTag.Internal));
			method.setBody(body);
			AMethodTypeCG mtype = new AMethodTypeCG();
			mtype.setResult(type);
			method.setMethodType(mtype);
			method.setName(assist.getInfo().getTempVarNameGen().nextVarName(FIELD_INITIALIZER));
			
			node.setInitial(newApply(NameMangler.mangle(method)));
			
			SClassDeclCG cls = node.getAncestor(SClassDeclCG.class);
			cls.getMethods().add(method);
		}
	}
}