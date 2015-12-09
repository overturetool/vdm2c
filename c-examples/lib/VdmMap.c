/*
 * VdmMap.c
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */

#include "VdmMap.h"

#define ASSERT_CHECK(s) assert(s->type == VDM_MAP && "Value is not a map")

struct TypedValue* newMap()
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	ptr->table = g_hash_table_new_full(vdm_typedvalue_hash, vdm_typedvalue_equal, vdm_g_free, vdm_g_free);

	return newTypeValue(VDM_MAP, (TypedValueType
			)
			{ .ptr = ptr });
}

void vdmMapAdd(TVP map,TVP key, TVP value)
{
	ASSERT_CHECK(map);

	UNWRAP_MAP(m,map);

	g_hash_table_insert(m->table, vdmClone(key), vdmClone(value));

}

TVP vdmMapApply(TVP map, TVP key)
{
	ASSERT_CHECK(map);
	UNWRAP_MAP(m,map);

	return (TVP) g_hash_table_lookup(m->table, key);
}

guint vdm_typedvalue_hash(gconstpointer v)
{
	TVP tv = (TVP)v;
	switch(tv->type)
	{
		case VDM_INT:
		case VDM_INT1:

		return g_int_hash(&tv->value.intVal);
		case VDM_BOOL:
		return g_int_hash(&tv->value.boolVal);
		case VDM_CHAR:
		return g_int_hash(&tv->value.charVal);
		case VDM_REAL:
		return g_double_hash(&tv->value.doubleVal);
		case VDM_QUOTE:
		return g_int_hash(&tv->value.uintVal);
		case VDM_MAP:
		//todo
		break;
		case VDM_PRODUCT:
		case VDM_SEQ:
		case VDM_SET:
		{
//			UNWRAP_COLLECTION(cptr,tmp);
//
//			struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));
//
//			//copy (size)
//			*ptr = *cptr;
//			ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue) * ptr->size);
//
//			for (int i = 0; i < cptr->size; i++)
//			{
//				ptr->value[i] = vdmClone(cptr->value[i]);
//			}
//
//			tmp->value.ptr = ptr;
			break;
		}
		case VDM_OPTIONAL:
		//TODO
		break;
		case VDM_RECORD:
		{
			//TODO duplicate (memcpy) and duplicate what ever any pointers points to except if a class
			break;
		}
		case VDM_CLASS:
		{
			break;
		}
	}

	return 0; //really bad hash
}

gboolean vdm_typedvalue_equal(gconstpointer v1, gconstpointer v2)
{
	TVP a = (TVP)v1;
	TVP b = (TVP)v2;

	return equals(a,b);

};

void vdm_g_free(gpointer mem)
{
	TVP a = (TVP)mem;
	recursiveFree(a);
}
