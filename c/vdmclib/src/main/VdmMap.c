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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * VdmMap.c
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */

#include "VdmMap.h"

#include <stdio.h> /* FIXME remove all printf!  */
#include <stdarg.h>

#define ASSERT_CHECK(s) assert(s->type == VDM_MAP && "Value is not a map")

#ifndef NO_MAPS

#ifdef WITH_GLIB_HASH



guint vdm_typedvalue_hash(gconstpointer v)
{
	TVP tv = (TVP)v;
	switch(tv->type)
	{
	case VDM_INT:
	case VDM_NAT1:
	case VDM_NAT:
		return g_int_hash(&tv->value.intVal);
	case VDM_BOOL:
		return g_int_hash(&tv->value.boolVal);
	case VDM_CHAR:
		return g_int_hash(&tv->value.charVal);
	case VDM_REAL:
	case VDM_RAT:
		return g_double_hash(&tv->value.doubleVal);
	case VDM_QUOTE:
		return g_int_hash(&tv->value.quoteVal);
	case VDM_MAP:
		/* todo  */
		break;
	case VDM_PRODUCT:
	case VDM_SEQ:
	case VDM_SET:
	{
		/* 			UNWRAP_COLLECTION(cptr,tmp);  */
		/*   */
		/* 			struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));  */
		/*   */
		/* 			 copy (size)  */
		/* 			*ptr = *cptr;  */
		/* 			ptr->value = (TVP*) malloc(sizeof(struct TypedValue) * ptr->size);  */
		/*   */
		/* 			for (int i = 0; i < cptr->size; i++)  */
		/* 			{  */
		/* 				ptr->value[i] = vdmClone(cptr->value[i]);  */
		/* 			}  */
		/*   */
		/* 			tmp->value.ptr = ptr;  */
		break;
	}
	/* 		case VDM_OPTIONAL:  */
	/* 		TODO  */
	/* 		break;  */
	case VDM_RECORD:
	{
		/* TODO duplicate (memcpy) and duplicate what ever any pointers points to except if a class  */
		break;
	}
	case VDM_CLASS:
	{
		break;
	}
	}

	return 0; /* really bad hash  */
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
	vdmFree(a);
}



TVP newMap()
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	ptr->table = g_hash_table_new_full(vdm_typedvalue_hash, vdm_typedvalue_equal, vdm_g_free, vdm_g_free);

	return newTypeValue(VDM_MAP, (TypedValueType
	)
			{ .ptr = ptr });
}

TVP newMapGC(TVP *from)
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	ptr->table = g_hash_table_new_full(vdm_typedvalue_hash, vdm_typedvalue_equal, vdm_g_free, vdm_g_free);

	return newTypeValueGC(VDM_MAP, (TypedValueType
	)
			{ .ptr = ptr }, from);
}

void vdmMapAdd(TVP map, TVP key, TVP value)
{
	ASSERT_CHECK(map);

	UNWRAP_MAP(m,map);

	g_hash_table_insert(m->table, vdmClone(key), vdmClone(value));

}


/*  TODO: Apply does not work, if they key is not found  */
TVP vdmMapApply(TVP map, TVP key)
{
	ASSERT_CHECK(map);
	UNWRAP_MAP(m,map);

	return (TVP) g_hash_table_lookup(m->table, key);
}


void print_iterator(gpointer item, gpointer prefix) {
	TVP item2 = (TVP) item;
	/* FIXME you cannot print a union type like this printf("%d \n", item2->value);  */
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
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);
	int set_size = g_hash_table_size(m->table);

	GList* dom = g_hash_table_get_keys(m->table), *iterator = NULL;;

	TVP arr[set_size];

	int i;
	for (iterator = dom, i=0; iterator; iterator = iterator->next,i++)
		arr[i]=(TVP) iterator->data;

	TVP res = newSetWithValues(set_size, arr);

	/* TODO: Free necessary variables  */
	g_list_free(dom);

	return res
}

