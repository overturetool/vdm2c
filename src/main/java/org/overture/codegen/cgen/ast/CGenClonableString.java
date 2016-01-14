package org.overture.codegen.cgen.ast;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.ExternalNode;

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