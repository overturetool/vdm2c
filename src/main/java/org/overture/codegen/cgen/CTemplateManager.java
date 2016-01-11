package org.overture.codegen.cgen;

import org.overture.cgc.extast.declarations.AClassHeaderDeclCG;
import org.overture.cgc.extast.declarations.AClassStateDeclCG;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;

public class CTemplateManager extends TemplateManager
{

	public CTemplateManager(TemplateStructure templateStructure)
	{
		super(templateStructure);
		initCNodes();
	}

	private void initCNodes()
	{
		nodeTemplateFileNames.put(AClassHeaderDeclCG.class, templateStructure.DECL_PATH
				+ "ClassHeader");
		nodeTemplateFileNames.put(AClassStateDeclCG.class, templateStructure.DECL_PATH
				+ AClassStateDeclCG.class.getSimpleName());
	}

}
