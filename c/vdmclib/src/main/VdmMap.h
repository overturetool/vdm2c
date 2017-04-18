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

#define UNWRAP_MAP(var, map) struct Map* var = (struct Map*)map->value.ptr


struct entry_s {
	TVP key;
	TVP value;
	struct entry_s *next;
};

typedef struct entry_s entry_t;

struct hashtable_s {
	int size;
	entry_t **chain;
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

void freeMap(struct Map *m);
struct Map* cloneMap(struct Map *m);

//util method for adding maplets
void vdmMapAdd(TVP map,TVP key, TVP value);
void vdmMapUpdate(TVP map, TVP key, TVP value);

TVP newMapVarToGrow(size_t, size_t, ...);
TVP newMapVarToGrowGC(size_t size, size_t expected_size, TVP *from, ...);
void vdmMapGrow(TVP, TVP, TVP);
void vdmMapFit(TVP);

//VDM map operators
TVP vdmMapDom(TVP map);
TVP vdmMapDomGC(TVP map, TVP *from);
TVP vdmMapRng(TVP map);
TVP vdmMapRngGC(TVP map, TVP *from);
TVP vdmMapMunion(TVP map1, TVP map2);
TVP vdmMapMunionGC(TVP map1, TVP map2, TVP *from);
TVP vdmMapOverride(TVP map1, TVP map2);
TVP vdmMapOverrideGC(TVP map1, TVP map2, TVP *from);
TVP vdmMapMerge(TVP set);
TVP vdmMapMergeGC(TVP set, TVP *from);
TVP vdmMapDomRestrictTo(TVP set,TVP map);
TVP vdmMapDomRestrictToGC(TVP set,TVP map, TVP *from);
TVP vdmMapDomRestrictBy(TVP set,TVP map);
TVP vdmMapDomRestrictByGC(TVP set,TVP map, TVP *from);
TVP vdmMapRngRestrictTo(TVP set,TVP map);
TVP vdmMapRngRestrictToGC(TVP set,TVP map, TVP *from);
TVP vdmMapRngRestrictBy(TVP set,TVP map);
TVP vdmMapRngRestrictByGC(TVP set,TVP map, TVP *from);
TVP vdmMapApply(TVP map, TVP key);
TVP vdmMapApplyGC(TVP map, TVP key, TVP *from);
TVP vdmMapInverse(TVP map);
TVP vdmMapInverseGC(TVP map, TVP *from);

TVP vdmMapEquals(TVP map1, TVP map2);

#ifdef WITH_GLIB_HASH
guint vdm_typedvalue_hash(gconstpointer v);
gboolean vdm_typedvalue_equal(gconstpointer v1, gconstpointer v2);
void vdm_g_free(gpointer mem);
#endif


#endif /* NO_MAPS */
#endif /* LIB_VDMMAP_H_ */
