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

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.AtomicStmTrans;
import org.overture.codegen.trans.CallObjStmTrans;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.WhileStmTrans;
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
import org.overture.codegen.vdm2c.transformations.CLetBeStStmTrans;
import org.overture.codegen.vdm2c.transformations.CallRewriteTrans;
import org.overture.codegen.vdm2c.transformations.CompToBlockTrans;
import org.overture.codegen.vdm2c.transformations.CreateGlobalConstInitFunctionTrans;
import org.overture.codegen.vdm2c.transformations.CreateGlobalStaticInitFunctionTrans;
import org.overture.codegen.vdm2c.transformations.CtorTrans;
import org.overture.codegen.vdm2c.transformations.ExtractEmbeddedCreationsTrans;
import org.overture.codegen.vdm2c.transformations.ExtractRetValTrans;
import org.overture.codegen.vdm2c.transformations.FieldAssignToFieldSetMacroTrans;
import org.overture.codegen.vdm2c.transformations.FieldExpRewriteTrans;
import org.overture.codegen.vdm2c.transformations.FieldInitializerExtractorTrans;
import org.overture.codegen.vdm2c.transformations.FieldReadToFieldGetMacroTrans;
import org.overture.codegen.vdm2c.transformations.ForLoopTrans;
import org.overture.codegen.vdm2c.transformations.GarbageCollectionTrans;
import org.overture.codegen.vdm2c.transformations.IfTrans;
import org.overture.codegen.vdm2c.transformations.IgnoreRenamingTrans;
import org.overture.codegen.vdm2c.transformations.IsNotYetSpecifiedTrans;
import org.overture.codegen.vdm2c.transformations.LetTrans;
import org.overture.codegen.vdm2c.transformations.LiteralInstantiationRewriteTrans;
import org.overture.codegen.vdm2c.transformations.LogicTrans;
import org.overture.codegen.vdm2c.transformations.MangleMethodNamesTrans;
import org.overture.codegen.vdm2c.transformations.MapSeqUpdateRewriteTrans;
import org.overture.codegen.vdm2c.transformations.MethodReturnInsertTrans;
import org.overture.codegen.vdm2c.transformations.MethodVisibilityTrans;
import org.overture.codegen.vdm2c.transformations.NewRewriteTrans;
import org.overture.codegen.vdm2c.transformations.NumericTrans;
import org.overture.codegen.vdm2c.transformations.RecTypeToClassTypeTrans;
import org.overture.codegen.vdm2c.transformations.RemoveCWrappersTrans;
import org.overture.codegen.vdm2c.transformations.RemoveRTConstructs;
import org.overture.codegen.vdm2c.transformations.RenameFieldsDeclsTrans;
import org.overture.codegen.vdm2c.transformations.ScopeCleanerTrans;
import org.overture.codegen.vdm2c.transformations.SelfTrans;
import org.overture.codegen.vdm2c.transformations.StaticFieldAccessRenameTrans;
import org.overture.codegen.vdm2c.transformations.SubClassResponsibilityMethodsTrans;
import org.overture.codegen.vdm2c.transformations.ValueAccessRenameTrans;

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
		transformations.add(new RecTypeToClassTypeTrans(transAssistant));
		transformations.add(new FuncTrans(transAssistant));
		transformations.add(new FieldInitializerExtractorTrans(transAssistant));
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
		transformations.add(new DivideTrans(info));
		transformations.add(new CallObjStmTrans(info));
		//This only transforms assignments to fields.  Is it needed?
		transformations.add(new AssignStmTrans(transAssistant));
		// PrePostTrans prePostTr = new PrePostTrans(info);
		transformations.add(new IfExpTrans(transAssistant));
		
		FuncValAssistant funcValAssist = new FuncValAssistant();
		transformations.add(new FuncValTrans(transAssistant, funcValAssist, funcValPrefixes));
		
		// ILanguageIterator langIte = new JavaLanguageIterator(transAssist, iteVarPrefixes);
		AbstractLanguageIterator langIte = new CForIterator(transAssistant, iteVarPrefixes);
		transformations.add(new CLetBeStStmTrans(transAssistant, langIte, iteVarPrefixes));

		transformations.add(new WhileStmTrans(transAssistant, varMan.whileCond()));
		transformations.add(new CompToBlockTrans(iteVarPrefixes, transAssistant, consExists1CounterData(), langIte, exp2stmPrefixes));
		transformations.add(new PatternTrans(iteVarPrefixes, transAssistant, patternPrefixes, varMan.casesExp()));
		transformations.add(new MethodReturnInsertTrans(transAssistant));
		
		

		/**
		 * Phase #1 - Rewrite all standard C nodes to match C 1-to-1<br/>
		 * - Rewrite e.g. 1 + 2 to vdmSum(1,2) instead.
		 */
		transformations.add(new NumericTrans(transAssistant));
		transformations.add(new LogicTrans(transAssistant));
		transformations.add(new SeqTrans(transAssistant));
		transformations.add(new LiteralInstantiationRewriteTrans(transAssistant));
		transformations.add(new RenameFieldsDeclsTrans(transAssistant));
		transformations.add(new FieldExpRewriteTrans(transAssistant));
		transformations.add(new StaticFieldAccessRenameTrans(transAssistant));
		transformations.add(new LetTrans(transAssistant));

		
		
		/**
		 * Phase #2 - Not defined yet.
		 */
		transformations.add(new CreateGlobalConstInitFunctionTrans(transAssistant));
		transformations.add(new CreateGlobalStaticInitFunctionTrans(transAssistant));
		transformations.add(new AddThisArgToMethodsTrans(transAssistant));
		transformations.add(new MangleMethodNamesTrans(transAssistant));
		transformations.add(new IsNotYetSpecifiedTrans(transAssistant));
		transformations.add(new CallRewriteTrans(transAssistant));
		transformations.add(new ExtractRetValTrans(transAssistant));
		transformations.add(new FieldAssignToFieldSetMacroTrans(transAssistant));
		transformations.add(new FieldReadToFieldGetMacroTrans(transAssistant));
		transformations.add(new ValueAccessRenameTrans(transAssistant));
		transformations.add(new CtorTrans(transAssistant));
		transformations.add(new NewRewriteTrans(transAssistant));
		transformations.add(new IgnoreRenamingTrans(transAssistant));
		transformations.add(new ForLoopTrans(transAssistant));
		transformations.add(new IfTrans(transAssistant));
		transformations.add(new MapSeqUpdateRewriteTrans(transAssistant));
		transformations.add(new SubClassResponsibilityMethodsTrans(transAssistant));
		transformations.add(new ScopeCleanerTrans(transAssistant));
		
//		ExtractEmbeddedCreationsTrans requires that blocks doesn't have any local decelerations but that all is statements
		transformations.add(new ExtractEmbeddedCreationsTrans(transAssistant));

		transformations.add(new SelfTrans(transAssistant));
		
		/**
		 * Phase #X - Remove any temporary nodes
		 */
		transformations.add(new RemoveCWrappersTrans(transAssistant));
		transformations.add(new MethodVisibilityTrans(transAssistant));

		if(codeGen.getCGenSettings().usesGarbageCollection())
		{
			transformations.add(new GarbageCollectionTrans(transAssistant));
		}

		return transformations;
	}

}
