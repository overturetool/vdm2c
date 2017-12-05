package org.overture.codegen.vdm2asn1;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AMapletExpIR;
import org.overture.codegen.ir.expressions.ASelfExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2asn1.extast.declarations.AClassHeaderDeclIR;

public class CFormat
{
	protected static Logger log = Logger.getLogger(CFormat.class.getName());

	private MergeVisitor mergeVisitor;
	private IRInfo info;

	private long nextClassId = 0;
	private Map<String, Long> classIds = new HashMap<String, Long>();

	boolean printSourceNodeLocations = true;
	final static String commentPattern = "/* %s */";

	public Long getClassId(String name)
	{
		if (classIds.containsKey(name))
		{
			return classIds.get(name);
		}

		Long id = nextClassId;
		classIds.put(name, id);
		nextClassId++;
		return id;

	}


	public CFormat(IRInfo info)
	{
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

	public String formatTyDecl(ATypeDeclIR node) throws AnalysisException
	{	
		SDeclIR inv = node.getInv();

		String name = "";

		if(node.getDecl() instanceof ANamedTypeDeclIR){
			ANamedTypeDeclIR decl = (ANamedTypeDeclIR) node.getDecl() ;

			if(decl.getType() instanceof AIntNumericBasicTypeIR)
				name =  decl.getName().toString() + " ::= INTEGER";

			if(decl.getType() instanceof ASeqSeqTypeIR)
				name =  decl.getName().toString() + " ::= SEQUENCE SIZE (";
		}

		if(inv instanceof AFuncDeclIR){
			AFuncDeclIR i = (AFuncDeclIR) inv;

			SExpIR body = i.getBody();
			if(body instanceof AAndBoolBinaryExpIR){
				AAndBoolBinaryExpIR b = (AAndBoolBinaryExpIR) body;

				// Format left side
				SExpIR left = b.getLeft();
				if(left instanceof AGreaterEqualNumericBinaryExpIR){
					AGreaterEqualNumericBinaryExpIR l = (AGreaterEqualNumericBinaryExpIR) left;
					name = name + "( " + l.getRight().toString();
				}

				// Format right side
				SExpIR right = b.getRight();
				if(right instanceof ALessEqualNumericBinaryExpIR){
					ALessEqualNumericBinaryExpIR r = (ALessEqualNumericBinaryExpIR) right;
					name = name + " .. " + r.getRight().toString() + " )";
				}
			}
		}

		if(node.getDecl() instanceof ANamedTypeDeclIR){
			ANamedTypeDeclIR decl = (ANamedTypeDeclIR) node.getDecl() ;	
			STypeIR ty = decl.getType();
			if(ty instanceof ASeqSeqTypeIR){
				name = name + ") OF ";
				ASeqSeqTypeIR seqTy = (ASeqSeqTypeIR) ty;
				name = name + format(seqTy.getSeqOf());
				//name = name + "INTEGER";
			}

		}

		return name;
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

	public MergeVisitor GetMergeVisitor()
	{
		return mergeVisitor;
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

	public String formatArgs(List<SExpIR> exps)
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

	public String formatFields(List<AFieldDeclIR> exps)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (exps.size() <= 0)
		{
			return "";
		}

		AFieldDeclIR firstExp = exps.get(0);

		STypeIR ty = firstExp.getType();

		if(ty instanceof AIntNumericBasicTypeIR){
			if(ty.getNamedInvType() != null){
				writer.append(firstExp.getName() + " " + ty.getNamedInvType().getName().getName());
			}
			else
				writer.append(format(firstExp));
		}


		//writer.append(" \n ");
		for (int i = 1; i < exps.size(); i++)
		{
			AFieldDeclIR exp = exps.get(i);
			writer.append(", " + format(exp));
			//writer.append(" \n ");
		}

		return writer.toString();
	}

	public String formatMapArgs(List<AMapletExpIR> exps) throws AnalysisException
	{
		List<SExpIR> flattened = new LinkedList<>();

		for(AMapletExpIR e : exps)
		{
			flattened.add(e.getLeft());
			flattened.add(e.getRight());
		}

		return formatArgs(flattened);
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

	public boolean isPublic(AMethodDeclIR method)
	{
		return "public".equals(method.getAccess());
	}

	public String formatSource(PIR node)
	{
		if (printSourceNodeLocations && node.getSourceNode() != null
				&& node.getSourceNode().getVdmNode() != null)
		{
			org.overture.ast.node.INode vdmNode = node.getSourceNode().getVdmNode();
			try
			{
				Method getLocationMethod = vdmNode.getClass().getMethod("getLocation", new Class<?>[0]);
				if (getLocationMethod != null)
				{
					ILexLocation location = (ILexLocation) getLocationMethod.invoke(vdmNode, new Object[0]);
					if (location != null)
					{
						return String.format(commentPattern, String.format("%s %d:%d", location.getFile().getName(), location.getStartLine(), location.getStartPos()));
					}
				}
			} catch (NoSuchMethodException e)
			{
			} catch (SecurityException e)
			{
			} catch (IllegalAccessException e)
			{
			} catch (IllegalArgumentException e)
			{
			} catch (InvocationTargetException e)
			{
			}
		}
		return "";
	}

	public boolean isBlock(PIR node)
	{
		return node instanceof ABlockStmIR;
	}

	public String getVdmType(PIR node)
	{
		if(AssistantBase.getVdmNode(node) instanceof SClassDefinition)
		{
			return "VDM_CLASS";
		}

		return "VDM_RECORD";
	}

	/**
	 * Call as $CFormat.findObjectName($node)
	 * 
	 * @param self
	 * @return
	 */
	public static String findObjectName(ASelfExpIR self)
	{
		AMethodDeclIR enclosingMethod = self.getAncestor(AMethodDeclIR.class);

		if(enclosingMethod == null)
		{
			log.error("Expected self to have an enclosing method");
			return null;
		}

		if(enclosingMethod.getFormalParams().isEmpty())
		{
			log.error("Expected method to have parametes");
			return null;
		}

		AFormalParamLocalParamIR firstParam = enclosingMethod.getFormalParams().getFirst();

		STypeIR type = firstParam.getType();

		if(!(type instanceof AExternalTypeIR))
		{
			log.error("Expected external type by now");
			return null;
		}

		AExternalTypeIR extType = (AExternalTypeIR) type;

		// We do it like this because the name is just constructed on the fly
		// rather than being mangled
		return extType.getName().replaceFirst("CLASS$", "");
	}

	public boolean isMapType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isMapType(exp);
	}

}
