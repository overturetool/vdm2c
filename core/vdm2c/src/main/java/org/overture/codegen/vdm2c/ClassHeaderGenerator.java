package org.overture.codegen.vdm2c;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.BooleanUtils;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.statements.AIdentifierStateDesignatorIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.vdm2c.ast.CGenClonableString;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.AClassStateDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.AQuoteDeclIR;
import org.overture.codegen.vdm2c.utils.QuoteNamesCollector;

public class ClassHeaderGenerator
{
	public Collection<? extends IRStatus<PIR>> generateClassHeaders(
			List<IRStatus<ADefaultClassDeclIR>> extract)
			throws AnalysisException
	{
		final List<AClassHeaderDeclIR> classHeaders = new Vector<AClassHeaderDeclIR>();

		Collection<IRStatus<PIR>> list = new Vector<IRStatus<PIR>>();

		for (IRStatus<ADefaultClassDeclIR> irStatus : extract)
		{
			ADefaultClassDeclIR classDef = irStatus.getIrNode();

			AClassHeaderDeclIR header = new AClassHeaderDeclIR();
			
			header.setSourceNode(classDef.getSourceNode());

			header.setOriginalDef(classDef);

			String name = irStatus.getIrNode().getName();
			
			if(!(classDef.getTag()==null)){
				header.setTag(classDef.getTag().toString());
				name = classDef.getTag().toString();
				}

			AClassStateDeclIR state = new AClassStateDeclIR();
			for (AFieldDeclIR field : classDef.getFields())
			{
				if (field.getFinal() || field.getStatic())
				{
					header.getValues().add(field);
				} else
				{
					state.getFields().add(field);
				}
			}
			
			header.setState(state);

			List<CGenClonableString> includes = new Vector<CGenClonableString>();

			for (String typeName : collectIncludeTypes(classDef).keySet())
			{
				if(!typeName.contains("CPU") && !typeName.contains("BUS"))
				{
					includes.add(new CGenClonableString(typeName));
				}
			}
//			header.setIncludes(includes);
				
			
			//Insert include of time.h here.
			if(BooleanUtils.isTrue(CGen.hasTimeMap.get(classDef.getName().toString())))
			{
				includes.add(new CGenClonableString("time"));
			}
			
			header.setIncludes(includes);
			
			header.setName(classDef.getName().toString());
			
			QuoteNamesCollector quoteCollector = new QuoteNamesCollector();
			classDef.apply(quoteCollector);
			
			for (String quote : quoteCollector.getQuotes())
			{
				AQuoteDeclIR qDef = new AQuoteDeclIR();
				qDef.setName(quote);
				qDef.setId(quoteCollector.getId());
				header.getQuotes().add(qDef);
			}

			list.add(new IRStatus<PIR>(irStatus.getVdmNode(), name, header, new HashSet<VdmNodeInfo>()));
			classHeaders.add(header);
		}

		// not all headers are made. Now we need to sort out the inheritance

		for (AClassHeaderDeclIR header : classHeaders)
		{
			SClassDeclIR originalDef = header.getOriginalDef();
			LinkedList<ATokenNameIR> superNames = originalDef.getSuperNames();

			if (superNames.isEmpty())
			{
				continue;
			}

			header.setFlattenedSupers(getSupers(new HeaderProvider()
			{

				@Override
				public AClassHeaderDeclIR get(String name)
				{
					for (AClassHeaderDeclIR header : classHeaders)
					{
						if (header.getName().equals(name))
						{
							return header;
						}
					}
					return null;
				}
			}, header));
		}

		return list;
	}

	private Map<String, STypeIR> collectIncludeTypes(
			final ADefaultClassDeclIR classDef) throws AnalysisException
	{
		final Map<String, STypeIR> types = new HashMap<String, STypeIR>();

		classDef.apply(new DepthFirstAnalysisAdaptor()
		{
			void addType(String name, STypeIR type)
			{
				if (!types.containsKey(name))
				{
					types.put(name, type);
				}
			}

			@Override
			public void caseAClassTypeIR(AClassTypeIR node)
					throws AnalysisException
			{
				addType(node.getName(), node);
			}

			@Override
			public void caseARecordTypeIR(ARecordTypeIR node)
					throws AnalysisException
			{
				addType(node.getName().getName(), node);
			}
			
			@Override
			public void caseAIdentifierStateDesignatorIR(AIdentifierStateDesignatorIR node) throws AnalysisException {
			
				super.caseAIdentifierStateDesignatorIR(node);
				
				String className = node.getClassName();
				
				AClassTypeIR classType = new AClassTypeIR();
				classType.setSourceNode(node.getSourceNode());
				classType.setName(className);
				
				addType(className, classType);
			}
		});

		for (ATokenNameIR s : classDef.getSuperNames())
		{
			AClassTypeIR ct = new AClassTypeIR();
			ct.setName(s.getName());
			if (!types.containsKey(s.getName()))
			{
				types.put(s.getName(), ct);
			}
		}

		return types;
	}

	public interface HeaderProvider
	{
		public AClassHeaderDeclIR get(String name);
	}

	List<AClassHeaderDeclIR> getSupers(HeaderProvider provider,
			AClassHeaderDeclIR header)
	{
		List<AClassHeaderDeclIR> supers = new Vector<AClassHeaderDeclIR>();

		SClassDeclIR originalDef = header.getOriginalDef();
		LinkedList<ATokenNameIR> superNames = originalDef.getSuperNames();

		if (!superNames.isEmpty())
		{
			for (ATokenNameIR superName : superNames)
			{
				AClassHeaderDeclIR super1Header = provider.get(superName.getName());
				supers.addAll(getSupers(provider, super1Header));
				supers.add(super1Header);
			}
		}

		return supers;
	}
}
