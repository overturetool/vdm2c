package org.overture.codegen.cgen.utils;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;

public class NameMangler
{
	static final String preFix = "_Z";
	static final String intId = "I";
	// static final String int1Id="I1";
	static final String realId = "R";
	static final String charId = "C";
	static final String boolId = "B";
	static final String voidId = "V";

	static final String nameId = "%d%s";

	static final String mangledPattern = preFix + "%sE%s";

	static final String setId = "%dS";
	static final String seqId = "%dQ";
	static final String mapId = "%dM";
	static final String classId = "%dC%s";

	static final NameGenerator generator = new NameGenerator();

	static String mkName(String name)
	{
		return String.format(nameId, name.length(), name);
	}

	public static String mangle(AMethodDeclCG method) throws AnalysisException
	{
		if(method.getName().startsWith(preFix))
		{
			return method.getName();
		}

		StringBuilder sb = new StringBuilder();
		for (AFormalParamLocalParamCG formal : method.getFormalParams())
		{
			sb.append(formal.getType().apply(generator));
		}

		if (sb.length() == 0)
		{
			sb.append(voidId);
		}

		String name = String.format(mangledPattern, mkName(method.getName()), sb.toString());
		System.out.println(method.getName() + " mangled to " + name);
		return name;
	}
	
	public static String getName(String mangledName)
	{
		if(mangledName.startsWith(preFix))
		{
			String tmp= mangledName.substring(preFix.length());
			
			
		int index = 0;
		while(Character.isDigit(tmp.charAt(index)))
		{
			index++;
		}
		int length = Integer.parseInt(tmp.substring(0,index));

		return tmp.substring(index,index+ length);
//			return mangledName.substring(preFix.length(),mangledName.in-preFix.length());
		}
		//unmangled
		return mangledName;
	}

	private static class NameGenerator extends
			DepthFirstAnalysisAdaptorAnswer<String>
	{

		@Override
		public String caseAClassTypeCG(AClassTypeCG node)
				throws AnalysisException
		{
			return mkName(node.getName());
		}

		@Override
		public String caseAIntNumericBasicTypeCG(AIntNumericBasicTypeCG node)
				throws AnalysisException
		{
			return intId;
		}

		@Override
		public String caseABoolBasicTypeCG(ABoolBasicTypeCG node)
				throws AnalysisException
		{
			return intId;
		}

		@Override
		public String caseARealNumericBasicTypeCG(ARealNumericBasicTypeCG node)
				throws AnalysisException
		{
			return realId;
		}

		@Override
		public String caseACharBasicTypeCG(ACharBasicTypeCG node)
				throws AnalysisException
		{
			return charId;
		}

		@Override
		public String mergeReturns(String original, String new_)
		{
			return original + new_;
		}

		@Override
		public String createNewReturnValue(INode node) throws AnalysisException
		{
			return "";
		}

		@Override
		public String createNewReturnValue(Object node)
				throws AnalysisException
		{
			return "";
		}

	}

}
