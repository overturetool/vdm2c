package org.overture.codegen.vdm2c.transformations;

import java.util.HashMap;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GarbageCollectionTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(GarbageCollectionTrans.class);

	private HashMap<String, String> gcNames;

	private TransAssistantIR assist;

	public GarbageCollectionTrans(TransAssistantIR assist)
	{
		this.gcNames = new HashMap<>();
		this.assist = assist;
		initNames();
	}

	public void initNames()
	{
		// Numeric unary operators
		gcNames.put(NumericTrans.VDM_MINUS, "vdmMinusGC");
		gcNames.put(NumericTrans.VDM_ABS, "vdmAbsGC");
		gcNames.put(NumericTrans.VDM_FLOOR, "vdmFloorGC");
		
		// Numeric binary expressions
		gcNames.put(NumericTrans.VDM_SUM, "vdmSumGC");
		gcNames.put(NumericTrans.VDM_DIFFERENCE, "vdmDifferenceGC");
		gcNames.put(NumericTrans.VDM_PRODUCT, "vdmProductGC");
		gcNames.put(NumericTrans.VDM_DIVISION, "vdmDivisionGC");
		gcNames.put(NumericTrans.VDM_DIV, "vdmDivGC");
		gcNames.put(NumericTrans.VDM_REM, "vdmRemGC");
		gcNames.put(NumericTrans.VDM_MOD, "vdmModGC");
		gcNames.put(NumericTrans.VDM_POW, "vdmPowerGC");
		
		// Numeric comparison
		gcNames.put(NumericTrans.VDM_GREATER_THAN, "vdmGreaterThanGC");
		gcNames.put(NumericTrans.VDM_GREATER_OR_EQUAL, "vdmGreaterOrEqualGC");
		gcNames.put(NumericTrans.VDM_LESS_THAN, "vdmLessThanGC");
		gcNames.put(NumericTrans.VDM_LESS_OR_EQUAL, "vdmLessOrEqualGC");
		
		// Equality
		gcNames.put(LogicTrans.VDM_EQUALS, "vdmEqualsGC");
		
		// Value constructors
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_INT, "newIntGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_BOOL, "newBoolGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_REAL, "newRealGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_CHAR, "newCharGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_QUOTE, "newQuoteGC");
	}

	@Override
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException
	{
		super.caseAApplyExpIR(node);

		if (node.getRoot() instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR id = (AIdentifierVarExpIR) node.getRoot();

			String val = gcNames.get(id.getName());

			if (val != null)
			{
				id.setName(val);
				node.getArgs().add(consAddr(node));
			}
		}
	}

	private SExpIR consAddr(SExpIR exp)
	{
		INode parent = exp.parent();
		
		if (parent instanceof AVarDeclIR)
		{
			SPatternIR pat = ((AVarDeclIR) parent).getPattern();

			if (pat instanceof AIdentifierPatternIR)
			{
				String name = ((AIdentifierPatternIR) pat).getName();
				AExternalExpIR addrOf = new AExternalExpIR();
				addrOf.setSourceNode(pat.getSourceNode());
				addrOf.setTargetLangExp("&" + name);
				return addrOf;
			}
			else
			{
				logger.error("Expected identifier pattern at this point.");
			}
		}

		// Expression is not associated with a memory address
		return assist.getInfo().getExpAssistant().consNullExp();
	}
}