TVP vdmMapDomGC(TVP map, TVP *from)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);
	int set_size = g_hash_table_size(m->table);

	GList* dom = g_hash_table_get_keys(m->table), *iterator = NULL;;

	TVP arr[set_size];

	int i;
	for (iterator = dom, i=0; iterator; iterator = iterator->next,i++)
		arr[i]=(TVP) iterator->data;

	TVP res = newSetWithValuesGC(set_size, arr, from);

	/* TODO: Free necessary variables  */
	g_list_free(dom);

	return res
}

TVP vdmMapRng(TVP map)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);
	int set_size = g_hash_table_size(m->table);

	GList* dom = g_hash_table_get_values(m->table), *iterator = NULL;;

	TVP arr[set_size];

	int i;
	for (iterator = dom, i=0; iterator; iterator = iterator->next,i++)
		arr[i]=(TVP) iterator->data;

	TVP res = newSetWithValues(set_size, arr);

	/* TODO: Free necessary variables  */
	g_list_free(dom);

	return newSetWithValues(set_size, arr);
}

TVP vdmMapRngGC(TVP map, TVP *from)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);
	int set_size = g_hash_table_size(m->table);

	GList* dom = g_hash_table_get_values(m->table), *iterator = NULL;;

	TVP arr[set_size];

	int i;
	for (iterator = dom, i=0; iterator; iterator = iterator->next,i++)
		arr[i]=(TVP) iterator->data;

	TVP res = newSetWithValuesGC(set_size, arr, from);

	/* TODO: Free necessary variables  */
	g_list_free(dom);

	return res;
}

#else


hashtable_t *ht_create( int size ) {

	hashtable_t *hashtable = NULL;
	int i;

	if( size < 1 ) return NULL;

	/* Allocate the table itself. */
	if( ( hashtable = malloc( sizeof( hashtable_t ) ) ) == NULL ) {
		return NULL;
	}

	/* Allocate pointers to the head nodes. */
	if( ( hashtable->chain = malloc( sizeof( entry_t * ) * size ) ) == NULL ) {
		return NULL;
	}
	for( i = 0; i < size; i++ ) {
		hashtable->chain[i] = NULL;
	}

	hashtable->size = size;

	return hashtable;
}



/* Hash a string for a particular hash table. */
int ht_hash( hashtable_t *hashtable, TVP key ) {


	/*
	 * Create hash
	while( hashval < ULONG_MAX && i < strlen( key ) ) {
		hashval = hashval << 8;
		hashval += key[ i ];
		i++;
	}
	return hashval % hashtable->size;
	 */

	/* TODO:  Figure out hash function for TVP  */
	return 0;
}



/* Create a key-value pair. */
entry_t *ht_newpair( TVP key, TVP value ) {
	entry_t *newpair;

	if( ( newpair = malloc( sizeof( entry_t ) ) ) == NULL ) {
		return NULL;
	}

	if( ( newpair->key = vdmClone(key) ) == NULL ) {
		return NULL;
	}

	if( ( newpair->value = vdmClone(value) ) == NULL ) {
		return NULL;
	}

	newpair->next = NULL;

	return newpair;
}



/* Insert a key-value pair into a hash table. */
void ht_set( hashtable_t *hashtable, TVP key, TVP value ) {
	int bin = 0;
	entry_t *newpair = NULL;
	entry_t *next = NULL;
	entry_t *last = NULL;
	TVP compres;

	bin = ht_hash( hashtable, key );

	next = hashtable->chain[ bin ];

	if(next != NULL && next->key != NULL)
	{
		compres = vdmEquals(key, next->key);
	}
	while( next != NULL && next->key != NULL && !compres->value.boolVal ) {
		last = next;
		next = next->next;

		if(next != NULL && next->key != NULL)
		{
			vdmFree(compres);
			compres = vdmEquals(key, next->key);
		}
	}

	/* There's already a pair.  Let's replace that string. */

	if( next != NULL && next->key != NULL)
	{
		compres = vdmEquals(key, next->key);

		if(compres->value.boolVal) {

			vdmFree( next->value );
			next->value = vdmClone(value);

			/* Nope, could't find it.  Time to grow a pair. */
		}

		vdmFree(compres);
	} else {
		newpair = ht_newpair( key, value );

		/* We're at the start of the linked list in this bin. */
		if( next == hashtable->chain[ bin ] ) {
			newpair->next = next;
			hashtable->chain[ bin ] = newpair;

			/* We're at the end of the linked list in this bin. */
		} else if ( next == NULL ) {
			last->next = newpair;

			/* We're in the middle of the list. */
		} else  {
			newpair->next = next;
			last->next = newpair;
		}
	}
}


