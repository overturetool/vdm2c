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


/*  VERSION: For the version of VDM2C used to generate this project, refer to one of the generated files.  */


#include "VdmMap.h"

#include <stdio.h> /* FIXME remove all printf!  */
#include <stdarg.h>

#define ASSERT_CHECK(s) assert(s->type == VDM_MAP && "Value is not a map")

#if !defined(NO_MAPS) || !defined(NO_SEQS)


TVP newMap()
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	assert(ptr != NULL);

	ptr->chain = NULL;

	return newTypeValue(VDM_MAP, (TypedValueType
	)
			{ .ptr = ptr });
}

void freeChain(struct KVPair *chain)
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
	freeChain(m->chain);

	free(m);
}

struct KVPair* cloneChain(struct KVPair *chain)
{
	struct KVPair *entry;

	if(chain == NULL)
		return NULL;

	entry = (struct KVPair *)malloc(sizeof(struct KVPair));
	assert(entry != NULL);

	entry->key = vdmClone(chain->key);
	entry->value = vdmClone(chain->value);
	entry->next = cloneChain(chain->next);

	return entry;
}

struct Map* cloneMap(struct Map *m)
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	assert(ptr != NULL);

	ptr->chain = cloneChain(m->chain);

	return ptr;
}


TVP newMapGC()
{
	struct Map* ptr = (struct Map*) malloc(sizeof(struct Map));
	assert(ptr != NULL);

	ptr->chain = NULL;

	return newTypeValueGC(VDM_MAP, (TypedValueType
	)
			{ .ptr = ptr });
}


/* Not a very useful function, but here to support the map comprehension mechanism.  */
TVP newMapVar(size_t size, size_t expected_size, ...)
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
	assert(ptr != NULL);
	ptr->chain = NULL;

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
TVP newMapVarGC(size_t size, size_t expected_size, ...)
{
	struct Map* ptr;
	TVP key;
	TVP value;
	TVP theMap;
	int i;

	if(size == 0)
	{
		return newMapGC();
	}

	ptr = (struct Map*) malloc(sizeof(struct Map));
	assert(ptr != NULL);

	ptr->chain = NULL;

	theMap = newTypeValueGC(VDM_MAP, (TypedValueType){ .ptr = ptr });

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
	struct KVPair *pair, *prev;

	ASSERT_CHECK(map);

	UNWRAP_MAP(m, map);

	/*  Check whether this is a new map.  */
	if(m->chain == NULL)
	{
		m->chain = (struct KVPair*) malloc(sizeof(struct KVPair));
		assert(m->chain != NULL);

		m->chain->key = vdmClone(key);
		m->chain->value = vdmClone(value);
		m->chain->next = NULL;

		return;
	}

	pair = m->chain;

	while(pair != NULL)
	{
		if(equals(pair->key, key))
		{
			vdmFree(pair->value);
			pair->value = vdmClone(value);
			return;
		}
		prev = pair;
		pair = pair->next;
	}

	prev->next = (struct KVPair*) malloc(sizeof(struct KVPair));
	assert(prev->next != NULL);

	prev->next->key = vdmClone(key);
	prev->next->value = vdmClone(value);
	prev->next->next = NULL;
}


void vdmMapUpdate(TVP map, TVP key, TVP value)
{
	vdmMapAdd(map, key, value);
}



/*  TODO: Apply does not work if the key is not found  */
TVP vdmMapApply(TVP map, TVP key)
{
	struct KVPair *pair;

	ASSERT_CHECK(map);
	UNWRAP_MAP(m, map);

	pair = m->chain;

	while(pair != NULL)
	{
		if(equals(pair->key, key))
			return vdmClone(pair->value);

		pair = pair->next;
	}

	assert(false && "Key not found.");
	return NULL;
}


/*  TODO: Apply does not work if the key is not found  */
TVP vdmMapApplyGC(TVP map, TVP key)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapApply(map, key);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
}



TVP vdmMapDom(TVP map)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);

	int mapsize = 0;
	struct KVPair *currentry;

	currentry = m->chain;

	while(currentry != NULL)
	{
		mapsize += 1;
		currentry = currentry->next;
	}

	TVP arr[mapsize];

	/* Reusing this variable.  */
	mapsize = 0;

	/* Get keys.  */
	currentry = m->chain;

	while(currentry != NULL)
	{
		arr[mapsize] = currentry->key;
		mapsize += 1;
		currentry = currentry->next;
	}

	TVP res = newSetWithValues(mapsize, arr);
	return res;
}


TVP vdmMapDomGC(TVP map)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapDom(map);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
}



