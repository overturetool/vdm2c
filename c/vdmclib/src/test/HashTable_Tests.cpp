/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

#ifdef WITH_GLIB_HASH

#include "gtest/gtest.h"

#include <stdio.h>

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include <glib.h>
}

//static GHashTable *table;

//static void add_data(void)
//{
//	gchar *key;
//	gchar *value;
//
//	key = g_new(gchar, 1);
//	value = g_new(gchar, 1);
//
//	printf("Input the key ");
//	scanf("%s", key);
//	printf("Input the value ");
//	scanf("%s", value);
//
//	g_hash_table_insert(table, g_strdup(key), g_strdup(value));
//}

static void print_data(int *key, int *value)
{
	printf("%d:%d\n", *key, *value);
}

TEST(HashTable, intHash)
{
	GHashTable *table = g_hash_table_new_full(g_int_hash, g_int_equal, g_free, g_free);

	int*value;
	value = g_new(int, 1);

	int* key = g_new(int, 1);
	*key = 7;
	*value = 8;

	g_hash_table_insert(table, key, value);

	value = g_new(int, 1);
	key = g_new(int, 1);
	*key = 6;
	*value = 18;
	g_hash_table_insert(table, key, value);

	int* lookup = g_new(int, 1);
	*lookup = 6;
	printf("Trying to lookup %d\n", *lookup);
	printf("Found %d\n", *(int*) g_hash_table_lookup(table, lookup));
//	int i = 10;
//	while (i--)
//	{
//		add_data();
//
//	}
	g_hash_table_foreach(table, (GHFunc) print_data, NULL);

}

//guint vdm_typedvalue_hash(gconstpointer v)
//{
//	TVP tv = (TVP)v;
//	switch(tv->type)
//	{
//	default:
//		return g_int_hash(v);
//	}
//	return 0; //really bad hash
//}
//
//gboolean vdm_typedvalue_equal(gconstpointer v1, gconstpointer v2)
//{
//	TVP a = (TVP)v1;
//	TVP b = (TVP)v2;
//
//	return equals(a,b);
//
//};
//
//void vdm_g_free(gpointer mem)
//{
//	TVP a = (TVP)mem;
//	vdmFree(a);
//}

TEST(HashTable, typedHash)
{
	GHashTable *table = g_hash_table_new_full(vdm_typedvalue_hash, vdm_typedvalue_equal, vdm_g_free, vdm_g_free);

	TVP key1 = newInt(1);
	TVP val1 = newBool(true);

	g_hash_table_insert(table, key1, val1);

//	value = g_new(int, 1);
//	key = g_new(int, 1);
//	*key = 6;
//	*value = 18;
//	g_hash_table_insert(table, key, value);
//
//	int* lookup = g_new(int, 1);
//	*lookup = 6;
	TVP lookup = newInt(1);
	printf("Trying to lookup %d\n", lookup->value.intVal);
	printf("Found %d\n", ((TVP) g_hash_table_lookup(table, lookup))->value.intVal);
//	int i = 10;
//	while (i--)
//	{
//		add_data();
//
//	}
	g_hash_table_foreach(table, (GHFunc) print_data, NULL);

}
#endif
