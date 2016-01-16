/*
 * VdmSet.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */
#include <string.h>
#include <math.h>
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

	return newBool(found);
}



TVP vdmSetNotMemberOf(TVP set, TVP element)
{
	TVP res;
	bool resval;

	ASSERT_CHECK(set);

	res = vdmSetMemberOf(set, element);
	resval = res->value.boolVal;

	vdmFree(res);

	return newBool(!resval);
}



TVP vdmSetUnion(TVP set1, TVP set2)
{
	TVP *newvalues;

	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

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
{
	TVP testelem;
	TVP inter;
	TVP tmpset1;
	TVP tmpset2;
	TVP res;

	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

	UNWRAP_COLLECTION(col1, set1);
	UNWRAP_COLLECTION(col2, set2);

	if(col1->size == 0 || col2->size ==0)
	{
		return newSetWithValues(0, NULL);
	}

	inter = newSetWithValues(0, NULL);

	for(int i = 0; i < col1->size; i++)
	{
		res = vdmSetMemberOf(set2, (col1->value)[i]);

		if(res->value.boolVal)
		{
			//add to intersection set
			tmpset1 = newSetVar(1, col1->value[i]);
			tmpset2 = vdmSetUnion(inter, tmpset1);
			vdmFree(inter);
			inter = tmpset2;
			vdmFree(tmpset1);
		}
		vdmFree(res);
	}

	return inter;
}



TVP vdmSetDifference(TVP set1, TVP set2)
{
	TVP tmpset1;
	TVP tmpset2;
	TVP resultset;
	TVP tmpelem;
	TVP res;

	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

	UNWRAP_COLLECTION(col1, set1);
	UNWRAP_COLLECTION(col2, set2);

	if(col1->size == 0 || col2->size == 0)
	{
		return set1;
	}

	resultset = newSetWithValues(0, NULL);

	for(int i = 0; i < col1->size; i++)
	{
		res = vdmSetNotMemberOf(set2, (col1->value)[i]);
		if(res->value.boolVal)
		{
			tmpset1 = newSetVar(1, (col1->value)[i]);
			tmpset2 = vdmSetUnion(resultset, tmpset1);
			vdmFree(resultset);
			resultset = tmpset2;
			vdmFree(tmpset1);
		}
		vdmFree(res);
	}

	return resultset;
}



TVP vdmSetSubset(TVP set1, TVP set2)
{
	TVP res;

	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

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
{
	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

	UNWRAP_COLLECTION(col1, set1);
	UNWRAP_COLLECTION(col2, set2);

	if(col1->size >= col2->size)
	{
		return newBool(false);
	}

	return vdmSetSubset(set1, set2);
}



TVP vdmSetEquals(TVP set1, TVP set2)
{
	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

	TVP res1;
	TVP res2;
	bool r;

	res1 = vdmSetSubset(set1, set2);
	res2 = vdmSetSubset(set2, set1);

	r = res1->value.boolVal && res2->value.boolVal;
	vdmFree(res1);
	vdmFree(res2);

	return newBool(r);
}



TVP vdmSetNotEquals(TVP set1, TVP set2)
{
	TVP res;
	bool tmp;

	ASSERT_CHECK(set1);
	ASSERT_CHECK(set2);

	res = vdmSetEquals(set1, set2);
	tmp = res->value.boolVal;
	vdmFree(res);

	return newBool(!tmp);
}



TVP vdmSetCard(TVP set)
{
	ASSERT_CHECK(set);
	UNWRAP_COLLECTION(col, set);

	return newInt(col->size);
}



TVP vdmSetDunion(TVP set)
{
	TVP unionset;
	TVP set1;

	//Preliminary checks.
	ASSERT_CHECK(set);

	UNWRAP_COLLECTION(col, set);
	for(int i = 0; i < col->size; i++)
	{
		ASSERT_CHECK((col->value)[i]);
	}

	//Initialize final set.
	unionset = newSetVar(0, NULL);

	//Build union set.
	for(int i = 0; i < col->size; i++)
	{
		set1 = vdmSetUnion(unionset, (col->value)[i]);
		vdmFree(unionset);
		unionset = set1;
	}

	return unionset;
}



TVP vdmSetDinter(TVP set)
{
	TVP interset;
	TVP set1;

	//Preliminary checks.
	ASSERT_CHECK(set);

	UNWRAP_COLLECTION(col, set);
	for(int i = 0; i < col->size; i++)
	{
		ASSERT_CHECK((col->value)[i]);
	}

	//Initialize final set.
	interset = vdmClone((col->value)[0]);

	//Build intersection set.
	for(int i = 1; i < col->size; i++)
	{
		set1 = vdmSetInter(interset, (col->value)[i]);
		vdmFree(interset);
		interset = set1;
	}

	return interset;
}



TVP vdmSetPower(TVP set)
{
	bool *whichelems, carry;
	TVP set1;
	TVP set2;
	TVP powerset;
	TVP thissizeset;

	ASSERT_CHECK(set);

	UNWRAP_COLLECTION(col, set);

	//Array of picks from whole collection col.
	whichelems = (bool*)malloc(col->size * sizeof(bool));
	for(int i = 0; i < col->size; i++)
	{
		whichelems[i] = false;
	}

	powerset = newSetVar(0, NULL);
	carry = false;
	for(int i = 1; i < pow(2, col->size); i++)
	{
		//Increment picks array.
		if(!whichelems[0])
		{
			carry = false;
			whichelems[0] = true;
		}
		else
		{
			carry = true;
			whichelems[0] = false;
		}

		for(int k = 1; k < col->size; k++)
		{
			if(!whichelems[k])
			{
				if(!carry)
				{
				}
				else
				{
					whichelems[k] = true;
					carry = false;
				}
			}
			else
			{
				if(!carry)
				{
				}
				else
				{
					whichelems[k] = false;
					carry = true;
				}
			}
		}

		thissizeset = newSetVar(0, NULL);
		for(int j = 0; j < col->size; j++)
		{
			if(whichelems[j])
			{
				//Add element to set corresponding to current size.
				set1 = newSetVar(1, (col->value)[j]);
				set2 = vdmSetUnion(thissizeset, set1);
				vdmFree(thissizeset);
				thissizeset = set2;
				vdmFree(set1);
			}
		}

		set1 = newSetVar(1, thissizeset);
		set2 = vdmSetUnion(powerset, thissizeset);
		vdmFree(powerset);
		powerset = set2;
		vdmFree(set1);
		vdmFree(thissizeset);
	}

	free(whichelems);

	return powerset;
}

