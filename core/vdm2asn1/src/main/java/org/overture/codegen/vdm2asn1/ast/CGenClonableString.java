package org.overture.codegen.vdm2asn1.ast;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.ExternalNode;

public class CGenClonableString implements ExternalNode
	{
		private static final long serialVersionUID = 1L;
		public String value;

		public CGenClonableString(String value)
		{
			this.value = value;
		}

		@Override
		public String toString()
		{
			return value;
		}

		public Object clone()
		{
			return new ClonableString(value);
		}
	}