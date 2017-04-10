package org.overture.codegen.vdm2c;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AMapletExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;

public class CFormat
{
	protected static Logger log = Logger.getLogger(CFormat.class.getName());
	
	final IHeaderFinder headerFinder;
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

	public AClassHeaderDeclIR getHeader(SClassDeclIR def)
	{
		return headerFinder.getHeader(def);
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
	
	public String formatUnary(SExpIR exp) throws AnalysisException
	{
		return format(exp, false);
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

	public boolean hasMethodTagHeaderOnly(AMethodDeclIR method)
	{
		return method.getTag() instanceof Vdm2cTag
				&& ((Vdm2cTag) method.getTag()).methodTags.contains(Vdm2cTag.MethodTag.HeaderOnly);
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
	
	public boolean isMapType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isMapType(exp);
	}
	
	public static String getVersion()
	{
	    String path = "/version.prop";
	    InputStream stream = CGenMain.class.getResourceAsStream(path);
	    if (stream == null)
	        return "UNKNOWN";
	    Properties props = new Properties();
	    try {
	        props.load(stream);
	        stream.close();
	        return (String) props.get("version");
	    } catch (IOException e) {
	        return "UNKNOWN";
	    }
	}
	
	public static String getGeneratedFileComment() {
		return "/**\n" + "* This file is generated by Overture's VDM-to-C code generator version " + getVersion()
				+ ".\n" + "* Website: https://github.com/overturetool/vdm2c\n" + "*/\n";
	}
}
