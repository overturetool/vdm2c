/*
 * VdmSet.h
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#ifndef LIB_VDMSET_H_
#define LIB_VDMSET_H_

#include "TypedValue.h"

struct TypedValue* newSet(size_t size);
struct TypedValue* newSetWithValues(size_t size,TVP* elements);

TVP vdmSetMemberOf(TVP set, TVP element);
TVP vdmSetNotMemberOf(TVP set, TVP element);
TVP vdmSetUnion(TVP set1, TVP set2);
TVP vdmSetInter(TVP set1, TVP set2);
TVP vdmSetDifference(TVP set1, TVP set2);
TVP vdmSetSubset(TVP set1, TVP set2);
TVP vdmSetProperSubset(TVP set1, TVP set2);
TVP vdmSetEquals(TVP set1, TVP set2);
TVP vdmSetInEquals(TVP set1, TVP set2);
TVP vdmSetCard(TVP set);
TVP vdmSetDunion(TVP set);
TVP vdmSetDinter(TVP set);
TVP vdmSetPower(TVP set);

#endif /* LIB_VDMSET_H_ */
