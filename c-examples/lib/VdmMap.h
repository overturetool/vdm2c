/*
 * VdmMap.h
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */

#ifndef LIB_VDMMAP_H_
#define LIB_VDMMAP_H_

#include "TypedValue.h"
#include "VdmSet.h"
#include <glib.h>

#include <assert.h>

#define UNWRAP_MAP(var,map) struct Map* var = (struct Map*)map->value.ptr

struct Map
{
	GHashTable *table;
};

struct TypedValue* newMap();

//util method for adding maplets
void vdmMapAdd(TVP map,TVP key, TVP value);

//VDM map operators
TVP vdmMapDom(TVP map);
TVP vdmMapRng(TVP map);
TVP vdmMapMunion(TVP map1, TVP map2);
TVP vdmMapOverride(TVP map1, TVP map2);
TVP vdmMapMerge(TVP set);
TVP vdmMapDomRestrictTo(TVP set,TVP map);
TVP vdmMapDomRestrictBy(TVP set,TVP map);
TVP vdmMapRngRestrictTo(TVP set,TVP map);
TVP vdmMapRngRestrictBy(TVP set,TVP map);
TVP vdmMapApply(TVP map, TVP key);
TVP vdmMapInverse(TVP map);

bool vdmMapEquals(TVP map1, TVP map2);
bool vdmMapInEquals(TVP map1, TVP map2);

guint vdm_typedvalue_hash(gconstpointer v);
gboolean vdm_typedvalue_equal(gconstpointer v1, gconstpointer v2);
void vdm_g_free(gpointer mem);

#endif /* LIB_VDMMAP_H_ */
