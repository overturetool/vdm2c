package org.overture.codegen.cgen;

import static org.overture.codegen.cgen.transformations.CTransUtil.createIdentifier;
import static org.overture.codegen.cgen.transformations.CTransUtil.newApply;
import static org.overture.codegen.cgen.transformations.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.cgen.transformations.CTransUtil.newExternalType;
import static org.overture.codegen.cgen.transformations.CTransUtil.newIdentifierPattern;
import static org.overture.codegen.cgen.transformations.CTransUtil.newReturnStm;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.cgc.extast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgen.ast.Vtables;
import org.overture.codegen.cgen.ast.Vtables.VEntry;
import org.overture.codegen.cgen.ast.Vtables.VEntryOverride;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.SourceNode;

public class VTableGenerator
{

	public static void generate(List<IRStatus<AClassHeaderDeclCG>> headers)
	{
		generateLevel1(headers);
		generateLevel2(headers);
		generateProxyOverrides(headers);
	}

	private static void generateProxyOverrides(
			List<IRStatus<AClassHeaderDeclCG>> headers)
	{
		for (IRStatus<AClassHeaderDeclCG> headerStatus : headers)
		{
			AClassHeaderDeclCG header = headerStatus.getIrNode();

			SClassDeclCG cDef = header.getOriginalDef();

			Vtables currentTable = header.getVtable();

			for (Entry<String, List<VEntryOverride>> entry : currentTable.superVTableOverrides.entrySet())
			{
				String superName = entry.getKey();
				for (VEntryOverride ov : entry.getValue())
				{
					AMethodDeclCG overrideProxy = ov.method.clone();
					overrideProxy.setName(superName + "_"
							+ overrideProxy.getName());

					ov.overrideProxy = overrideProxy;

					List<SExpCG> args = new Vector<SExpCG>();
					for (AFormalParamLocalParamCG format : overrideProxy.getFormalParams())
					{
						// TODO: not sure what to do if we have something thats not an identifier pattern
						args.add(createIdentifier(format.getPattern().toString(), SourceNode.copy(format.getSourceNode())));
					}

					AFormalParamLocalParamCG thisArg = overrideProxy.getFormalParams().get(0);
					thisArg.setPattern(newIdentifierPattern("base"));

					ABlockStmCG body = new ABlockStmCG();
					body.setScoped(true);

					SExpCG downcast = newApply("CLASS_DOWNCAST", createIdentifier(superName, null), createIdentifier(cDef.getName(), null), createIdentifier("base", null));
					body.getLocalDefs().add(newDeclarationAssignment("this", newExternalType(cDef.getName()
							+ "CLASS"), downcast, null));

					body.getStatements().add(newReturnStm(newApply(ov.override.getName(), args.toArray(new SExpCG[0]))));
					overrideProxy.setBody(body);
					
					cDef.getMethods().add(overrideProxy);
				}
			}
		}
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
				if (m.getIsConstructor())
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

				for (VEntry entry : currentTable.table)
				{
					if (superTable.hasMethod(entry))
					{
						currentTable.addSuperOverride(superHeader, superTable.getEntry(entry), entry);
					}
				}
				System.out.println(currentTable);
			}

		}
	}

}
