package org.overture.codegen.vdm2c.transformations;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.patterns.ASeqMultipleBindIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ALetBeStStmIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;
import org.overture.codegen.vdm2c.utils.CLetBeStStrategy;

/**
 * TODO: PJ: Make the platform version of this transformation extensible so that
 * this transformation inherit from that one. The problem with the platform
 * transformation is that some of the members are private and that the platform
 * version of this transformation instantiates iteration strategies using direct
 * constructor calls. Really this should be done via public method to promote
 * extensibility. The problem is exactly the same for comprehensions, and
 * perhaps other transformations as well.
 */
public class CLetBeStStmTrans extends DepthFirstAnalysisAdaptor {
	private TransAssistantIR transAssistant;
	private ILanguageIterator langIterator;
	private IterationVarPrefixes iteVarPrefixes;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public CLetBeStStmTrans(TransAssistantIR transAssistant, ILanguageIterator langIterator,
			IterationVarPrefixes iteVarPrefixes) {
		this.transAssistant = transAssistant;
		this.langIterator = langIterator;
		this.iteVarPrefixes = iteVarPrefixes;
	}

	@Override
	public void caseALetBeStStmIR(ALetBeStStmIR node) throws AnalysisException {
		AHeaderLetBeStIR header = node.getHeader();

		if (!(header.getBinding() instanceof ASetMultipleBindIR || header.getBinding() instanceof ASeqMultipleBindIR)) {
			transAssistant.getInfo().addTransformationWarning(node.getHeader().getBinding(),
					"This transformation only works for 'let be st' "
							+ "statements with with multiple set binds and not multiple type binds in '"
							+ this.getClass().getSimpleName() + "'");
			return;
		}

		SExpIR suchThat = header.getSuchThat();
		SMultipleBindIR binding = node.getHeader().getBinding();

		STypeIR setSeqType;
		if (binding instanceof ASetMultipleBindIR) {
			ASetMultipleBindIR sb = (ASetMultipleBindIR) binding;
			setSeqType = sb.getSet().getType().clone();
		} else if (binding instanceof ASeqMultipleBindIR) {
			ASeqMultipleBindIR sb = (ASeqMultipleBindIR) binding;
			setSeqType = sb.getSeq().getType().clone();
		} else {
			log.error("Expected multiple set bind or multiple sequence bind. Got: " + binding);
			setSeqType = new AUnknownTypeIR();

			// The closest we get
			setSeqType.setSourceNode(binding.getSourceNode());
		}

		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();

		LetBeStStrategy strategy = new CLetBeStStrategy(transAssistant, suchThat, setSeqType.clone(), langIterator,
				tempVarNameGen, iteVarPrefixes);

		if (transAssistant.hasEmptySet(binding)) {
			transAssistant.cleanUpBinding(binding);
			node.setStatement(new ABlockStmIR());
		}

		LinkedList<SPatternIR> patterns = binding.getPatterns();
		ABlockStmIR outerBlock = transAssistant.consIterationBlock(patterns, getCol(binding), tempVarNameGen, strategy,
				iteVarPrefixes);

		// Only the statement of the let be st statement is added to the outer
		// block statements.
		// We obtain the equivalent functionality of the remaining part of the
		// let be st statement
		// from the transformation in the outer block
		outerBlock.getStatements().add(node.getStatement());

		// Replace the let be st statement with the transformation
		transAssistant.replaceNodeWithRecursively(node, outerBlock, this);

		outerBlock.setScoped(transAssistant.getInfo().getStmAssistant().isScoped(outerBlock));
	}

	private SExpIR getCol(SMultipleBindIR binding) {
		if (binding instanceof ASetMultipleBindIR) {
			return ((ASetMultipleBindIR) binding).getSet();
		} else if (binding instanceof ASeqMultipleBindIR) {
			return ((ASeqMultipleBindIR) binding).getSeq();
		} else {
			log.error("Expected multiple set bind or multiple sequence bind. Got: " + binding);
			return null;
		}
	}
}
