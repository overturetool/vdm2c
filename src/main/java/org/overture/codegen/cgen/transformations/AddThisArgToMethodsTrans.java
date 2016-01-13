package org.overture.codegen.cgen.transformations;

import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class AddThisArgToMethodsTrans extends DepthFirstAnalysisAdaptor
{

	// private TransAssistantCG transformationAssistant;

	public AddThisArgToMethodsTrans(TransAssistantCG transformationAssistant)
	{
		// this.transformationAssistant = transformationAssistant;
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		LinkedList<AFormalParamLocalParamCG> f = new LinkedList<>();

		AFormalParamLocalParamCG cl = new AFormalParamLocalParamCG();
		AIdentifierPatternCG id = new AIdentifierPatternCG();

		AIntNumericBasicTypeCG ty = new AIntNumericBasicTypeCG();

		// Create the special new parameter for each operation
		cl.setTag("class");
		id.setName("this"); // This one gets printed
		cl.setPattern(id);
		cl.setType(ty);

		f.add(cl);

		//add as first argument
		node.getFormalParams().add(0, cl);
	}

}
