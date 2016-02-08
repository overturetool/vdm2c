package org.overture.codegen.vdm2c;

import java.util.HashSet;
import java.util.Set;

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
		HeaderOnly 
	};

	public final Set<MethodTag> methodTags = new HashSet<MethodTag>();

	public Vdm2cTag addMethodTag(MethodTag tag)
	{
		this.methodTags.add( tag);
		return this;
	}
	
}