TVP ht_get( hashtable_t *hashtable, TVP key ) {
	int bin = 0;
	entry_t *pair;
	TVP compres;

	bin = ht_hash( hashtable, key );

	/* Step through the bin, looking for our value. */
	pair = hashtable->chain[ bin ];

	if(pair != NULL && pair->key != NULL)
	{
		compres = vdmEquals(key, pair->key);
	}
	while( pair != NULL && pair->key != NULL && !compres->value.boolVal )
	{
		pair = pair->next;
		if(pair != NULL && pair->key != NULL)
		{
			vdmFree(compres);
			compres = vdmEquals(key, pair->key);
		}
	}

	/* Did we actually find anything? */
	if( pair == NULL || pair->key == NULL || !compres->value.boolVal ) {
		vdmFree(compres);
		return NULL;

	} else {
		vdmFree(compres);
		return pair->value;
	}

}



TVP newMap()
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	/* TODO:  work out initial size.  */
	ptr->table =  ht_create(10);

	return newTypeValue(VDM_MAP, (TypedValueType
	)
			{ .ptr = ptr });
}

void freeChain(entry_t *chain)
{
	if(chain == NULL)
		return;

	if(chain->next != NULL)
		freeChain(chain->next);

	vdmFree(chain->key);
	vdmFree(chain->value);
	free(chain);
}

void freeMap(struct Map *m)
{
	int i;
	for(i = 0; i < m->table->size; i++)
	{
		freeChain(m->table->chain[i]);
	}

	free(m->table->chain);
	free(m->table);
	free(m);
}

entry_t* cloneChain(entry_t *chain)
{
	entry_t *entry;

	if(chain == NULL)
		return NULL;

	entry = (entry_t *)malloc(sizeof(entry_t));
	entry->key = vdmClone(chain->key);
	entry->value = vdmClone(chain->value);
	entry->next = cloneChain(chain->next);

	return entry;
}

struct Map* cloneMap(struct Map *m)
{
	int i;

	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));

	ptr->table = ht_create(m->table->size);
	for(i = 0; i < ptr->table->size; i++)
	{
		ptr->table->chain[i] = cloneChain(m->table->chain[i]);
	}

	return ptr;
}


TVP newMapGC(TVP *from)
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	/* TODO:  work out initial size.  */
	ptr->table =  ht_create(10);

	return newTypeValueGC(VDM_MAP, (TypedValueType
	)
			{ .ptr = ptr }, from);
}


/* Not a very useful function, but here to support the map comprehension mechanism.  */
TVP newMapVarToGrow(size_t size, size_t expected_size, ...)
{
	struct Map* ptr;
	TVP key;
	TVP value;
	TVP theMap;
	int i;

	if(size == 0)
	{
		return newMap();
	}

	ptr = (struct Map*) malloc(sizeof(struct Map));
	ptr->table =  ht_create(expected_size);
	theMap = newTypeValue(VDM_MAP, (TypedValueType){ .ptr = ptr });

	va_list argList;
	va_start(argList, expected_size);

	for(i = 0; i < size; i++)
	{
		key = vdmClone(va_arg(argList, TVP));
		value = vdmClone(va_arg(argList, TVP));

		vdmMapAdd(theMap, key, value);

		vdmFree(key);
		vdmFree(value);
	}
	va_end(argList);

	return theMap;
}

