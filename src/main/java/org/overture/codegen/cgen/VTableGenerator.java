package org.overture.codegen.cgen;

import java.util.List;

import org.overture.cgc.extast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgen.ast.Vtables;
import org.overture.codegen.cgen.ast.Vtables.VEntry;
import org.overture.codegen.ir.IRStatus;

public class VTableGenerator
{

	public static void generate(List<IRStatus<AClassHeaderDeclCG>> headers)
	{
		generateLevel1(headers);
		generateLevel2(headers);
	}

	public static void generateLevel1(List<IRStatus<AClassHeaderDeclCG>> headers)
	{
		for (IRStatus<AClassHeaderDeclCG> headerStatus : headers)
		{
			AClassHeaderDeclCG header = headerStatus.getIrNode();

			SClassDeclCG cDef = header.getOriginalDef();

			Vtables tables = new Vtables(header);

			for (AMethodDeclCG m : cDef.getMethods())
			{
				if(m.getIsConstructor())
					continue;
				tables.table.add(new VEntry(m.getName(), m));
			}
			
			header.setVtable(tables);
			
		}
	}
	
	public static void generateLevel2(List<IRStatus<AClassHeaderDeclCG>> headers)
	{
		for (IRStatus<AClassHeaderDeclCG> headerStatus : headers)
		{
			AClassHeaderDeclCG header = headerStatus.getIrNode();
			
			for (SDeclCG sh : header.getFlattenedSupers())
			{
				AClassHeaderDeclCG superHeader = (AClassHeaderDeclCG) sh;
				Vtables superTable = superHeader.getVtable();
				Vtables currentTable = header.getVtable();
				
				for (VEntry entry :currentTable.table )
				{
					if(superTable.hasMethod(entry))
					{
						currentTable.addSuperOverride(superHeader.getName(),superTable.getEntry(entry),entry);
					}
				}
				System.out.println(currentTable);
			}
			

		}
	}

}
