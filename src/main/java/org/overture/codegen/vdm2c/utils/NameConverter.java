package org.overture.codegen.vdm2c.utils;

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.ir.declarations.AFieldDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;

public class NameConverter
{
	final static String valueNameTemplate = "g_%s_%s";

	final static Map<String, String> fieldNames = new HashMap<String, String>();

	public static String getCName(AFieldDeclCG field)
	{
		String name = String.format(valueNameTemplate, field.getType().getAncestor(SClassDeclCG.class).getName(), field.getName());
		fieldNames.put(field.getName(), name);
		return name;
	}

	public static String getCFieldNameFromOriginal(String name)
	{
		return fieldNames.get(name);
	}
}
