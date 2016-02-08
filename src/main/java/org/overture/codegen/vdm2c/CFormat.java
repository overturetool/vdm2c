package org.overture.codegen.vdm2c;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.expressions.SBinaryExpIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.SMapTypeIR;
import org.overture.codegen.ir.types.SSeqTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;

public class CFormat
{

	final IHeaderFinder headerFinder;
	private MergeVisitor mergeVisitor;
	private IRInfo info;
	private int number = 0;

	private long nextClassId = 0;
	private Map<String, Long> classIds = new HashMap<String, Long>();

	public Long getClassId(String name)
	{
		if (classIds.containsKey(name))
			return classIds.get(name);

		Long id = nextClassId;
		classIds.put(name, id);
		nextClassId++;
		return id;

	}

	public AClassHeaderDeclIR getHeader(SClassDeclIR def)
	{
		return headerFinder.getHeader(def);
	}

	public static final String UTILS_FILE = "Utils";

	public String getNumber()
	{
		number = number + 1;

		return Integer.toString(number - 1);
	}

	public CFormat(IRInfo info, IHeaderFinder headerfinder)
	{
		this.headerFinder = headerfinder;
		TemplateManager tm = new TemplateManager("c-templates");

		TemplateCallable[] templateCallables = new TemplateCallable[] {
				new TemplateCallable("CFormat", this),
				new TemplateCallable("String", new String()) };
		this.mergeVisitor = new MergeVisitor(tm, templateCallables);
		this.info = info;
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

	/**
	 * This method is intended to be used for debugging. Changing the {@link #format(INode)} call in the template to
	 * {@link #debug(INode)} make it possible to set a breakpoint here
	 * 
	 * @param node
	 * @return
	 * @throws AnalysisException
	 */
	public String debug(INode node) throws AnalysisException
	{
		return format(node);
	}

	public boolean isSeqType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isSeqType(exp);
	}

	// public Boolean isClassType(AFormalParamLocalParamIR fp)
	// {
	// return fp.getTag() == "class";
	// }
	//
	// public String getEnclosingClass(AFormalParamLocalParamIR fp)
	// {
	// return fp.getAncestor(ADefaultClassDeclIR.class).getName().toString()
	// + "CLASS";
	// }

	public String format(SExpIR exp, boolean leftChild)
			throws AnalysisException
	{
		String formattedExp = format(exp);

		CPrecedence precedence = new CPrecedence();

		INode parent = exp.parent();

		if (!(parent instanceof SExpIR))
		{
			return formattedExp;
		}

		boolean isolate = precedence.mustIsolate((SExpIR) parent, exp, leftChild);

		return isolate ? "(" + formattedExp + ")" : formattedExp;
	}

	public MergeVisitor GetMergeVisitor()
	{
		return mergeVisitor;
	}

	public String formatOperationBody(SStmIR body) throws AnalysisException
	{
		String NEWLINE = "\n";
		if (body == null)
		{
			return ";";
		}

		StringWriter generatedBody = new StringWriter();

		generatedBody.append("{" + NEWLINE + NEWLINE);
		generatedBody.append(handleOpBody(body));
		generatedBody.append(NEWLINE + "}");

		return generatedBody.toString();
	}

	private String handleOpBody(SStmIR body) throws AnalysisException
	{
		AMethodDeclIR method = body.getAncestor(AMethodDeclIR.class);

		if (method == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing method when formatting operation body. Got: "
					+ body);
		} else if (method.getAsync() != null && method.getAsync())
		{
			return "new VDMThread(){ " + "\tpublic void run() {" + "\t "
					+ format(body) + "\t} " + "}.start();";
		}

