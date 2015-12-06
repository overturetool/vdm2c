/*
 * VdmSet.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#include "VdmSet.h"

struct TypedValue* newSet(size_t size)
{
	return newCollection(size, VDM_SET);
}

struct TypedValue* newSetWithValues(size_t size,TVP* elements)
{
	//FIXME
	return newCollectionWithValues(VDM_SET,size,elements);
}
