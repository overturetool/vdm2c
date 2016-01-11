package org.overture.codegen.cgen;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.cgc.extast.declarations.AClassHeaderDeclCG;
import org.overture.cgc.extast.declarations.AClassStateDeclCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.VdmNodeInfo;

public class ClassHeaderGenerator
{
	@SuppressWarnings("unchecked")
	public Collection<? extends IRStatus<INode>> generateClassHeaders(
			List<IRStatus<ADefaultClassDeclCG>> extract)
	{
		final List<AClassHeaderDeclCG> classHeaders = new Vector<AClassHeaderDeclCG>();

		Collection<IRStatus<INode>> list = new Vector<IRStatus<INode>>();

		for (IRStatus<ADefaultClassDeclCG> irStatus : extract)
		{
			ADefaultClassDeclCG classDef = irStatus.getIrNode();

			AClassHeaderDeclCG def = new AClassHeaderDeclCG();

			def.setOriginalDef(classDef);

			for (AFieldDeclCG fi : classDef.getFields())
			{
				fi.getAccess();
				fi.getInitial();
			}
			
			AClassStateDeclCG state = new AClassStateDeclCG();
			state.setFields((List<? extends AFieldDeclCG>) classDef.getFields().clone());
			
			def.setState(state);

			def.setMethods((List<? extends AMethodDeclCG>) classDef.getMethods().clone());
			for (AMethodDeclCG m : def.getMethods())
			{
				m.setBody(null);
				// m.getSourceNode().getVdmNode();
			}

			def.setName(classDef.getName().toString());

			list.add(new IRStatus<INode>(def.getName(), def, new HashSet<VdmNodeInfo>()));
			classHeaders.add(def);
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
						if(header.getName().equals(name))
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
