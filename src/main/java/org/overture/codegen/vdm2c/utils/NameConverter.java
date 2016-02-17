package org.overture.codegen.vdm2c.utils;

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;

public class NameConverter
{
	final static String valueNameTemplate = "g_%s_%s";

	final static Map<AFieldDeclIR, String> fieldNames = new HashMap<AFieldDeclIR, String>();
	final static Map<AFieldDeclIR, String> originalNames = new HashMap<AFieldDeclIR, String>();

	public static String getCName(AFieldDeclIR field)
	{
		if (fieldNames.containsKey(field))
		{
			return fieldNames.get(field);
		}
		String name = String.format(valueNameTemplate, field.getType().getAncestor(SClassDeclIR.class).getName(), field.getName());
		fieldNames.put(field, name);
		originalNames.put(field, field.getName());
		return name;
	}

	public static boolean hasName(String name)
	{
		return fieldNames.containsKey(name);
	}

	public static boolean matches(AFieldDeclIR f, String name)
	{
		// make sure the name is in cache
		String fieldName = getCName(f);

		// check if name is already a global name
		if (fieldName.equals(name))
		{
			return true;
		} else if (originalNames.get(f).equals(name))
		{
			// unsafe check agains the original field name
			return true;
		}

		return false;

	}
}