		return format(body);
	}

	public String format(List<AFormalParamLocalParamIR> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0)
		{
			return "";
		}

		AFormalParamLocalParamIR firstParam = params.get(0);
		writer.append(format(firstParam));

		for (int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalParamIR param = params.get(i);
			writer.append(", ");
			writer.append(format(param));
		}
		return writer.toString();
	}

	public boolean isClass(INode node)
	{
		return node != null && node instanceof ADefaultClassDeclIR;
	}

	public String formatArgs(List<? extends SExpIR> exps)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (exps.size() <= 0)
		{
			return "";
		}

		SExpIR firstExp = exps.get(0);
		writer.append(format(firstExp));

		for (int i = 1; i < exps.size(); i++)
		{
			SExpIR exp = exps.get(i);
			writer.append(", " + format(exp));
		}

		return writer.toString();
	}

	public List<AMethodDeclIR> getMethodsByAccess(List<AMethodDeclIR> methods,
			String access)
	{
		LinkedList<AMethodDeclIR> matches = new LinkedList<AMethodDeclIR>();

		for (AMethodDeclIR m : methods)
		{
			if (m.getAccess().equals(access))
			{
				matches.add(m);
			}
		}

		return matches;
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	public String escapeChar(char c)
	{
		return GeneralUtils.isEscapeSequence(c) ? StringEscapeUtils.escapeJavaScript(c
				+ "")
				: c + "";
	}

	public List<AFieldDeclIR> getFieldsByAccess(List<AFieldDeclIR> fields,
			String access)
	{
		LinkedList<AFieldDeclIR> matches = new LinkedList<AFieldDeclIR>();

		for (AFieldDeclIR f : fields)
		{
			if (f.getAccess().equals(access))
			{
				matches.add(f);
			}
		}

		return matches;
	}

	public String formatEqualsBinaryExp(AEqualsBinaryExpIR node)
			throws AnalysisException
	{
		STypeIR leftNodeType = node.getLeft().getType();

		if (leftNodeType instanceof SSeqTypeIR
				|| leftNodeType instanceof SSetTypeIR
				|| leftNodeType instanceof SMapTypeIR)
		{
			return handleCollectionComparison(node);
		} else
		{
			return handleEquals(node);
		}
	}

	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpIR node)
			throws AnalysisException
	{
		ANotUnaryExpIR transformed = transNotEquals(node);
		return formatNotUnary(transformed.getExp());
	}

	public String formatNotUnary(SExpIR exp) throws AnalysisException
	{
		String formattedExp = format(exp, false);

		boolean doNotWrap = exp instanceof ABoolLiteralExpIR
				|| formattedExp.startsWith("(") && formattedExp.endsWith(")");

		return doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
	}

	private ANotUnaryExpIR transNotEquals(ANotEqualsBinaryExpIR notEqual)
	{
		ANotUnaryExpIR notUnary = new ANotUnaryExpIR();
		notUnary.setType(new ABoolBasicTypeIR());

		AEqualsBinaryExpIR equal = new AEqualsBinaryExpIR();
		equal.setType(new ABoolBasicTypeIR());
		equal.setLeft(notEqual.getLeft().clone());
		equal.setRight(notEqual.getRight().clone());

		notUnary.setExp(equal);

		// Replace the "notEqual" expression with the transformed expression
		INode parent = notEqual.parent();

		// It may be the case that the parent is null if we execute e.g. [1] <>
		// [1] in isolation
		if (parent != null)
		{
			parent.replaceChild(notEqual, notUnary);
			notEqual.parent(null);
		}

		return notUnary;
	}

	private String handleEquals(AEqualsBinaryExpIR valueType)
			throws AnalysisException
	{
		return String.format("%s.equals(%s, %s)", UTILS_FILE, format(valueType.getLeft()), format(valueType.getRight()));
	}

	private String handleCollectionComparison(SBinaryExpIR node)
			throws AnalysisException
	{
		// In VDM the types of the equals are compatible when the AST passes the
		// type check
		SExpIR leftNode = node.getLeft();
		SExpIR rightNode = node.getRight();

		final String EMPTY = ".isEmpty()";

		if (isEmptyCollection(leftNode.getType()))
		{
			return format(node.getRight()) + EMPTY;
		} else if (isEmptyCollection(rightNode.getType()))
		{
			return format(node.getLeft()) + EMPTY;
		}

		return UTILS_FILE + ".equals(" + format(node.getLeft()) + ", "
				+ format(node.getRight()) + ")";
	}

	private boolean isEmptyCollection(STypeIR type)
	{
		if (type instanceof SSeqTypeIR)
		{
			SSeqTypeIR seq = (SSeqTypeIR) type;

			return seq.getEmpty();
		} else if (type instanceof SSetTypeIR)
		{
			SSetTypeIR set = (SSetTypeIR) type;

			return set.getEmpty();
		} else if (type instanceof SMapTypeIR)
		{
			SMapTypeIR map = (SMapTypeIR) type;

			return map.getEmpty();
		}

		return false;
	}

	public String getClassName(SClassDeclIR cl)
	{
		return cl.getName().toString();
	}

	public boolean isPublic(AMethodDeclIR method)
	{
		return "public".equals(method.getAccess());
	}

}