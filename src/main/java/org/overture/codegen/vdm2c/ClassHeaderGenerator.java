package org.overture.codegen.vdm2c;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.codegen.ir.PCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.declarations.AFieldDeclCG;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;
import org.overture.codegen.ir.name.ATokenNameCG;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.types.AClassTypeCG;
import org.overture.codegen.ir.types.ARecordTypeCG;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.ast.CGenClonableString;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.vdm2c.extast.declarations.AClassStateDeclCG;

public class ClassHeaderGenerator
{
	public Collection<? extends IRStatus<PCG>> generateClassHeaders(
			List<IRStatus<ADefaultClassDeclCG>> extract)
			throws AnalysisException
	{
		final List<AClassHeaderDeclCG> classHeaders = new Vector<AClassHeaderDeclCG>();

		Collection<IRStatus<PCG>> list = new Vector<IRStatus<PCG>>();

		for (IRStatus<ADefaultClassDeclCG> irStatus : extract)
		{
			ADefaultClassDeclCG classDef = irStatus.getIrNode();

			AClassHeaderDeclCG header = new AClassHeaderDeclCG();

			header.setOriginalDef(classDef);

			AClassStateDeclCG state = new AClassStateDeclCG();
			for (AFieldDeclCG field : classDef.getFields())
			{
				if (field.getFinal())
				{
					header.getValues().add(field);
//					field.setName(NameConverter.getCName(field));
				} else
				{
					state.getFields().add(field);
				}
			}

			header.setState(state);

			for (AMethodDeclCG m : classDef.getMethods())
			{
				if (m.getTag() instanceof Vdm2cTag)
				{
					if (((Vdm2cTag) m.getTag()).methodTags.contains(MethodTag.Internal))
					{
						continue;
					}
				}
				AMethodDeclCG copy = m.clone();
				copy.setBody(null);
				for (AFormalParamLocalParamCG formal : copy.getFormalParams())
				{
					if (formal.getPattern() instanceof AIdentifierPatternCG)
					{
						AIdentifierPatternCG pattern = (AIdentifierPatternCG) formal.getPattern();
						if (pattern.getName().equals("this"))
						{
							pattern.setName("this_ptr");
						}
					}
				}
				header.getMethods().add(copy);
			}

			List<CGenClonableString> includes = new Vector<CGenClonableString>();

			for (String typeName : collectIncludeTypes(classDef).keySet())
			{
				includes.add(new CGenClonableString(typeName));
			}
			header.setIncludes(includes);

			header.setName(classDef.getName().toString());

			list.add(new IRStatus<PCG>(irStatus.getVdmNode(), header.getName(), header, new HashSet<VdmNodeInfo>()));
			classHeaders.add(header);
		}

		// not all headers are made. Now we need to sort out the inheritance

		for (AClassHeaderDeclCG header : classHeaders)
		{
			SClassDeclCG originalDef = header.getOriginalDef();
			LinkedList<ATokenNameCG> superNames = originalDef.getSuperNames();

			if (superNames.isEmpty())
			{
				continue;
			}

			header.setFlattenedSupers(getSupers(new HeaderProvider()
			{

				@Override
				public AClassHeaderDeclCG get(String name)
				{
					for (AClassHeaderDeclCG header : classHeaders)
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

	private Map<String, STypeCG> collectIncludeTypes(
			final ADefaultClassDeclCG classDef) throws AnalysisException
	{
		final Map<String, STypeCG> types = new HashMap<String, STypeCG>();

		classDef.apply(new DepthFirstAnalysisAdaptor()
		{
			void addType(String name, STypeCG type)
			{
				if (!types.containsKey(name))
				{
					types.put(name, type);
				}
			}

			@Override
			public void caseAClassTypeCG(AClassTypeCG node)
					throws AnalysisException
			{
				addType(node.getName(), node);
			}

			@Override
			public void caseARecordTypeCG(ARecordTypeCG node)
					throws AnalysisException
			{
				addType(node.getName().getName(), node);
			}

		});

		for (ATokenNameCG s : classDef.getSuperNames())
		{
			AClassTypeCG ct = new AClassTypeCG();
			ct.setName(s.getName());
			if (!types.containsKey(s.getName()))
			{
				types.put(s.getName(), ct);
			}
		}

		return types;
	}

	private interface HeaderProvider
	{
		public AClassHeaderDeclCG get(String name);
	}

	List<AClassHeaderDeclCG> getSupers(HeaderProvider provider,
			AClassHeaderDeclCG header)
	{
		List<AClassHeaderDeclCG> supers = new Vector<AClassHeaderDeclCG>();

		SClassDeclCG originalDef = header.getOriginalDef();
		LinkedList<ATokenNameCG> superNames = originalDef.getSuperNames();

		if (!superNames.isEmpty())
		{
			for (ATokenNameCG superName : superNames)
			{
				AClassHeaderDeclCG super1Header = provider.get(superName.getName());
				supers.addAll(getSupers(provider, super1Header));
				supers.add(super1Header);
			}
		}

		return supers;
	}
}