/* Not a very useful function, but here to support the map comprehension mechanism.  */
TVP newMapVarToGrowGC(size_t size, size_t expected_size, TVP *from, ...)
{
	struct Map* ptr;
	TVP key;
	TVP value;
	TVP theMap;
	int i;

	if(size == 0)
	{
		return newMapGC(from);
	}

	ptr = (struct Map*) malloc(sizeof(struct Map));
	ptr->table =  ht_create(expected_size);
	theMap = newTypeValueGC(VDM_MAP, (TypedValueType){ .ptr = ptr }, from);

	va_list argList;
	va_start(argList, from);

	for(i = 0; i < size; i++)
	{
		key = vdmClone(va_arg(argList, TVP));
		value = vdmClone(va_arg(argList, TVP));

		vdmMapAdd(theMap, key, value);

		vdmFree(key);
		vdmFree(value);
	}
	va_end(argList);

	return theMap;
}

/* Not a very useful operation, but here to support the map comprehension mechanism.  */
void vdmMapGrow(TVP theMap, TVP key, TVP val)
{
	vdmMapAdd(theMap, key, val);
}

/* Not a very useful operation, but here to support the map comprehension mechanism.  */
void vdmMapFit(TVP theMap)
{
	return;
}


void vdmMapAdd(TVP map, TVP key, TVP value)
{
	ASSERT_CHECK(map);

	UNWRAP_MAP(m,map);

	ht_set(m->table, key, value);
}


void vdmMapUpdate(TVP map, TVP key, TVP value)
{
	vdmMapAdd(map, key, value);
}



/*  TODO: Apply does not work if the key is not found  */
TVP vdmMapApply(TVP map, TVP key)
{
	ASSERT_CHECK(map);
	UNWRAP_MAP(m,map);

	return vdmClone(ht_get(m->table, key));
}


/*  TODO: Apply does not work if the key is not found  */
TVP vdmMapApplyGC(TVP map, TVP key, TVP *from)
{
	ASSERT_CHECK(map);
	UNWRAP_MAP(m,map);

	return vdmCloneGC(ht_get(m->table, key), from);
}



TVP vdmMapDom(TVP map)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);

	int i;
	int mapsize = 0;
	entry_t *currentry;

	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP arr[mapsize];

	/* Reusing this variable.  */
	mapsize = 0;
	/* Get keys.  */
	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			arr[mapsize] = currentry->key;
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP res = newSetWithValues(mapsize, arr);
	return res;
}


TVP vdmMapDomGC(TVP map, TVP *from)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);

	int i;
	int mapsize = 0;
	entry_t *currentry;

	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP arr[mapsize];

	/* Reusing this variable.  */
	mapsize = 0;
	/* Get keys.  */
	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			arr[mapsize] = currentry->key;
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP res = newSetWithValuesGC(mapsize, arr, from);
	return res;
}



TVP vdmMapRng(TVP map)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);

	int i;
	int mapsize = 0;
	entry_t *currentry;

	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP arr[mapsize];

	/* Reusing this variable.  */
	mapsize = 0;
	/* Get keys.  */
	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			arr[mapsize] = currentry->value;
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP res = newSetWithValues(mapsize, arr);
	return res;
}


TVP vdmMapRngGC(TVP map, TVP *from)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);

	int i;
	int mapsize = 0;
	entry_t *currentry;

	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP arr[mapsize];

	/* Reusing this variable.  */
	mapsize = 0;
	/* Get keys.  */
	for(i = 0; i < m->table->size; i++)
	{
		currentry = (m->table->chain)[i];

		while(currentry != NULL)
		{
			arr[mapsize] = currentry->value;
			mapsize += 1;
			currentry = currentry->next;
		}
	}

	TVP res = newSetWithValuesGC(mapsize, arr, from);
	return res;
}

#endif



