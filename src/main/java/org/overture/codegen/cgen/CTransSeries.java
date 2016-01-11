package org.overture.codegen.cgen;

/*
 import static org.overture.codegen.ir.CodeGenBase.EVAL_METHOD_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.INTERFACE_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.PARAM_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.TEMPLATE_TYPE_PREFIX;

 import static org.overture.codegen.ir.CodeGenBase;
 */
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgen.transformations.ExtractRetValTrans;
import org.overture.codegen.cgen.transformations.FieldIdentifierToFieldGetApplyTrans;
import org.overture.codegen.cgen.transformations.MethodParamTransformation;
//import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.letexps.FuncTrans;

public class CTransSeries
{

	private CGen codeGen;

	public CTransSeries(CGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses(
			List<ADefaultClassDeclCG> classes,
			FuncValAssistant functionValueAssistant)
	{
		// Data and functionality to support the transformations
		// IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		// TempVarPrefixes varPrefixes = codeGen.getTempVarPrefixes();
		// ITempVarGen nameGen = irInfo.getTempVarNameGen();
		// TraceNames traceNamePrefixes = codeGen.getTracePrefixes();
		TransAssistantCG transAssistant = codeGen.getTransAssistant();
		// IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		// Set up order of transformations
		List<DepthFirstAnalysisAdaptor> transformations = new LinkedList<DepthFirstAnalysisAdaptor>();

		// VdmBasicTypesCppTrans typeTrans = new VdmBasicTypesCppTrans(transAssistant);
		// VdmSetCppTrans setTrans = new VdmSetCppTrans(transAssistant);
		// VdmSeqCppTrans seqTrans = new VdmSeqCppTrans(transAssistant);

		// Construct the transformations
		transformations.add(new FuncTrans(transAssistant));
		// DivideTransformation divideTrans = new DivideTransformation(irInfo);
		// CallObjStmTrans callObjTransformation = new CallObjStmTrans(irInfo);
		// AssignStmTrans assignTransformation = new AssignStmTrans(transAssistant);
		// PrePostTransformation prePostTransformation = new PrePostTransformation(irInfo);
		// IfExpTransformation ifExpTransformation = new IfExpTransformation(transAssistant);
		// FunctionValueTransformation funcValueTransformation = new FunctionValueTransformation(irInfo, transAssistant,
		// functionValueAssistant, INTERFACE_NAME_PREFIX, TEMPLATE_TYPE_PREFIX, EVAL_METHOD_PREFIX, PARAM_NAME_PREFIX);
		// ILanguageIterator langIterator = new JavaLanguageIterator(transAssistant, nameGen, varPrefixes);
		// TransformationVisitor transVisitor = new TransformationVisitor(irInfo, classes, varPrefixes, transAssistant,
		// consExists1CounterData(), langIterator, TERNARY_IF_EXP_NAME_PREFIX, CASES_EXP_RESULT_NAME_PREFIX,
		// AND_EXP_NAME_PREFIX, OR_EXP_NAME_PREFIX, WHILE_COND_NAME_PREFIX, REC_MODIFIER_NAME_PREFIX);
		// PatternTransformation patternTransformation = new PatternTransformation(classes, varPrefixes, irInfo,
		// transAssistant, new PatternMatchConfig(), CASES_EXP_NAME_PREFIX);
		// PreCheckTransformation preCheckTransformation = new PreCheckTransformation(irInfo, transAssistant, new
		// JavaValueSemanticsTag(false));
		// PostCheckTransformation postCheckTransformation = new PostCheckTransformation(postCheckCreator, irInfo,
		// transAssistant, FUNC_RESULT_NAME_PREFIX, new JavaValueSemanticsTag(false));
		// IsExpTransformation isExpTransformation = new IsExpTransformation(irInfo, transAssistant,
		// IS_EXP_SUBJECT_NAME_PREFIX);
		// SeqConversionTransformation seqConversionTransformation = new SeqConversionTransformation(transAssistant);
		// TracesTransformation tracesTransformation = new TracesTransformation(irInfo, classes, transAssistant,
		// varPrefixes, traceNamePrefixes, langIterator, new JavaCallStmToStringBuilder());
		// UnionTypeTransformation unionTypeTransformation = new UnionTypeTransformation(transAssistant, irInfo,
		// classes, APPLY_EXP_NAME_PREFIX, OBJ_EXP_NAME_PREFIX, CALL_STM_OBJ_NAME_PREFIX, MISSING_OP_MEMBER,
		// MISSING_MEMBER);
		// JavaClassToStringTrans javaToStringTransformation = new JavaClassToStringTrans(irInfo);
		// RecordMetodsTransformation recTransformation = new
		// RecordMetodsTransformation(codeGen.getJavaFormat().getRecCreator());

		// Start concurrency transformations
		// SentinelTransformation concurrencytransform = new SentinelTransformation(irInfo, classes);
		// MainClassConcTransformation mainclassTransform = new MainClassConcTransformation(irInfo, classes);
		// MutexDeclTransformation mutexTransform = new MutexDeclTransformation(irInfo, classes);
		// InstanceVarPPEvalTransformation instanceVarPPEval = new InstanceVarPPEvalTransformation(irInfo,
		// transAssistant, classes);
		// End concurrency transformations

		/* C transformations */

		transformations.add(new MethodParamTransformation(transAssistant));
		transformations.add(new ExtractRetValTrans(transAssistant));
		transformations.add(new FieldIdentifierToFieldGetApplyTrans(transAssistant));

		return transformations;
	}

}
