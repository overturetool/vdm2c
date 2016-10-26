package org.overture.codegen.vdm2c;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifierPattern;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newLocalDefinition;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newReturnStm;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.vdm2c.ast.Vtables;
import org.overture.codegen.vdm2c.ast.Vtables.VEntry;
import org.overture.codegen.vdm2c.ast.Vtables.VEntryOverride;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VTableGenerator
{
	final static Logger logger = LoggerFactory.getLogger(VTableGenerator.class);

	public static void generate(List<IRStatus<AClassHeaderDeclIR>> headers)
	{
		generateLevel1(headers);
		generateLevel2(headers);
		generateProxyOverrides(headers);
	}

	private static void generateProxyOverrides(
			List<IRStatus<AClassHeaderDeclIR>> headers)
	{
		for (IRStatus<AClassHeaderDeclIR> headerStatus : headers)
		{
			AClassHeaderDeclIR header = headerStatus.getIrNode();

			SClassDeclIR cDef = header.getOriginalDef();

			Vtables currentTable = header.getVtable();

			for (Entry<String, List<VEntryOverride>> entry : currentTable.superVTableOverrides.entrySet())
			{
				String superName = entry.getKey();
				for (VEntryOverride ov : entry.getValue())
				{
					AMethodDeclIR overrideProxy = ov.method.clone();
					overrideProxy.setName(superName + "_"
							+ overrideProxy.getName());

					ov.overrideProxy = overrideProxy;

					List<SExpIR> args = new Vector<SExpIR>();
					for (AFormalParamLocalParamIR format : overrideProxy.getFormalParams())
					{
						// TODO: not sure what to do if we have something thats not an identifier pattern
						args.add(createIdentifier(format.getPattern().toString(), format.getSourceNode()));
					}

					AFormalParamLocalParamIR thisArg = overrideProxy.getFormalParams().get(0);
					thisArg.setPattern(newIdentifierPattern("base"));

					ABlockStmIR body = new ABlockStmIR();
					body.setScoped(true);

					SExpIR downcast = newApply("CLASS_DOWNCAST", createIdentifier(superName, null), createIdentifier(cDef.getName(), null), createIdentifier("base", null));

					body.getStatements().add(newLocalDefinition(newDeclarationAssignment("this", newExternalType(cDef.getName()
							+ "CLASS"), downcast, null)));

					body.getStatements().add(newReturnStm(newApply(ov.override.getName(), args.toArray(new SExpIR[0]))));
					overrideProxy.setBody(body);

					cDef.getMethods().add(overrideProxy);
				}
			}
		}
	}

	public static void generateLevel1(List<IRStatus<AClassHeaderDeclIR>> headers)
	{
		for (IRStatus<AClassHeaderDeclIR> headerStatus : headers)
		{
			AClassHeaderDeclIR header = headerStatus.getIrNode();

			SClassDeclIR cDef = header.getOriginalDef();

			Vtables tables = new Vtables(header);

			for (AMethodDeclIR m : cDef.getMethods())
			{
				if (excludeFromVtable(m))
				{
					continue;
				}
				tables.table.add(new VEntry(m.getName(), m));
			}

			header.setVtable(tables);

		}
	}

	public static void generateLevel2(List<IRStatus<AClassHeaderDeclIR>> headers)
	{
		for (IRStatus<AClassHeaderDeclIR> headerStatus : headers)
		{
			AClassHeaderDeclIR header = headerStatus.getIrNode();
			Vtables currentTable = header.getVtable();

			for (SDeclIR sh : header.getFlattenedSupers())
			{
				AClassHeaderDeclIR superHeader = (AClassHeaderDeclIR) sh;
				Vtables superTable = superHeader.getVtable();

				for (VEntry entry : currentTable.table)
				{
					if (superTable.hasMethod(entry))
					{
						currentTable.addSuperOverride(superHeader, superTable.getEntry(entry), entry);
					}
				}
				logger.debug("VTable:\n {}", currentTable);
			}

		}
	}

	static boolean excludeFromVtable(AMethodDeclIR m)
	{
//		return m.getIsConstructor()
//				|| m.getTag() instanceof Vdm2cTag
//				&& ((Vdm2cTag) m.getTag()).methodTags.contains(Vdm2cTag.MethodTag.Internal);
		
		return m.getTag() instanceof Vdm2cTag
				&& ((Vdm2cTag) m.getTag()).methodTags.contains(Vdm2cTag.MethodTag.Internal);
	}

}
