package org.overture.codegen.vdm2c.utils;

import java.util.HashSet;
import java.util.Set;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AQuoteLiteralExpIR;
import org.overture.codegen.ir.patterns.AQuotePatternIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;

/**
 * This class provides a collecting search which will find all quote names in the visited tree
 * 
 * @author kel
 */
public class QuoteNamesCollector extends DepthFirstAnalysisCAdaptor
{
	Set<String> quotes = new HashSet<String>();

	public Set<String> getQuotes()
	{
		return this.quotes;
	}

	@Override
	public void caseAQuoteLiteralExpIR(AQuoteLiteralExpIR node)
			throws AnalysisException
	{
		quotes.add(node.getValue());
	}

	@Override
	public void caseAQuoteTypeIR(AQuoteTypeIR node) throws AnalysisException
	{
		quotes.add(node.getValue());
	}

	@Override
	public void caseAQuotePatternIR(AQuotePatternIR node)
			throws AnalysisException
	{
		quotes.add(node.getValue());
	}
}
