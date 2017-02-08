package org.overture.codegen.vdm2c;

import java.util.HashSet;
import java.util.Set;

import org.overture.codegen.ir.PIR;

public class Vdm2cTag
{
	public enum MethodTag
	{
		/**
		 * Internal method, should not be exported and included in the VTable
		 */
		Internal, 
		/**
		 * Do not generate code in .c
		 */
		HeaderOnly ,
		FieldInitializer
	};

	public final Set<MethodTag> methodTags = new HashSet<MethodTag>();

	public Vdm2cTag addMethodTag(MethodTag tag)
	{
		this.methodTags.add( tag);
		return this;
	}
	
	public static void addMethodTag(PIR node, MethodTag methodTag)
	{
		Object tag = node.getTag();
		
		if(tag == null)
		{
			Vdm2cTag vdm2cTag = new Vdm2cTag();
			node.setTag(vdm2cTag);
			vdm2cTag.addMethodTag(methodTag);
		}
		else if (tag instanceof Vdm2cTag)
		{
			((Vdm2cTag) tag).addMethodTag(methodTag);
		}
	}
	
}