TVP vdmMapMunion(TVP map1, TVP map2)
{
	/*  Create a new map  */
	TVP map = newMap();
	TVP dom1set;
	TVP dom2set;
	TVP dominter;
	TVP map1res;
	TVP map2res;
	TVP map1resrng;
	TVP map2resrng;
	TVP res;
	TVP key;
	TVP val;
	int i;

	/* Assert map  */
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	/* Ensure that maps are compatible.  */
	dom1set = vdmMapDom(map1);
	dom2set = vdmMapDom(map2);
	dominter = vdmSetInter(dom1set, dom2set);
	vdmFree(dom1set);
	vdmFree(dom2set);

	map1res = vdmMapDomRestrictTo(dominter, map1);
	map2res = vdmMapDomRestrictTo(dominter, map2);
	vdmFree(dominter);
	map1resrng = vdmMapRng(map1res);
	map2resrng = vdmMapRng(map2res);
	vdmFree(map1res);
	vdmFree(map2res);
	res = vdmEquals(map1resrng, map2resrng);
	vdmFree(map1resrng);
	vdmFree(map2resrng);
	assert(res->value.boolVal && "Maps not compatible.");
	vdmFree(res);

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(d1,map1_dom);

	/*  Add key/val for map1  */
	for (i=0; i<d1->size; i++)
	{
		key = d1->value[i];
		val = vdmMapApply(map1,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(d2,map2_dom);

	/*  Add key/val for map2  */
	for (i=0; i<d2->size; i++)
	{
		key = d2->value[i];
		val = vdmMapApply(map2,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	vdmFree(map1_dom);
	vdmFree(map2_dom);
	return map;
}


TVP vdmMapMunionGC(TVP map1, TVP map2, TVP *from)
{
	/*  Create a new map  */
	TVP map = newMapGC(from);
	TVP dom1set;
	TVP dom2set;
	TVP dominter;
	TVP map1res;
	TVP map2res;
	TVP map1resrng;
	TVP map2resrng;
	TVP res;
	TVP key;
	TVP val;
	int i;

	/* Assert map  */
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	/* Ensure that maps are compatible.  */
	dom1set = vdmMapDom(map1);
	dom2set = vdmMapDom(map2);
	dominter = vdmSetInter(dom1set, dom2set);
	vdmFree(dom1set);
	vdmFree(dom2set);

	map1res = vdmMapDomRestrictTo(dominter, map1);
	map2res = vdmMapDomRestrictTo(dominter, map2);
	vdmFree(dominter);
	map1resrng = vdmMapRng(map1res);
	map2resrng = vdmMapRng(map2res);
	vdmFree(map1res);
	vdmFree(map2res);
	res = vdmEquals(map1resrng, map2resrng);
	vdmFree(map1resrng);
	vdmFree(map2resrng);
	assert(res->value.boolVal && "Maps not compatible.");
	vdmFree(res);

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(d1,map1_dom);

	/*  Add key/val for map1  */
	for (i=0; i<d1->size; i++)
	{
		key = d1->value[i];
		val = vdmMapApply(map1,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(d2,map2_dom);

	/*  Add key/val for map2  */
	for (i=0; i<d2->size; i++)
	{
		key = d2->value[i];
		val = vdmMapApply(map2,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	vdmFree(map1_dom);
	vdmFree(map2_dom);
	return map;
}


TVP vdmMapOverride(TVP map1, TVP map2)
{
	/*  Create a new map  */
	TVP map = newMap();
	TVP key;
	TVP val;
	int i;

	/* Assert map  */
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(d1,map1_dom);

	/*  Add key/val for map1  */
	for (i=0; i<d1->size; i++){
		key = d1->value[i];
		val = vdmMapApply(map1,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(d2,map2_dom);

	/*  Add key/val for map2  */
	for (i=0; i<d2->size; i++){
		key = d2->value[i];
		val = vdmMapApply(map2,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	vdmFree(map1_dom);
	vdmFree(map2_dom);

	return map;
}


TVP vdmMapOverrideGC(TVP map1, TVP map2, TVP *from)
{
	/*  Create a new map  */
	TVP map = newMapGC(from);
	TVP key;
	TVP val;
	int i;

	/* Assert map  */
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	TVP map1_dom = vdmMapDom(map1);
	UNWRAP_COLLECTION(d1,map1_dom);

	/*  Add key/val for map1  */
	for (i=0; i<d1->size; i++){
		key = d1->value[i];
		val = vdmMapApply(map1,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	TVP map2_dom = vdmMapDom(map2);
	UNWRAP_COLLECTION(d2,map2_dom);

	/*  Add key/val for map2  */
	for (i=0; i<d2->size; i++){
		key = d2->value[i];
		val = vdmMapApply(map2,key);
		vdmMapAdd(map,key,val);

		vdmFree(val);
	}

	vdmFree(map1_dom);
	vdmFree(map2_dom);

	return map;
}


TVP vdmMapMerge(TVP set)
{
	TVP map = newMap();
	int i;

	UNWRAP_COLLECTION(s,set);

	for(i=0; i<s->size; i++)
		map = vdmMapMunion(map,s->value[i]);

	return map;
}


TVP vdmMapMergeGC(TVP set, TVP *from)
{
	TVP map = newMapGC(from);
	int i;

	UNWRAP_COLLECTION(s,set);

	for(i=0; i<s->size; i++)
		map = vdmMapMunionGC(map,s->value[i], from);

	return map;
}

TVP vdmMapDomRestrictTo(TVP set,TVP map)
{
	ASSERT_CHECK(map);

	TVP key;
	TVP val;

	TVP map_res = newMap();
	TVP res;

	int i;

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++)
	{
		key = m->value[i];
		res = vdmSetMemberOf(set, key);
		if(res->value.boolVal)
		{
			val = vdmMapApply(map,key);
			vdmMapAdd(map_res,key,val);

			vdmFree(val);
		}
		vdmFree(res);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapDomRestrictToGC(TVP set,TVP map, TVP *from)
{
	ASSERT_CHECK(map);

	TVP key;
	TVP val;

	TVP map_res = newMapGC(from);
	TVP res;

	int i;

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++)
	{
		key = m->value[i];
		res = vdmSetMemberOf(set, key);
		if(res->value.boolVal)
		{
			val = vdmMapApply(map,key);
			vdmMapAdd(map_res,key,val);

			vdmFree(val);
		}
		vdmFree(res);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapDomRestrictBy(TVP set,TVP map)
{
	ASSERT_CHECK(map);

	TVP map_res = newMap();
	TVP key;
	TVP val;
	int i;

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++)
	{
		key = m->value[i];

		if(vdmSetNotMemberOf(set,key)->value.boolVal){
			val = vdmMapApply(map,key);
			vdmMapAdd(map_res,key,val);

			vdmFree(val);
		}
	}

	vdmFree(map_dom);
	return map_res;
}



TVP vdmMapDomRestrictByGC(TVP set,TVP map, TVP *from)
{
	ASSERT_CHECK(map);

	TVP map_res = newMapGC(from);
	TVP key;
	TVP val;
	int i;

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++)
	{
		key = m->value[i];
		if(vdmSetNotMemberOf(set,key)->value.boolVal){
			val = vdmMapApply(map,key);
			vdmMapAdd(map_res,key,val);

			vdmFree(val);
		}
	}

	vdmFree(map_dom);
	return map_res;
}



TVP vdmMapRngRestrictTo(TVP map, TVP set)
{
	ASSERT_CHECK(map);

	TVP key;
	TVP val;
	TVP res;
	int i;
	TVP map_res = newMap();
	TVP map_dom = vdmMapDom(map);

	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++){
		key = m->value[i];
		val = vdmMapApply(map,key);
		res = vdmSetMemberOf(set,val);

		if(res->value.boolVal)
		{
			vdmMapAdd(map_res,key,val);
		}
		vdmFree(res);
		vdmFree(val);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapRngRestrictToGC(TVP map, TVP set, TVP *from)
{
	ASSERT_CHECK(map);

	TVP key;
	TVP val;
	TVP res;
	int i;
	TVP map_res = newMapGC(from);

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++){
		key = m->value[i];
		val = vdmMapApply(map,key);
		res = vdmSetMemberOf(set,val);

		if(res->value.boolVal)
		{
			vdmMapAdd(map_res,key,val);
		}
		vdmFree(res);
		vdmFree(val);
	}

	vdmFree(map_dom);
	return map_res;
}



TVP vdmMapRngRestrictBy(TVP map, TVP set)
{
	ASSERT_CHECK(map);

	TVP key;
	TVP val;
	TVP res;
	int i;
	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++){
		key = m->value[i];
		val = vdmMapApply(map,key);
		res = vdmSetNotMemberOf(set,val);

		if(res->value.boolVal)
		{
			vdmMapAdd(map_res,key,val);
		}
		vdmFree(res);
		vdmFree(val);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapRngRestrictByGC(TVP map, TVP set, TVP *from)
{
	ASSERT_CHECK(map);

	TVP key;
	TVP val;
	TVP res;
	int i;
	TVP map_res = newMapGC(from);

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++){
		key = m->value[i];
		val = vdmMapApply(map,key);
		res = vdmSetNotMemberOf(set,val);

		if(res->value.boolVal)
		{
			vdmMapAdd(map_res,key,val);
		}
		vdmFree(res);
		vdmFree(val);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapInverse(TVP map){

	ASSERT_CHECK(map);

	TVP key;
	TVP val;
	int i;
	TVP map_res = newMap();

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++){
		key = m->value[i];
		val = vdmMapApply(map,key);
		vdmMapAdd(map_res,val,key);

		vdmFree(val);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapInverseGC(TVP map, TVP *from)
{

	ASSERT_CHECK(map);

	TVP key;
	TVP val;
	int i;
	TVP map_res = newMapGC(from);

	TVP map_dom = vdmMapDom(map);
	UNWRAP_COLLECTION(m,map_dom);

	for(i=0; i<m->size;i++){
		key = m->value[i];
		val = vdmMapApply(map,key);
		vdmMapAdd(map_res,val,key);

		vdmFree(val);
	}

	vdmFree(map_dom);
	return map_res;
}


TVP vdmMapEquals(TVP map1, TVP map2){

	/* Assert map  */
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	bool eq = true;

	TVP key1;
	TVP val1;
	TVP key2;
	TVP val2;
	TVP map1_dom;
	TVP map2_dom;

	TVP map1_rng;
	TVP map2_rng;

	int i;

	map1_dom = vdmMapDom(map1);
	map2_dom = vdmMapDom(map2);
	if(!equals(map1_dom, map2_dom))
	{
		eq = false;

		vdmFree(map1_dom);
		vdmFree(map2_dom);

		return newBool(eq);
	}


	map1_rng = vdmMapRng(map1);
	map2_rng = vdmMapRng(map2);
	if(!equals(map1_rng, map2_rng))
	{
		eq = false;

		vdmFree(map1_dom);
		vdmFree(map2_dom);
		vdmFree(map1_rng);
		vdmFree(map2_rng);

		return newBool(eq);
	}

	UNWRAP_COLLECTION(m1, map1_dom);

	for (i = 0; i < m1->size; i++)
	{
		key1 = m1->value[i];
		val1 = vdmMapApply(map1, key1);

		key2 = m1->value[i];
		val2 = vdmMapApply(map2, key2);

		if(!equals(val1, val2))
		{
			eq = false;

			vdmFree(val1);
			vdmFree(val2);
			vdmFree(map1_dom);
			vdmFree(map2_dom);
			vdmFree(map1_rng);
			vdmFree(map2_rng);

			return newBool(eq);
		}
	}

	vdmFree(val1);
	vdmFree(val2);
	vdmFree(map1_dom);
	vdmFree(map2_dom);
	vdmFree(map1_rng);
	vdmFree(map2_rng);

	return newBool(eq);

}
#endif /* NO_MAPS */
