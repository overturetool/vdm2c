/*
 * VdmMap.c
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */

#include "VdmMap.h"

#include <stdio.h> //FIXME remove all printf!

#define ASSERT_CHECK(s) assert(s->type == VDM_MAP && "Value is not a map")

struct TypedValue* newMap()
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	ptr->table = g_hash_table_new_full(vdm_typedvalue_hash, vdm_typedvalue_equal, vdm_g_free, vdm_g_free);

	return newTypeValue(VDM_MAP, (TypedValueType
			)
			{ .ptr = ptr });
}

void vdmMapAdd(TVP map, TVP key, TVP value)
{
	ASSERT_CHECK(map);

	UNWRAP_MAP(m,map);

	g_hash_table_insert(m->table, vdmClone(key), vdmClone(value));

}


// TODO: Apply does not work, if they key is not found
TVP vdmMapApply(TVP map, TVP key)
{
	ASSERT_CHECK(map);
	UNWRAP_MAP(m,map);

	return (TVP) g_hash_table_lookup(m->table, key);
}


void print_iterator(gpointer item, gpointer prefix) {
	TVP item2 = (TVP) item;
	//FIXME you cannot print a union type like this printf("%d \n", item2->value);
}
void print_iterator_short(gpointer item) {
 printf("%s\n", item);
}

int getGreater(int a, int b){
	if(a>b) return a;
	else return b;
}

TVP vdmMapDom(TVP map)
{
	//Assert map
	ASSERT_CHECK(map);

	// Get map size
	UNWRAP_MAP(m,map);
	int set_size = g_hash_table_size(m->table);

	GList* dom = g_hash_table_get_keys(m->table), *iterator = NULL;;

	TVP arr[set_size];

	int i;
	for (iterator = dom, i=0; iterator; iterator = iterator->next,i++)
		 arr[i]=(TVP) iterator->data;

	TVP res = newSetWithValues(set_size, arr);

	//TODO: Free necessary variables
	g_list_free(dom);

	return newSetWithValues(set_size, arr);
}

TVP vdmMapRng(TVP map)
{
	//Assert map
	ASSERT_CHECK(map);

	// Get map size
	UNWRAP_MAP(m,map);
	int set_size = g_hash_table_size(m->table);

	GList* dom = g_hash_table_get_values(m->table), *iterator = NULL;;

	TVP arr[set_size];

	int i;
	for (iterator = dom, i=0; iterator; iterator = iterator->next,i++)
		 arr[i]=(TVP) iterator->data;

	TVP res = newSetWithValues(set_size, arr);

	//TODO: Free necessary variables
	g_list_free(dom);

	return newSetWithValues(set_size, arr);
}

TVP vdmMapMunion(TVP map1, TVP map2)
{
	// Create a new map
	TVP map = newMap();

	//Assert map
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	// Assert that they are compatible, by using Set libary

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(d1,map1_dom);

	// Add key/val for map1
	for (int i=0; i<d1->size; i++){
		TVP key = d1->value[i];
		TVP val = vdmMapApply(map1,key);
		vdmMapAdd(map,key,val);
	}

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(d2,map2_dom);

	// Add key/val for map2
	for (int i=0; i<d2->size; i++){
		TVP key = d2->value[i];
		TVP val = vdmMapApply(map2,key);
		vdmMapAdd(map,key,val);
	}

	return map;
}

TVP vdmMapOverride(TVP map1, TVP map2)
{
	// Create a new map
	TVP map = newMap();

	//Assert map
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	// TODO: Assert that they are compatible, by using Set library

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(d1,map1_dom);

	// Add key/val for map1
	for (int i=0; i<d1->size; i++){
		TVP key = d1->value[i];
		TVP val = vdmMapApply(map1,key);
		vdmMapAdd(map,key,val);
	}

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(d2,map2_dom);

	// Add key/val for map2
	for (int i=0; i<d2->size; i++){
		TVP key = d2->value[i];
		TVP val = vdmMapApply(map2,key);
		vdmMapAdd(map,key,val);
	}

	return map;
}

