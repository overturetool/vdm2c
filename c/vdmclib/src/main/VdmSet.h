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
 * VdmSet.h
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#ifndef LIB_VDMSET_H_
#define LIB_VDMSET_H_

#include "Vdm.h"

#include <stdio.h>

#ifndef NO_SETS

/*
 * Create new empty set
 */
/* TVP newSet(size_t size);  */
/*
 * Create new set from array of elements
 */
TVP newSetWithValues(size_t size,TVP* elements);
TVP newSetWithValuesGC(size_t size,TVP* elements);
/*
 * Create new set from variadic list of elements
 */
TVP newSetVar(size_t size, ...);
TVP newSetVarGC(size_t size, ...);
TVP newSetVarToGrow(size_t size, size_t expected_size, ...);
TVP newSetVarToGrowGC(size_t size, size_t expected_size,  ...);

void vdmSetGrow(TVP set, TVP element);
void vdmSetFit(TVP set);
TVP vdmSetEnumerateSetOfInts(int lower, int upper);
TVP vdmSetElementAt(TVP set, int loc);
TVP vdmSetElementAtGC(TVP set, int loc);
TVP vdmSetMemberOf(TVP set, TVP element);
TVP vdmSetMemberOfGC(TVP set, TVP element);
TVP vdmSetNotMemberOf(TVP set, TVP element);
TVP vdmSetNotMemberOfGC(TVP set, TVP element);
TVP vdmSetUnion(TVP set1, TVP set2);
TVP vdmSetUnionGC(TVP set1, TVP set2);
TVP vdmSetInter(TVP set1, TVP set2);
TVP vdmSetInterGC(TVP set1, TVP set2);
TVP vdmSetDifference(TVP set1, TVP set2);
TVP vdmSetDifferenceGC(TVP set1, TVP set2);
TVP vdmSetSubset(TVP set1, TVP set2);
TVP vdmSetSubsetGC(TVP set1, TVP set2);
TVP vdmSetProperSubset(TVP set1, TVP set2);
TVP vdmSetProperSubsetGC(TVP set1, TVP set2);
TVP vdmSetEquals(TVP set1, TVP set2);
TVP vdmSetCard(TVP set);
TVP vdmSetCardGC(TVP set);
TVP vdmSetDunion(TVP set);
TVP vdmSetDunionGC(TVP set);
TVP vdmSetDinter(TVP set);
TVP vdmSetDinterGC(TVP set);
TVP vdmSetPower(TVP set);
TVP vdmSetPowerGC(TVP set);

#endif /* NO_SETS */
#endif /* LIB_VDMSET_H_ */
