package org.overture.codegen.vdm2c.utils;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1BasicTypeWrappersTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameMangler
{
	final static Logger logger = LoggerFactory.getLogger(NameMangler.class);

	static final String preFix = "_Z";
	static final String intId = "I";
	static final String natId = "N";
	static final String nat1Id = "K";

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

	public static String mangle(AMethodDeclIR method) throws AnalysisException
	{
		if (method.getName().startsWith(preFix))
		{
			return method.getName();
		}

		StringBuilder sb = new StringBuilder();
		for (AFormalParamLocalParamIR formal : method.getFormalParams())
		{
			sb.append(formal.getType().apply(generator));
		}

		if (sb.length() == 0)
		{
			sb.append(voidId);
		}

		String name = String.format(mangledPattern, mkName(method.getName()), sb.toString());
		logger.trace(method.getName() + " mangled to " + name);
		return name;
	}

	public static String getName(String mangledName)
	{
		if (mangledName.startsWith(preFix))
		{
			String tmp = mangledName.substring(preFix.length());

			int index = 0;
			while (Character.isDigit(tmp.charAt(index)))
			{
				index++;
			}
			int length = Integer.parseInt(tmp.substring(0, index));

			return tmp.substring(index, index + length);
			// return mangledName.substring(preFix.length(),mangledName.in-preFix.length());
		}
		// unmangled
		return mangledName;
	}

	private static class NameGenerator extends
			DepthFirstAnalysisAdaptorAnswer<String>
	{

		@Override
		public String caseAClassTypeIR(AClassTypeIR node)
				throws AnalysisException
		{
			String name = node.getName();
			return String.format(classId, name.length(), name);
		}

		@Override
		public String caseANatNumericBasicTypeIR(ANatNumericBasicTypeIR node)
				throws AnalysisException
		{
			return natId;
		}

		@Override
		public String caseANat1BasicTypeWrappersTypeIR(
				ANat1BasicTypeWrappersTypeIR node) throws AnalysisException
		{
			return nat1Id;
		}

		@Override
		public String caseAIntNumericBasicTypeIR(AIntNumericBasicTypeIR node)
				throws AnalysisException
		{
			return intId;
		}

		@Override
		public String caseABoolBasicTypeIR(ABoolBasicTypeIR node)
				throws AnalysisException
		{
			return boolId;
		}

		@Override
		public String caseARealNumericBasicTypeIR(ARealNumericBasicTypeIR node)
				throws AnalysisException
		{
			return realId;
		}

		@Override
		public String caseACharBasicTypeIR(ACharBasicTypeIR node)
				throws AnalysisException
		{
			return charId;
		}

		@Override
		public String caseAExternalTypeIR(AExternalTypeIR node)
				throws AnalysisException
		{
			return "";
		}

		@Override
		public String caseASeqSeqTypeIR(ASeqSeqTypeIR node)
				throws AnalysisException
		{
			String name = node.getSeqOf().apply(THIS);
			return String.format(seqId, name.length(), name);
		}

		@Override
		public String defaultInINode(INode node) throws AnalysisException
		{
			System.err.println("Mangling not handled: "
					+ node.getClass().getName());
			return super.defaultInINode(node);
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