TVP vdmMapMerge(TVP set)
{
// TODO unwrap set, creat a new map to return and set munion on it. Then return it

	TVP map = newMap();

	UNWRAP_COLLECTION(s,set);

	for(int i=0; i<s->size; i++)
		map = vdmMapMunion(map,s->value[i]);

	return map;
}

TVP vdmMapDomRestrictTo(TVP set,TVP map)
{
	// TODO: Check also for Set
	ASSERT_CHECK(map);

	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(int i=0; i<m->size;i++){
		TVP key = m->value[i];
		if(vdmSetMemberOf(set,key)->value.boolVal){
			TVP val = vdmMapApply(map,key);
			vdmMapAdd(map_res,key,val);
		}
	}

	return map_res;
}

TVP vdmMapDomRestrictBy(TVP set,TVP map)
{
	// TODO: Check also for Set
	ASSERT_CHECK(map);

	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(int i=0; i<m->size;i++){
		TVP key = m->value[i];
		if(!vdmSetMemberOf(set,key)->value.boolVal){ // TODO: Use vdmNotSetMember of when implemented
			TVP val = vdmMapApply(map,key);
			vdmMapAdd(map_res,key,val);
		}
	}

	return map_res;
}

TVP vdmMapRngRestrictTo(TVP set,TVP map)
{
	// TODO: Check also for Set
	ASSERT_CHECK(map);

	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(int i=0; i<m->size;i++){
		TVP key = m->value[i];
		TVP val = vdmMapApply(map,key);
		if(vdmSetMemberOf(set,val)->value.boolVal){ // TODO: Use vdmNotSetMember of when implemented
			vdmMapAdd(map_res,key,val);
		}
	}

	return map_res;
}


TVP vdmMapRngRestrictBy(TVP set,TVP map)
{
	// TODO: Check also for Set
	ASSERT_CHECK(map);

	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(int i=0; i<m->size;i++){
		TVP key = m->value[i];
		TVP val = vdmMapApply(map,key);
		if(!vdmSetMemberOf(set,val)->value.boolVal){ // TODO: Use vdmNotSetMember of when implemented
			vdmMapAdd(map_res,key,val);
		}
	}

	return map_res;
}

TVP vdmMapInverse(TVP map){

	ASSERT_CHECK(map);

	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(int i=0; i<m->size;i++){
		TVP key = m->value[i];
		TVP val = vdmMapApply(map,key);
		vdmMapAdd(map_res,val,key);
	}

	return map_res;
}

bool vdmMapEquals(TVP map1, TVP map2){

	//Assert map
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	bool eq = true;

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(m1,map1_dom);

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(m2,map2_dom);

	if(m1->size!=m2->size)
		return false;

	// Add key/val for map1
	for (int i=0; i<m1->size; i++){
		TVP key1 = m1->value[i];
		TVP val1 = vdmMapApply(map1,key1);

		TVP key2 = m2->value[i];
		TVP val2 = vdmMapApply(map2,key2);

		if(!equals(key1,key2) || !equals(val1,val2)){
			eq = false;
			break;
		}
	}

	return eq;

}

bool vdmMapInEquals(TVP map1, TVP map2){

	//Assert map
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	bool not_eq = true;

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(m1,map1_dom);

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(m2,map2_dom);

	if(m1->size!=m2->size)
		return true;

	for (int i=0; i<m1->size; i++){
		TVP key1 = m1->value[i];
		TVP val1 = vdmMapApply(map1,key1);

		TVP key2 = m2->value[i];
		TVP val2 = vdmMapApply(map2,key2);

		if(equals(key1,key2) && equals(val1,val2)){
			not_eq = false;
			break;
		}
	}

	return not_eq;

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
//		case VDM_OPTIONAL:
//		//TODO
//		break;
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
