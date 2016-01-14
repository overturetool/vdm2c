/*
 * VdmSet.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */
#include <string.h>
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
	struct TypedValue** value = (struct TypedValue**)calloc(bufsize, sizeof(struct TypedValue*));

	for (int i = 0; i < size; i++)
	{
		TVP v = vdmClone(elements[i]); // set binding

		if(count >= bufsize)
		{
			//buffer too small add memory chunk
			bufsize += DEFAULT_SET_COMP_BUFFER_STEPSIZE;
			value = (struct TypedValue**)realloc(value, bufsize * sizeof(struct TypedValue*));
		}
		vdmSetAdd(value, &count,v);
	}

	TVP res = newCollectionWithValues(VDM_SET, count, value);

	//This crashes for sets larger than 5 and also leaks memory (?) because it's not a deep free (?).

	for(int i = 0; i < count; i++)
	{
		vdmFree(value[i]);
	}
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
			bufsize += DEFAULT_SET_COMP_BUFFER_STEPSIZE;
			value = (struct TypedValue**)realloc(value,bufsize * sizeof(struct TypedValue*));
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

	//TODO:  Probably needs freeing somewhere.
	return newBool(found);
}



TVP vdmSetNotMemberOf(TVP set, TVP element)
{
	//TODO:  Probably needs freeing somewhere.
	return newBool(!(vdmSetMemberOf(set, element))->value.boolVal);
}



TVP vdmSetUnion(TVP set1, TVP set2)
{
	TVP *newvalues;

	UNWRAP_COLLECTION(col1, set1);
	UNWRAP_COLLECTION(col2, set2);
	//col1 and col2 of type struct Collection*.

	//This can not be done because col1 and col2 are not valid at the time when
	//newcol1 and newcol2 are declared, hence the bogus length of one of them.
	//TVP newcol1[col1->size];
	//TVP newcol2[col2->size];
	//Something like the following is fine because memory layout is not involved.
	//int a = col1->size;

	//newcol1 = (TVP*)malloc(col1->size * sizeof(TVP));
	//newcol2 = (TVP*)malloc(col2->size * sizeof(TVP));

	newvalues = (TVP*)malloc((col1->size + col2->size) * sizeof(TVP));
	for(int i = 0; i < col1->size; i++)
	{
		newvalues[i] = vdmClone((col1->value)[i]);
	}

	for(int i = col1->size; i < (col1-> size + col2->size); i++)
	{
		newvalues[i] = vdmClone((col2->value)[i - col1->size]);
	}

	return newSetWithValues(col1->size + col2->size, newvalues);
}



TVP vdmSetInter(TVP set1, TVP set2)
{}



TVP vdmSetDifference(TVP set1, TVP set2)
{}



TVP vdmSetSubset(TVP set1, TVP set2)
{
	TVP res;

	UNWRAP_COLLECTION(col1, set1);
	UNWRAP_COLLECTION(col2, set2);

	if(col1-> size > col2->size)
	{
		return newBool(false);
	}

	for(int i = 0; i < col1->size; i++)
	{

		res = vdmSetMemberOf(set2, (col1->value)[i]);
		if(!res->value.boolVal)
		{
			free(res);
			return newBool(false);
		}
		free(res);
	}

	return newBool(true);
}



TVP vdmSetProperSubset(TVP set1, TVP set2)
{}



TVP vdmSetEquals(TVP set1, TVP set2)
{}



TVP vdmSetNotEquals(TVP set1, TVP set2)
{}



TVP vdmSetCard(TVP set)
{
	ASSERT_CHECK(set);
	UNWRAP_COLLECTION(col, set);

	//TODO:  This requires freeing somewhere;
	return newInt(col->size);
}



TVP vdmSetDunion(TVP set)
{}



TVP vdmSetDinter(TVP set)
{}



TVP vdmSetPower(TVP set)
{}
