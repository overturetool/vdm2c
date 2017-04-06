package org.overture.codegen.vdm2c.utils;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.types.ASetSetType;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameMangler
{
	final static Logger logger = LoggerFactory.getLogger(NameMangler.class);
	
	public static Map<String, String> mangledNames = new HashMap<String, String>();

	static final String preFix = "_Z";
	static final String intId = "I";
	static final String natId = "N";
	static final String nat1Id = "K";

	// static final String int1Id="I1";
	static final String realId = "R";
	static final String charId = "C";
	static final String boolId = "B";
	static final String voidId = "V";
	static final String ratId = "J";
	static final String tokenId = "T";

	static final String unknownId = "U";

	static final String nameId = "%d%s";

	static final String mangledPattern = preFix + "%sE%s";

	static final String setId = "%dS%s";
	static final String set1Id = "%dG%s";
	
	
	static final String seqId = "%dQ%s";
	static final String seq1Id = "%dH%s";
	
	
	static final String mapId = "%dM%s%s";
	static final String classId = "%dC%s";
	static final String templateId = "%dT%s";
	static final String namedTypeId = "%dW%s";

	static final String quoteId = "%dY%s";

	static final String unionId = "%dX%s";

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

		//Output map of model names to mangled names.
		if(method.getAncestor(SClassDeclIR.class) != null)			
			mangledNames.put(method.getAncestor(SClassDeclIR.class) + "_" + method.getName(), name);
		
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

	public static class NameGenerator extends
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
		public String caseATemplateTypeIR(ATemplateTypeIR node)
				throws AnalysisException
		{
			String name = node.getName();
			return String.format(templateId, name.length(), name);
		}

		@Override
		public String caseAQuoteTypeIR(AQuoteTypeIR node)
				throws AnalysisException
		{
			String value = node.getValue();
			return String.format(quoteId, value.length(), value);
		}

		@Override
		public String caseANamedTypeDeclIR(ANamedTypeDeclIR node)
				throws AnalysisException
		{
			String name = node.getName().getDefiningClass() + "_" + node.getName().getName();

			return String.format(namedTypeId, name.length(), name);
		}

		@Override
		public String caseANatNumericBasicTypeIR(ANatNumericBasicTypeIR node)
				throws AnalysisException
		{
			return natId;
		}

		@Override
		public String caseANat1NumericBasicTypeIR(ANat1NumericBasicTypeIR node)
				throws AnalysisException
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
		public String caseAUnknownTypeIR(AUnknownTypeIR node)
				throws AnalysisException
		{
			return unknownId;
		}
		
		@Override
		public String caseAVoidTypeIR(AVoidTypeIR node) throws AnalysisException
		{
			return voidId;
		}
		
		@Override
		public String caseARatNumericBasicTypeIR(ARatNumericBasicTypeIR node)
				throws AnalysisException
		{
			return ratId;
		}
		
		@Override
		public String caseATokenBasicTypeIR(ATokenBasicTypeIR node) throws AnalysisException {
			
			return tokenId;
		}

		@Override
		public String caseAExternalTypeIR(AExternalTypeIR node)
				throws AnalysisException
		{
			return "";
		}
		
		@Override
		public String caseASetSetTypeIR(ASetSetTypeIR node)
				throws AnalysisException
		{
			//TODO: Update IR to support set1
			org.overture.ast.node.INode source = AssistantBase.getVdmNode(node);
			
			String id = source instanceof ASetSetType ? setId : set1Id;
			
			String name = node.getSetOf().apply(THIS);
			return String.format(id, name.length(), name);
		}
		
		@Override
		public String caseASeqSeqTypeIR(ASeqSeqTypeIR node)
				throws AnalysisException
		{
			String name = node.getSeqOf().apply(THIS);
			return String.format(node.getSeq1() ? seq1Id : seqId, name.length(), name);
		}
		
		@Override
		public String caseAMapMapTypeIR(AMapMapTypeIR node)
				throws AnalysisException
		{
			String from = node.getFrom().apply(THIS);
			String to = node.getTo().apply(THIS);
			
			return String.format(mapId, from.length() + to.length(), from, to);
		}

		@Override
		public String caseAUnionTypeIR(AUnionTypeIR node)
				throws AnalysisException
		{
			StringBuilder sb = new StringBuilder();

			for(STypeIR t : node.getTypes())
			{
				sb.append(t.apply(THIS));
			}

			String name = sb.toString();

			return String.format(unionId, name.length(), name);
		}

		@Override
		public String defaultInINode(INode node) throws AnalysisException
		{
			logger.debug("Mangling not handled: {}", node.getClass().getName());
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
