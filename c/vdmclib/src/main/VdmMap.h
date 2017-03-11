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

/*
 * VdmMap.h
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */

#ifndef LIB_VDMMAP_H_
#define LIB_VDMMAP_H_

#include "Vdm.h"
#include "VdmSet.h"

#ifdef WITH_GLIB_HASH
#include <glib.h>
#else
#include <limits.h>
#endif


#include <assert.h>

#ifndef NO_MAPS

#define UNWRAP_MAP(var,map) struct Map* var = (struct Map*)map->value.ptr


struct entry_s {
	TVP key;
	TVP value;
	struct entry_s *next;
};

typedef struct entry_s entry_t;

struct hashtable_s {
	int size;
	struct entry_s **table;
};

typedef struct hashtable_s hashtable_t;

struct Map
{
#ifdef WITH_GLIB_HASH
	GHashTable *table;
#else
	hashtable_t *table;
#endif
};

hashtable_t *ht_create( int size );


struct TypedValue* newMap();

//util method for adding maplets
void vdmMapAdd(TVP map,TVP key, TVP value);
void vdmMapUpdate(TVP map, TVP key, TVP value);

TVP newMapVarToGrow(size_t, size_t, ...);
void vdmMapGrow(TVP, TVP, TVP);
void vdmMapFit(TVP);

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

TVP vdmMapEquals(TVP map1, TVP map2);
bool vdmMapInEquals(TVP map1, TVP map2);

#ifdef WITH_GLIB_HASH
guint vdm_typedvalue_hash(gconstpointer v);
gboolean vdm_typedvalue_equal(gconstpointer v1, gconstpointer v2);
void vdm_g_free(gpointer mem);
#endif


#endif /* NO_MAPS */
#endif /* LIB_VDMMAP_H_ */
