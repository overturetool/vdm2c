package org.overture.codegen.vdm2c;

/*
 import static org.overture.codegen.ir.CodeGenBase.EVAL_METHOD_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.INTERFACE_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.PARAM_NAME_PREFIX;
 import static org.overture.codegen.ir.CodeGenBase.TEMPLATE_TYPE_PREFIX;

 import static org.overture.codegen.ir.CodeGenBase;
 */
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.AtomicStmTrans;
import org.overture.codegen.trans.CallObjStmTrans;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.LetBeStTrans;
import org.overture.codegen.trans.WhileStmTrans;
//import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.funcvalues.FuncValPrefixes;
import org.overture.codegen.trans.funcvalues.FuncValTrans;
import org.overture.codegen.trans.iterator.AbstractLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTrans;
import org.overture.codegen.trans.letexps.IfExpTrans;
import org.overture.codegen.trans.patterns.PatternTrans;
import org.overture.codegen.trans.patterns.PatternVarPrefixes;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.vdm2c.transformations.AddThisArgToMethodsTrans;
import org.overture.codegen.vdm2c.transformations.CExp2StmTrans;
import org.overture.codegen.vdm2c.transformations.CallRewriteTrans;
import org.overture.codegen.vdm2c.transformations.CtorTrans;
import org.overture.codegen.vdm2c.transformations.ExtractRetValTrans;
import org.overture.codegen.vdm2c.transformations.FieldIdentifierToFieldGetApplyTrans;
import org.overture.codegen.vdm2c.transformations.ForLoopTrans;
import org.overture.codegen.vdm2c.transformations.IfTrans;
import org.overture.codegen.vdm2c.transformations.IgnoreRenamingTrans;
import org.overture.codegen.vdm2c.transformations.InitializerExtractorTrans;
import org.overture.codegen.vdm2c.transformations.LetTrans;
import org.overture.codegen.vdm2c.transformations.LiteralInstantiationRewriteTrans;
import org.overture.codegen.vdm2c.transformations.LogicTrans;
import org.overture.codegen.vdm2c.transformations.MangleMethodNamesTrans;
import org.overture.codegen.vdm2c.transformations.NewRewriteTrans;
import org.overture.codegen.vdm2c.transformations.NumericTrans;
import org.overture.codegen.vdm2c.transformations.RemoveCWrappersTrans;
import org.overture.codegen.vdm2c.transformations.RemoveRTConstructs;
import org.overture.codegen.vdm2c.transformations.RenameValueFieldsTrans;
import org.overture.codegen.vdm2c.transformations.ScopeCleanerTrans;
import org.overture.codegen.vdm2c.transformations.SubClassResponsibilityMethodsTrans;
import org.overture.codegen.vdm2c.transformations.ValueAccessRenameTrans;
import org.overture.codegen.vdm2c.transformations.VisualizeIRAST;
import org.overture.codegen.vdm2c.transformations.FreeBeforeReturn;

public class CTransSeries
{

	private CGen codeGen;

	public CTransSeries(CGen codeGen)
	{
		this.codeGen = codeGen;
	}

