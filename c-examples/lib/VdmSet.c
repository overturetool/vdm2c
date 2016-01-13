/*
 * VdmSet.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#include "VdmSet.h"

#define ASSERT_CHECK(s) assert(s->type == VDM_SET && "Value is not a set")
#define DEFAULT_SET_COMP_BUFFER 2
#define DEFAULT_SET_COMP_BUFFER_STEPSIZE 10



//Utility functions.
//------------------------------------------------
static void vdmSetAdd(struct TypedValue** value, int* index, TVP newValue)
{
	bool found = false;

	for (int i = 0; i < *index; i++)
	{
		found |= equals(value[i],newValue);
	}

	if(!found)
	{
		//(*value)->value.intVal = 3;
		value[*index] = newValue;
		*index = (*index) + 1;
	}
}
//End utility functions
//------------------------------------------------



struct TypedValue* newSetWithValues(size_t size, TVP* elements)
{
	int count = 0;
	int bufsize = DEFAULT_SET_COMP_BUFFER;
	struct TypedValue** value = (struct TypedValue**) calloc(bufsize, sizeof(struct TypedValue*));

	for (int i = 0; i < size; i++)
	{
		TVP v = vdmClone(elements[i]); // set binding

		if(count >= bufsize)
		{
			//buffer too small add memory chunk
			bufsize+=(DEFAULT_SET_COMP_BUFFER_STEPSIZE*sizeof(struct TypedValue*));
			value = (struct TypedValue**)realloc(value,bufsize);
		}
		vdmSetAdd(value, &count,v);

	}

	TVP res = newCollectionWithValues(VDM_SET,count,value);
	free(value);
	return res;
}

struct TypedValue* newSetVar(size_t size, ...)
{

	va_list ap;
	va_start(ap, size);

	int count = 0;

	int bufsize = DEFAULT_SET_COMP_BUFFER;
	struct TypedValue** value = (struct TypedValue**) calloc(bufsize, sizeof(struct TypedValue*));

	for (int i = 0; i < size; i++)
	{
		TVP arg = va_arg(ap, TVP);
		TVP v= vdmClone(arg); // set binding

		if(count>=bufsize)
		{
			//buffer too small add memory chunk
			bufsize+=(DEFAULT_SET_COMP_BUFFER_STEPSIZE*sizeof(struct TypedValue*));
			value = (struct TypedValue**)realloc(value,bufsize);
		}
		vdmSetAdd(value,&count,v);
	}

	va_end(ap);

	TVP res = newCollectionWithValues(VDM_SET,count,value);
	free(value);
	return res;
}



TVP vdmSetMemberOf(TVP set, TVP element)
{
	ASSERT_CHECK(set);

	UNWRAP_COLLECTION(col,set);

	bool found = false;

	for (int i = 0; i < col->size; i++)
	{
		found|= equals(col->value[i],element);
	}
	return newBool(found);
}



TVP vdmSetNotMemberOf(TVP set, TVP element)
{}



TVP vdmSetUnion(TVP set1, TVP set2)
{}



TVP vdmSetInter(TVP set1, TVP set2)
{}



TVP vdmSetDifference(TVP set1, TVP set2)
{}



TVP vdmSetSubset(TVP set1, TVP set2)
{}



TVP vdmSetProperSubset(TVP set1, TVP set2)
{}



TVP vdmSetEquals(TVP set1, TVP set2)
{}



TVP vdmSetInEquals(TVP set1, TVP set2)
{}



TVP vdmSetCard(TVP set)
{
	ASSERT_CHECK(set);
	UNWRAP_COLLECTION(col, set);

	//This requires freeing somewhere;
	return newInt(col->size);
}



TVP vdmSetDunion(TVP set)
{}



TVP vdmSetDinter(TVP set)
{}



TVP vdmSetPower(TVP set)
{}
