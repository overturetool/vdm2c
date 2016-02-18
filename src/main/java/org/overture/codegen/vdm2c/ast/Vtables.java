package org.overture.codegen.vdm2c.ast;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newCast;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.AAnonymousStruct;
import org.overture.codegen.vdm2c.extast.declarations.AArrayDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class Vtables
{
	final public List<VEntry> table = new Vector<VEntry>();

	final public Map<String, List<VEntryOverride>> superVTableOverrides = new HashMap<String, List<VEntryOverride>>();

	Map<String, AClassHeaderDeclIR> supers = new HashMap<String, AClassHeaderDeclIR>();

	final AClassHeaderDeclIR header;

	public Vtables(AClassHeaderDeclIR header)
	{
		this.header = header;
	}

	public List<VEntry> getTable()
	{
		return table;
	}

	public static class VEntry
	{
		public VEntry(String key, AMethodDeclIR method)
		{
			this.key = key;
			this.method = method;
		}

		public final String key;
		public final AMethodDeclIR method;

		public String getKey()
		{
			return key;
		}

		public AMethodDeclIR getMethod()
		{
			return method;
		}
		
		@Override
		public String toString()
		{
			return key+" "+method;
		}

	}

	public static class VEntryOverride extends VEntry
	{
		public final AMethodDeclIR override;
		public AMethodDeclIR overrideProxy;

		public VEntryOverride(String key, AMethodDeclIR method,
				AMethodDeclIR override)
		{
			super(key, method);
			this.override = override;
		}

	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("-------------- VTable for " + header.getName()
				+ "-------------\n");

		sb.append(formatVTable(table, false));

		for (Entry<String, List<VEntryOverride>> ov : superVTableOverrides.entrySet())
		{
			sb.append("\t---------- VTable override for " + ov.getKey()
					+ "---------\n");
			sb.append(formatVTable(ov.getValue(), true));

		}

		return sb.toString();
	}

	public static String formatVTable(List<? extends VEntry> table,
			boolean indent)
	{
		StringBuilder sb = new StringBuilder();
		int length = 20;
		for (VEntry vEntry : table)
		{
			sb.append((indent ? "\t" : "")
					+ String.format("%1$" + length + "s", vEntry.key)
					+ ": "
					+ vEntry.method.getAncestor(ADefaultClassDeclIR.class).getName()
					+ " -> " + NameMangler.getName(vEntry.method.getName()));
			if (vEntry instanceof VEntryOverride)
			{
				VEntryOverride override = (VEntryOverride) vEntry;
				sb.append(" overriden by "
						+ override.override.getAncestor(ADefaultClassDeclIR.class).getName()
						+ " -> "
						+ NameMangler.getName(override.override.getName()));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public boolean hasMethod(VEntry method)
	{
		for (VEntry entry : table)
		{
			if (entry.key.equals(method.key))
			{
				return true;
			}
		}

		// TODO check all overrides
		return false;
	}

	public VEntry getEntry(VEntry method)
	{
		for (VEntry entry : table)
		{
			if (entry.key.equals(method.key))
			{
				return entry;
			}
		}

		// TODO check all overrides

		return null;
	}

	public void addSuperOverride(AClassHeaderDeclIR superDcl, VEntry original,
			VEntry override)
	{
		String name = superDcl.getName();
		supers.put(name, superDcl);
		List<VEntryOverride> overrides = superVTableOverrides.get(name);
		if (overrides == null)
		{
			overrides = new Vector<Vtables.VEntryOverride>();
			superVTableOverrides.put(name, overrides);
		}

		for (int i = 0; i < overrides.size(); i++)
		{
			if (overrides.get(i).key.equals(original.key))
			{
				overrides.set(i, new VEntryOverride(original.key, original.method, override.method));
				return;
			}
		}

		overrides.add(new VEntryOverride(original.key, original.method, override.method));

	}

	public boolean hasOverride(String superName)
	{
		return supers.containsKey(superName);
	}

	public Map<String, String> getOverrides(String sueprName)
	{
		Map<String, String> overrideMap = new HashMap<String, String>();

		for (Entry<String, List<VEntryOverride>> entry : superVTableOverrides.entrySet())
		{
			AClassHeaderDeclIR superHeader = supers.get(entry.getKey());

			// FIXME this should also be recursive
			for (VEntry aArrayDeclIR : superHeader.getVtable().table)
			{
				for (VEntryOverride m : entry.getValue())
				{
					if (m.method == aArrayDeclIR.method)
					{
						overrideMap.put(String.format("CLASS_%s_%s", sueprName, aArrayDeclIR.getMethod().getName()), m.overrideProxy.getName());
					}
				}

			}

		}

		return overrideMap;
	}

	public String getOverrideStaticTableName(String superName)
	{
		return "g_VTableArrayFor" + header.getName() + "_Override_" + superName;
	}

	public List<AArrayDeclIR> getOverrideVTableDeclarations()
	{
		List<AArrayDeclIR> fields = new Vector<AArrayDeclIR>();

		for (Entry<String, List<VEntryOverride>> entry : superVTableOverrides.entrySet())
		{
			String name = getOverrideStaticTableName(entry.getKey());
			AArrayDeclIR arrayDcl = new AArrayDeclIR();
			arrayDcl.setStatic(true);
			arrayDcl.setName(name);
			arrayDcl.setType(newExternalType("struct VTable"));
			AClassHeaderDeclIR superHeader = supers.get(entry.getKey());
			arrayDcl.setSize(superHeader.getVtable().table.size());

			// FIXME this should also be recursive
			for (VEntry aArrayDeclIR : superHeader.getVtable().table)
			{
				AMethodDeclIR selectedMethod = aArrayDeclIR.method;
				for (VEntryOverride m : entry.getValue())
				{
					if (m.method == aArrayDeclIR.method)
					{
						// is overriden
						selectedMethod = m.overrideProxy;
					}
				}

				AAnonymousStruct structEntry = new AAnonymousStruct();

				structEntry.getExp().add(createIdentifier("0", null));// sorry this should be int literal
				structEntry.getExp().add(createIdentifier("0", null));// sorry this should be int literal
				structEntry.getExp().add(newCast("VirtualFunctionPointer", createIdentifier(selectedMethod.getName(), null)));

				// arrayDcl.getInitial().add(structEntry);

			}

			fields.add(arrayDcl);
		}

		return fields;
	}

	public AArrayDeclIR getVTableDeclarations()
	{
		String name = "VTableArrayFor" + header.getName();
		AArrayDeclIR arrayDcl = new AArrayDeclIR();
		arrayDcl.setStatic(true);
		arrayDcl.setName(name);
		arrayDcl.setType(newExternalType("struct VTable"));
		arrayDcl.setSize(table.size());

		for (VEntry aArrayDeclIR : table)
		{
			AMethodDeclIR selectedMethod = aArrayDeclIR.method;

			AAnonymousStruct structEntry = new AAnonymousStruct();

			structEntry.getExp().add(createIdentifier("0", null));// sorry this should be int literal
			structEntry.getExp().add(createIdentifier("0", null));// sorry this should be int literal
			structEntry.getExp().add(newCast("VirtualFunctionPointer", createIdentifier(selectedMethod.getName(), null)));

			arrayDcl.getInitial().add(structEntry);

		}

		return arrayDcl;
	}

}
