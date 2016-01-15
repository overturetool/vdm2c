package org.overture.codegen.cgen.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.cgc.extast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgen.utils.NameMangler;

public class Vtables
{
	final public List<VEntry> table = new Vector<VEntry>();

	final public Map<String, List<VEntryOverride>> superVTableOverrides = new HashMap<String, List<VEntryOverride>>();

	final AClassHeaderDeclCG header;

	public Vtables(AClassHeaderDeclCG header)
	{
		this.header = header;
	}
	
	public List<VEntry> getTable()
	{
		return table;
	}

	public static class VEntry
	{
		public VEntry(String key, AMethodDeclCG method)
		{
			this.key = key;
			this.method = method;
		}

		public final String key;
		public final AMethodDeclCG method;
		
		public String getKey()
		{
			return key;
		}
		public AMethodDeclCG getMethod()
		{
			return method;
		}
		
		

	}

	public static class VEntryOverride extends VEntry
	{
		public final AMethodDeclCG override;
		public AMethodDeclCG overrideProxy;

		public VEntryOverride(String key, AMethodDeclCG method,
				AMethodDeclCG override)
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
					+ vEntry.method.getAncestor(ADefaultClassDeclCG.class).getName()
					+ " -> " + NameMangler.getName(vEntry.method.getName()));
			if (vEntry instanceof VEntryOverride)
			{
				VEntryOverride override = (VEntryOverride) vEntry;
				sb.append(" overriden by "
						+ override.override.getAncestor(ADefaultClassDeclCG.class).getName()
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
				return true;
		}

		// TODO check all overrides
		return false;
	}

	public VEntry getEntry(VEntry method)
	{
		for (VEntry entry : table)
		{
			if (entry.key.equals(method.key))
				return entry;
		}

		// TODO check all overrides

		return null;
	}

	public void addSuperOverride(String name, VEntry original, VEntry override)
	{
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

}