	private Exists1CounterData consExists1CounterData()
	{
		AExternalTypeIR type = new AExternalTypeIR();
		type.setName("Long");

		IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		AIntLiteralExpIR initExp = irInfo.getExpAssistant().consIntLiteral(0);

		return new Exists1CounterData(type, initExp);
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses()
	{
		// Data and functionality to support the transformations
		// IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		// TempVarPrefixes varPrefixes = codeGen.getTempVarPrefixes();
		// ITempVarGen nameGen = irInfo.getTempVarNameGen();
		// TraceNames traceNamePrefixes = codeGen.getTracePrefixes();
		TransAssistantIR transAssistant = codeGen.getTransAssistant();
		// IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		// Set up order of transformations
		List<DepthFirstAnalysisAdaptor> transformations = new LinkedList<DepthFirstAnalysisAdaptor>();

		/**
		 * Phase #0 - initial construction / merge of nodes<br/>
		 * - Do weeding here
		 */
		// Construct the transformations
		transformations.add(new FuncTrans(transAssistant));
		transformations.add(new InitializerExtractorTrans(transAssistant));
		transformations.add(new RemoveRTConstructs(transAssistant));

		// Data and functionality to support the transformations
		IRInfo info = codeGen.getIRGenerator().getIRInfo();
		VarPrefixManager varMan = new VarPrefixManager();
		IterationVarPrefixes iteVarPrefixes = varMan.getIteVarPrefixes();
		Exp2StmVarPrefixes exp2stmPrefixes = varMan.getExp2stmPrefixes();
		FuncValPrefixes funcValPrefixes = varMan.getFuncValPrefixes();
		PatternVarPrefixes patternPrefixes = varMan.getPatternPrefixes();
		// UnionTypeVarPrefixes unionTypePrefixes = varMan.getUnionTypePrefixes();
		// List<INode> cloneFreeNodes = codeGen.getJavaFormat().getValueSemantics().getCloneFreeNodes();

		// IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(varMan.postCheckMethodName());

		// Construct the transformations
		transformations.add(new AtomicStmTrans(transAssistant, varMan.atomicTmpVar()));
		transformations.add(new FuncTrans(transAssistant));
		transformations.add(new DivideTrans(info));
		transformations.add(new CallObjStmTrans(info));
		transformations.add(new AssignStmTrans(transAssistant));
		// PrePostTrans prePostTr = new PrePostTrans(info);
		transformations.add(new IfExpTrans(transAssistant));
		FuncValAssistant funcValAssist = new FuncValAssistant();
		transformations.add(new FuncValTrans(transAssistant, funcValAssist, funcValPrefixes));
		// ILanguageIterator langIte = new JavaLanguageIterator(transAssist, iteVarPrefixes);
		AbstractLanguageIterator langIte = new CForIterator(transAssistant, iteVarPrefixes);
		transformations.add(new LetBeStTrans(transAssistant, langIte, iteVarPrefixes));

		transformations.add(new WhileStmTrans(transAssistant, varMan.whileCond()));
		transformations.add(new CExp2StmTrans(iteVarPrefixes, transAssistant, consExists1CounterData(), langIte, exp2stmPrefixes));
		transformations.add(new PatternTrans(iteVarPrefixes, transAssistant, patternPrefixes, varMan.casesExp()));
		// transformations.add(new RemoveSetCompAddTrans(transAssist));

		/* C transformations */

		/**
		 * Phase #1 - Rewrite all standard C nodes to match C 1-to-1<br/>
		 * - Rewrite e.g. 1 + 2 to vdmSum(1,2) instead.
		 */
		transformations.add(new NumericTrans(transAssistant));
		transformations.add(new LogicTrans(transAssistant));
		transformations.add(new LiteralInstantiationRewriteTrans(transAssistant));
		transformations.add(new RenameValueFieldsTrans(transAssistant));
		transformations.add(new ValueAccessRenameTrans(transAssistant));
		transformations.add(new LetTrans(transAssistant));

		/**
		 * Phase #2 - Not defined yet.
		 */
		transformations.add(new AddThisArgToMethodsTrans(transAssistant));
		transformations.add(new MangleMethodNamesTrans(transAssistant));

		transformations.add(new CallRewriteTrans(transAssistant));
		transformations.add(new ExtractRetValTrans(transAssistant));
		transformations.add(new FieldIdentifierToFieldGetApplyTrans(transAssistant));
		transformations.add(new CtorTrans(transAssistant));
		transformations.add(new NewRewriteTrans(transAssistant));
		transformations.add(new IgnoreRenamingTrans(transAssistant));
		transformations.add(new ForLoopTrans(transAssistant));
		transformations.add(new IfTrans(transAssistant));
		transformations.add(new SubClassResponsibilityMethodsTrans(transAssistant));

		/**
		 * Phase #X - Remove any temporary nodes
		 */
		transformations.add(new RemoveCWrappersTrans(transAssistant));
		transformations.add(new ScopeCleanerTrans(transAssistant));
		//transformations.add(new VisualizeIRAST(transAssistant));
		transformations.add(new FreeBeforeReturn(transAssistant));
		
		return transformations;
	}

}
