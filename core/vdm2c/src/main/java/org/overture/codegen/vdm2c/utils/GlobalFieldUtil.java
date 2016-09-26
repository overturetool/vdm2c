package org.overture.codegen.vdm2c.utils;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalFieldUtil
{
	final static Logger logger = LoggerFactory.getLogger(GlobalFieldUtil.class);
	final TransAssistantIR assist;

	public GlobalFieldUtil(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	public void replaceWithIdentifier(SVarExpIR node)
	{
		String className = null;
		
		if(node instanceof AExplicitVarExpIR)
		{
			STypeIR classType = ((AExplicitVarExpIR) node).getClassType();
			
			if(classType instanceof AClassTypeIR)
			{
				className = ((AClassTypeIR) classType).getName();
			}
		}
		else if(node instanceof AIdentifierVarExpIR)
		{
			className = node.getAncestor(SClassDeclIR.class).getName();
		}
		
		if(className == null)
		{
			logger.warn("Expected explicit or identifier variable expression at this  point. Got: %", node);
			// Give up
			return;
		}
		
		AFieldDeclIR field = lookupField(CTransUtil.getClass(assist, className), node.getName());
		
		if(field == null)
		{
			return;
		}
		
		AIdentifierVarExpIR identifier = newIdentifier(NameConverter.getCName(field), node.getSourceNode());
		identifier.setIsLocal(false);
		identifier.setType(field.getType().clone());
		assist.replaceNodeWith(node, identifier);
	}

	public void replaceWithStaticReference(SClassDefinition classDefinition,
			AIdentifierVarExpIR node)
	{
		SClassDeclIR classDef = CTransUtil.getClass(assist, classDefinition.getName().getName());
		replaceWithStaticReference(classDef, node);
	}

	void replaceWithStaticReference(SClassDeclIR classDef,
			AIdentifierVarExpIR identifier)
	{
		replaceWithStaticReference(classDef, identifier.getName(), identifier);
	}

	public void replaceWithStaticReference(SClassDeclIR classDef, String name,
			SExpIR node)
	{
		AFieldDeclIR field = lookupField(classDef, name);
		AIdentifierVarExpIR newIdentifier = newIdentifier(NameConverter.getCName(field), node.getSourceNode());
		newIdentifier.setType(node.getType().clone());
		newIdentifier.setIsLocal(false);
		AApplyExpIR vdmCloneApply = newApply("vdmClone", newIdentifier);
		vdmCloneApply.setType(node.getType().clone());
		assist.replaceNodeWith(node, vdmCloneApply);
	}

	public String lookupFieldClass(SClassDeclIR node, String name)
	{
		for (AFieldDeclIR f : node.getFields())
		{
			if (f.getName().equals(name))
			{
				return node.getName();
			}
		}

		for (ATokenNameIR superName : node.getSuperNames())
		{
			for (SClassDeclIR def : assist.getInfo().getClasses())
			{
				if (def.getName().equals(superName))
				{
					String n = lookupFieldClass(def, name);
					if (n != null)
					{
						return n;
					}
				}
			}
		}

		return null;
	}

	public AFieldDeclIR lookupField(SClassDeclIR node, String name)
	{
		for (AFieldDeclIR f : node.getFields())
		{
			if (f.getName().equals(name))
			{
				return f;
			} else if (f.getStatic())
			{
				if (NameConverter.matches(f, name))
				{
					return f;
				}

			}
		}

		for (ATokenNameIR superName : node.getSuperNames())
		{
			SClassDeclIR def = CTransUtil.getClass(assist, superName.getName());

			if (def != null)
			{
				AFieldDeclIR n = lookupField(def, name);
				if (n != null)
				{
					return n;
				}
			} else
			{
				logger.error("Unable to find super class: {}, when searching for field: {}", superName, name);
			}
		}

		return null;
	}

	public boolean isStatic(SClassDeclIR classDef, String name)
	{
		AFieldDeclIR field = lookupField(classDef, name);

		return field.getStatic();
	}

}
