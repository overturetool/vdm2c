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
 * VdmMap.h
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */


/*  VERSION: For the version of VDM2C used to generate this project, refer to one of the generated files.  */


#ifndef LIB_VDMMAP_H_
#define LIB_VDMMAP_H_

#include "Vdm.h"
#include "VdmSet.h"
#include <limits.h>
#include <assert.h>

#if !defined(NO_MAPS) || !defined(NO_SEQS)

#define UNWRAP_MAP(var, map) struct Map* var = (struct Map*)map->value.ptr


struct KVPair{
	TVP key;
	TVP value;
	struct KVPair *next;
};

struct Map
{
	struct KVPair *chain;
};


void freeMap(struct Map *m);
struct Map* cloneMap(struct Map *m);

/* util method for adding maplets  */
void vdmMapAdd(TVP map,TVP key, TVP value);
void vdmMapUpdate(TVP map, TVP key, TVP value);

TVP newMapVar(size_t, size_t, ...);
TVP newMapVarGC(size_t size, size_t expected_size, ...);
void vdmMapGrow(TVP, TVP, TVP);
void vdmMapFit(TVP);

/* VDM map operators  */
TVP vdmMapDom(TVP map);
TVP vdmMapDomGC(TVP map);
TVP vdmMapRng(TVP map);
TVP vdmMapRngGC(TVP map);
TVP vdmMapMunion(TVP map1, TVP map2);
TVP vdmMapMunionGC(TVP map1, TVP map2);
TVP vdmMapOverride(TVP map1, TVP map2);
TVP vdmMapOverrideGC(TVP map1, TVP map2);
TVP vdmMapMerge(TVP set);
TVP vdmMapMergeGC(TVP set);
TVP vdmMapDomRestrictTo(TVP set,TVP map);
TVP vdmMapDomRestrictToGC(TVP set,TVP map);
TVP vdmMapDomRestrictBy(TVP set,TVP map);
TVP vdmMapDomRestrictByGC(TVP set,TVP map);
TVP vdmMapRngRestrictTo(TVP map, TVP set);
TVP vdmMapRngRestrictToGC(TVP map, TVP set);
TVP vdmMapRngRestrictBy(TVP map, TVP set);
TVP vdmMapRngRestrictByGC(TVP map, TVP set);
TVP vdmMapApply(TVP map, TVP key);
TVP vdmMapApplyGC(TVP map, TVP key);
TVP vdmMapInverse(TVP map);
TVP vdmMapInverseGC(TVP map);
TVP vdmMapEquals(TVP map1, TVP map2);
TVP vdmMapCompose(TVP a, TVP b);
TVP vdmMapComposeGC(TVP a, TVP b);
TVP vdmMapIterate(TVP a, TVP num);
TVP vdmMapIterateGC(TVP a, TVP num);

#endif /* NO_MAPS */
#endif /* LIB_VDMMAP_H_ */