TVP vdmMapRng(TVP map)
{
	/* Assert map  */
	ASSERT_CHECK(map);

	/*  Get map size  */
	UNWRAP_MAP(m,map);

	int mapsize = 0;
	struct KVPair *currentry;

	currentry = m->chain;

	while(currentry != NULL)
	{
		mapsize += 1;
		currentry = currentry->next;
	}

	TVP arr[mapsize];

	/* Reusing this variable.  */
	mapsize = 0;

	/* Get values.  */
	currentry = m->chain;

	while(currentry != NULL)
	{
		arr[mapsize] = currentry->value;
		mapsize += 1;
		currentry = currentry->next;
	}

	TVP res = newSetWithValues(mapsize, arr);
	return res;
}


TVP vdmMapRngGC(TVP map)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapRng(map);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
}



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


TVP vdmMapMunionGC(TVP map1, TVP map2)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapMunion(map1, map2);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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


TVP vdmMapOverrideGC(TVP map1, TVP map2)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapOverride(map1, map2);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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


TVP vdmMapMergeGC(TVP set)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapMerge(set);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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


TVP vdmMapDomRestrictToGC(TVP set,TVP map)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapDomRestrictTo(set, map);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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



TVP vdmMapDomRestrictByGC(TVP set,TVP map)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapDomRestrictBy(set, map);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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


TVP vdmMapRngRestrictToGC(TVP map, TVP set)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapRngRestrictTo(map, set);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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


TVP vdmMapRngRestrictByGC(TVP map, TVP set)
{
	ASSERT_CHECK(map);
	TVP tmp;
	TVP res;

	tmp = vdmMapRngRestrictBy(map, set);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
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


TVP vdmMapInverseGC(TVP map)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapInverse(map);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
}


TVP vdmMapEquals(TVP map1, TVP map2){

	/* Assert map  */
	ASSERT_CHECK(map1);
	ASSERT_CHECK(map2);

	bool eq = true;

	TVP key1;
	TVP val1 = NULL;
	TVP key2 = NULL;
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

TVP vdmMapCompose(TVP a, TVP b)
{
	TVP domB;
	TVP rngB;
	TVP domA;
	TVP res;
	TVP tmp1;
	TVP tmp2;
	TVP tmp3;
	int i, numPairs;

	domB = vdmMapDom(b);
	rngB = vdmMapRng(b);
	domA = vdmMapDom(a);

	res = vdmSetSubset(rngB, domA);

	assert(res->value.boolVal);
	vdmFree(res);

	tmp1 = vdmSetCard(domB);
	numPairs = tmp1->value.intVal;
	vdmFree(tmp1);

	res = newMap();

	for(i = 0; i < numPairs; i++)
	{
		tmp1 = vdmSetElementAt(domB, i);
		tmp2 = vdmMapApply(b, tmp1);
		tmp3 = vdmMapApply(a, tmp2);

		vdmMapGrow(res, tmp1, tmp3);

		vdmFree(tmp1);
		vdmFree(tmp2);
		vdmFree(tmp3);
	}

	vdmFree(domB);
	vdmFree(domA);
	vdmFree(rngB);

	return res;
}

TVP vdmMapComposeGC(TVP a, TVP b)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapCompose(a, b);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
}

TVP vdmMapIterate(TVP a, TVP num)
{
	TVP domA;
	TVP rngA;
	TVP tmp1;
	TVP tmp2;
	TVP res;
	int i, iters = num->value.intVal;

	domA = vdmMapDom(a);
	rngA = vdmMapRng(a);
	tmp1 = vdmSetCard(domA);

	if(iters == 0)
	{
		res = newMap();

		for(i = 0; i < tmp1->value.intVal; i++)
		{
			tmp2 = vdmSetElementAt(domA, i);
			vdmMapGrow(res, tmp2, tmp2);
			vdmFree(tmp2);
		}

		vdmFree(domA);
		vdmFree(rngA);
		vdmFree(tmp1);

		return res;
	}

	if(iters == 1)
		return vdmClone(a);

	res = vdmSetSubset(rngA, domA);
	assert(res->value.boolVal && "Range-domain incompatibility in map iteration.\n");
	vdmFree(res);

	res = vdmClone(a);
	for(i = 0; i < iters - 1; i++)
	{
		tmp1 = vdmMapCompose(a, res);
		vdmFree(res);
		res = vdmClone(tmp1);
		vdmFree(tmp1);
	}

	vdmFree(domA);
	vdmFree(rngA);

	return res;
}

TVP vdmMapIterateGC(TVP a, TVP num)
{
	TVP tmp;
	TVP res;

	tmp = vdmMapIterate(a, num);
	res = vdmCloneGC(tmp);
	vdmFree(tmp);

	return res;
}
#endif /* NO_MAPS || NO_SEQS */
