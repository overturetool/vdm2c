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

#include "gtest/gtest.h"

extern "C"
{
#include "lib/Vdm.h"
#include <stdio.h>
#include <math.h>
//Maximum values have more recognizable hex representations.
#include <limits.h>
}



//Utility functions
//------------------------------------------------
//TODO use correct set construction
static TVP newSet(int size, int* arr)
{
	struct TypedValue** value = (struct TypedValue**) calloc(size, sizeof(struct TypedValue*));

	for (int i = 0; i < size; i++)
	{
		value[i] = newInt(arr[i]);
	}

	return newSetWithValues(size, value);
}
//End utility functions
//------------------------------------------------


//#define COMPREHENSION(exp,var,S,P)

#define DEFAULT_SET_COMP_BUFFER 1
#define DEFAULT_SET_COMP_BUFFER_STEPSIZE 10



TEST(Expression_Set, setGrow)
{
	TVP set1;
	TVP set2;
	TVP res;

	//Create test set.
	set1 = newSetVarToGrow(1, 2, newInt(1));
	set2 = newSetVar(2, newInt(1), newInt(2));

	vdmSetGrow(set1, newInt(2));

	res = vdmSetEquals(set1, set2);
	EXPECT_EQ(true, res->value.boolVal);

	vdmSetGrow(set1, newInt(3));

	UNWRAP_COLLECTION(col, set1);
	vdmFree(res);
	res = vdmSetEquals(set1, set2);
	EXPECT_EQ(false, res->value.boolVal);

	//Clean up.
	vdmFree(res);
	vdmFree(set1);
	vdmFree(set2);
}



TEST(Expression_Set, setFit)
{
	TVP set1;
	TVP set2;
	TVP res;

	//Create test set.
	set1 = newSetVarToGrow(1, 2, newInt(1));
	set2 = newSetVar(2, newInt(1), newInt(2));

	vdmSetGrow(set1, newInt(2));

	res = vdmSetEquals(set1, set2);
	EXPECT_EQ(true, res->value.boolVal);

	vdmSetGrow(set1, newInt(3));

	UNWRAP_COLLECTION(col, set1);
	vdmFree(res);
	res = vdmSetEquals(set1, set2);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(res);

	vdmSetFit(set1);

	//vdmSetGrow works for any set, but if it wasn't preallocated with
	//vdmSetVarToGrow it is not as efficient.
	vdmSetGrow(set2, newInt(3));
	res = vdmEquals(set1, set2);
	EXPECT_EQ(true, res->value.boolVal);

	//Clean up.
	vdmFree(res);
	vdmFree(set1);
	vdmFree(set2);
}


TEST(Expression_Set, setEnumerateSetOfInts)
{
	int l = 0;
	int u = 3;
	TVP tmp;
	TVP res;
	TVP set = vdmSetEnumerateSetOfInts(l, u);

	res = vdmSetMemberOf(set, newInt(l));
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	res = vdmSetMemberOf(set, newInt(u));
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	tmp = newInt(1);
	res = vdmSetMemberOf(set, tmp);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);
	vdmFree(tmp);

	tmp = newInt(2);
	res = vdmSetMemberOf(set, tmp);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);
	vdmFree(tmp);

	tmp = newInt(6);
	res = vdmSetMemberOf(set, tmp);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(res);
	vdmFree(tmp);

	res = vdmSetCard(set);
	EXPECT_EQ(4, res->value.intVal);
	vdmFree(res);

	vdmFree(set);
}



TEST(Expression_Set, setElementAt)
{
	TVP set;
	TVP elem;
	TVP compelem;
	TVP res;

	set = newSetVar(3, newInt(1), newInt(2), newInt(3));

	for(int i = 0; i < 3; i++)
	{
		elem = vdmSetElementAt(set, i);
		compelem = newInt(i + 1);
		res = vdmEquals(elem, compelem);
		EXPECT_EQ(true, res->value.boolVal);
		vdmFree(elem);
		vdmFree(compelem);
		vdmFree(res);
	}

	//Clean up.
	vdmFree(set);
}


TEST(Expression_Set, setComprehension)
{
	int arr[] = { 1, 1, 2, 8, 15 };
	TVP S = newSet(5, arr); //Technically this isn't a set but we need this for testing
	TVP t = newInt(10);

	//{ exp | v in set S . P}

	TVP comp = NULL;
	{
		//S, check and unwrap
		assert(S->type == VDM_SET && "Value is not a set");
		UNWRAP_COLLECTION(col, S);

		int count = 0;
		int size = DEFAULT_SET_COMP_BUFFER;
		struct TypedValue** buf = (struct TypedValue**) calloc(size, sizeof(struct TypedValue*));

		for (int i = 0; i < col->size; i++)
		{
			TVP v= col->value[i]; // set binding

			TVP cond =vdmLessOrEqual(v,t);//P or NULL if none
			if(cond==NULL || cond->value.boolVal)
			{
				if(count>=size)
				{
					//buffer too small add memory chunk
					size+=(DEFAULT_SET_COMP_BUFFER_STEPSIZE*sizeof(struct TypedValue*));
					buf = (struct TypedValue**)realloc(buf,size);
				}
				TVP element = vdmClone(v); // exp, the vdmClone here will be inside any expression
				buf[count++]=element;
			}
			if(cond!=NULL)
			{
				vdmFree(cond);
			}
		}

		EXPECT_EQ(1, buf[0]->value.intVal);
		EXPECT_EQ(2, buf[1]->value.intVal);
		EXPECT_EQ(8, buf[2]->value.intVal);

		comp = newSetWithValues(count, buf);

		for (int i = 0; i < count; i++)
		{
			vdmFree(buf[i]);
		}
		free(buf);
	}

	//	COMPREHENSION(exp,var,S,vdmLessThan(var,t));
	EXPECT_EQ(VDM_SET, comp->type);
	UNWRAP_COLLECTION(col, comp);
	EXPECT_EQ(3, col->size);

	//Wrap up.
	vdmFree(S);
	vdmFree(t);
}



TEST(Expression_Set, setInSet)
{
	const int numelems = 10;
	TVP elem = newInt(3);
	TVP set = newSetVar(1, elem);
	struct TypedValue *randelems[100], *randelemscpy[100];

	//Make sure that we're checking for the value itself and not the newInt structure.
	vdmFree(elem);
	elem = newInt(3);

	TVP res = vdmSetMemberOf(set, elem);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(elem);

	for(int i = 0; i < numelems; i++)
	{
		randelems[i] = newInt(i);
	}

	vdmFree(set);
	set = newSetWithValues(numelems, randelems);

	for(int i = 0; i < numelems; i++)
	{
		randelemscpy[i] = vdmClone(randelems[i]);
	}

	for(int i = 0; i < numelems; i++)
	{
		vdmFree(res);
		res = vdmSetMemberOf(set, randelemscpy[i]);
		EXPECT_EQ(true, res->value.boolVal);
	}

	//Wrap up.
	vdmFree(res);
	vdmFree(set);
	for(int i = 0; i < numelems; i++)
	{
		vdmFree(randelems[i]);
		vdmFree(randelemscpy[i]);
	}
}



TEST(Expression_Set, setNotInSet)
{
	const int numelems = 10;
	TVP elem = newInt(3);
	TVP set = newSetVar(1, elem);
	struct TypedValue *randelems[100], *randelemscpy[100];

	//Make sure that we're checking for the value itself and not the newInt structure.
	vdmFree(elem);
	elem = newInt(3);

	TVP res = vdmSetNotMemberOf(set, elem);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(elem);
	vdmFree(res);

	elem = newInt(2);
	res = vdmSetNotMemberOf(set, elem);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(elem);

	for(int i = 0; i < numelems; i++)
	{
		randelems[i] = newInt(i);
	}

	vdmFree(set);
	set = newSetWithValues(numelems, randelems);

	for(int i = 0; i < numelems; i++)
	{
		randelemscpy[i] = vdmClone(randelems[i]);
	}

	for(int i = 0; i < numelems; i++)
	{
		vdmFree(res);
		res = vdmSetNotMemberOf(set, randelemscpy[i]);
		EXPECT_EQ(false, res->value.boolVal);
	}

	//Wrap up.
	vdmFree(res);
	vdmFree(set);
	for(int i = 0; i < numelems; i++)
	{
		free(randelems[i]);
		free(randelemscpy[i]);
	}
}



TEST(Expression_Set, setUnion)
{
	const int numelems1 = 101;
	const int numelems2 = 97;
	TVP randelems1[numelems1];
	TVP randelems2[numelems2];
	TVP set1;
	TVP set2;
	TVP unionset;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(i);
	}

	for(int i = 0; i < numelems2; i++)
	{
		randelems2[i] = newInt(i);
	}

	//Create the random test sets.
	set1 = newSetWithValues(numelems1, randelems1);
	set2 = newSetWithValues(numelems2, randelems2);
	unionset = vdmSetUnion(set1, set2);

	//Check membership of each test element in union.
	for(int i = 0; i < numelems1; i++)
	{
		res = vdmSetMemberOf(unionset, randelems1[i]);
		EXPECT_EQ(true, res->value.boolVal);
		vdmFree(res);
	}
	for(int i = 0; i < numelems2; i++)
	{
		res = vdmSetMemberOf(unionset, randelems2[i]);
		EXPECT_EQ(true, res->value.boolVal);
		vdmFree(res);
	}

	//Ensure that there are no other elements.
	EXPECT_EQ(true, ((struct Collection*)(unionset->value.ptr))->size <= \
			((struct Collection*)(set1->value.ptr))->size + \
			((struct Collection*)(set2->value.ptr))->size);

	//Wrap up.
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(unionset);
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	for(int i = 0; i < numelems2; i++)
	{
		vdmFree(randelems2[i]);
	}
}



TEST(Expression_Set, setIntersection)
{
	const int numelems1 = 101;
	const int numelems2 = 97;
	TVP randelems1[numelems1];
	TVP randelems2[numelems2];
	TVP set1;
	TVP set2;
	TVP interset;
	TVP res;

	//Generate the random test value collections.  Get random values modulo 100
	//to overcome the stellar performance of the random number generator.
	srand(time(0));
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt((i * i) % 100);
	}

	for(int i = 0; i < numelems2; i++)
	{
		randelems2[i] = newInt((i * i) % 100);
	}

	//Create the random test sets.
	set1 = newSetWithValues(numelems1, randelems1);
	set2 = newSetWithValues(numelems2, randelems2);
	interset = vdmSetInter(set1, set2);
	UNWRAP_COLLECTION(intercol, interset);

	//Check that intersection is a subset of both sets.
	res = vdmSetSubset(interset, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);
	res = vdmSetSubset(interset, set2);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//Ensure that intersection has at most as many elements as the smallest of the two sets.
	UNWRAP_COLLECTION(col1, set1);
	UNWRAP_COLLECTION(col2, set2);
	EXPECT_EQ(true, (intercol->size <= col1->size) && (intercol->size <= col2->size));

	//Wrap up.
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(interset);
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	for(int i = 0; i < numelems2; i++)
	{
		vdmFree(randelems2[i]);
	}
}



TEST(Expression_Set, setDifference)
{
	const int numelems1 = 101;
	const int numelems2 = 97;
	TVP randelems1[numelems1];
	TVP randelems2[numelems2];
	TVP set1;
	TVP set2;
	TVP diffset;
	TVP interset;
	TVP res;

	//Generate the random test value collections.  Get random values modulo 100
	//to overcome the stellar performance of the random number generator.
	srand(time(0));
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt((i * i) % 100);
	}

	for(int i = 0; i < numelems2; i++)
	{
		randelems2[i] = newInt((i * i) % 100);
	}

	//Create the random test sets.
	set1 = newSetWithValues(numelems1, randelems1);
	set2 = newSetWithValues(numelems2, randelems2);
	diffset = vdmSetDifference(set1, set2);
	UNWRAP_COLLECTION(diffcol, diffset);

	//Check that the difference is a subset of the original set.
	res = vdmSetSubset(diffset, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//Check that none of the elements in set2 is in the difference set.
	interset = vdmSetInter(diffset, set2);
	UNWRAP_COLLECTION(intercol, interset);
	EXPECT_EQ(true, intercol->size == 0);

	//Wrap up.
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(diffset);
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	for(int i = 0; i < numelems2; i++)
	{
		vdmFree(randelems2[i]);
	}
}



TEST(Expression_Set, setSubset)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(i);
	}

	//Create test set.
	set1 = newSetWithValues(numelems1, randelems1);

	//Set must be a subset of itself.
	res = vdmSetSubset(set1, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//Get corresponding collection of test set without duplicates.
	UNWRAP_COLLECTION(setnodupscol, set1);
	//Make this into a new but smaller set.
	set2 = newSetWithValues(setnodupscol->size - 1, setnodupscol->value);
	res = vdmSetSubset(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//And a silly non-subset test.
	vdmFree(set1);
	vdmFree(set2);
	set1 = newSetVar(1, newInt(1));
	set2 = newSetVar(1, newInt(2));
	res = vdmSetSubset(set1, set2);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(res);

	//Wrap up.
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
}



TEST(Expression_Set, setProperSubset)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(i);
	}

	//Create test set.
	set1 = newSetWithValues(numelems1, randelems1);

	//Set must not be a proper subset of itself.
	res = vdmSetProperSubset(set1, set1);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(res);

	//Get corresponding collection of test set without duplicates.
	UNWRAP_COLLECTION(setnodupscol, set1);
	//Make this into a new but smaller set.
	set2 = newSetWithValues(setnodupscol->size - 1, setnodupscol->value);
	res = vdmSetProperSubset(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//And a silly non-subset test.
	vdmFree(set1);
	vdmFree(set2);
	set1 = newSetVar(1, newInt(1));
	set2 = newSetVar(1, newInt(2), newInt(3));
	res = vdmSetProperSubset(set1, set2);
	EXPECT_EQ(false, res->value.boolVal);

	//Wrap up.
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(res);
}



TEST(Expression_Set, setEquality)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(i);
	}

	//Create test set.
	set1 = newSetWithValues(numelems1, randelems1);
	set2 = newSetWithValues(numelems1, randelems1);

	//Sets must be equal.
	res = vdmSetEquals(set1, set2);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//And a silly inequality test.
	vdmFree(set1);
	vdmFree(set2);
	set1 = newSetVar(1, newInt(1));
	set2 = newSetVar(1, newInt(2));
	res = vdmSetEquals(set1, set2);
	EXPECT_EQ(false, res->value.boolVal);

	//Wrap up.
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(res);
}



TEST(Expression_Set, setInequality)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(i);
	}

	//Create test set.
	set1 = newSetWithValues(numelems1, randelems1);
	set2 = newSetWithValues(numelems1, randelems1);

	//Sets must be equal.
	res = vdmSetNotEquals(set1, set2);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(res);

	//And a silly inequality test.
	vdmFree(set1);
	vdmFree(set2);
	set1 = newSetVar(1, newInt(1));
	set2 = newSetVar(1, newInt(2));
	res = vdmSetNotEquals(set1, set2);
	EXPECT_EQ(true, res->value.boolVal);

	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(res);
}



TEST(Expression_Set, setCard)
{
	const int numelems = 5;
	TVP elems[numelems] = {newInt(1), newInt(2), newInt(2), newInt(3), newInt(11)};
	TVP theset;

	//Cardinality of empty set.
	theset = newSetWithValues(0, NULL);
	EXPECT_EQ(0, (vdmSetCard(theset))->value.intVal);
	vdmFree(theset);

	//Cardinality of non-empty set.
	theset = newSetWithValues(numelems, elems);
	EXPECT_EQ(numelems - 1, (vdmSetCard(theset))->value.intVal);

	//Wrap up.
	for(int i = 0; i < numelems; i++)
	{
		vdmFree(elems[i]);
	}
	vdmFree(theset);
}



TEST(Expression_Set, setDunion)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP set3;
	TVP set4;
	TVP set5;
	TVP res;

	//Initialize main set of sets.
	set3 = newSetVar(0, NULL);

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(i);

		//A set containing one singleton set.
		set1 = newSetVar(1, newSetVar(1, randelems1[i]));
		set2 = vdmSetUnion(set3, set1);
		vdmFree(set3);
		set3 = set2;
		vdmFree(set1);
	}

	set1 = vdmSetDunion(set3);
	vdmFree(set3);

	//Check that every element of the original singletons are in the union set.
	for(int i = 0; i < numelems1; i++)
	{
		res = vdmSetMemberOf(set1, randelems1[i]);
		EXPECT_EQ(true, res->value.boolVal);
		vdmFree(res);
	}

	//Similar check but with non-singleton initial sets.
	vdmFree(set1);
	set1 = newSetVar(2, newInt(1), newInt(2));
	set2 = newSetVar(3, newInt(3), newInt(4), newInt(5));
	set3 = newSetVar(1, set1);
	set4 = newSetVar(1, set2);
	set5 = vdmSetUnion(set3, set4);
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(set3);
	vdmFree(set4);
	set1 = vdmSetDunion(set5);
	vdmFree(set5);

	//Check that the original sets are subsets of the distributed union set.
	set2 = newSetVar(2, newInt(1), newInt(2));
	set3 = newSetVar(3, newInt(3), newInt(4), newInt(5));
	res = vdmSetSubset(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);
	res = vdmSetSubset(set3, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);

	//Wrap up.
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(set3);
}



TEST(Expression_Set, setDinter)
{
	TVP set1;
	TVP set2;
	TVP set3;
	TVP set4;
	TVP set5;
	TVP res;

	set1 = newSetVar(3, newInt(1), newInt(2), newInt(3));
	set2 = newSetVar(3, newInt(2), newInt(3), newInt(4));
	set3 = newSetVar(1, set1);
	set4 = newSetVar(1, set2);
	set5 = vdmSetUnion(set3, set4);
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(set3);
	vdmFree(set4);
	set1 = vdmSetDinter(set5);
	vdmFree(set5);

	//Check that intersecting the distributed intersection with each of the original sets results in the distributed intersection itself.
	set2 = newSetVar(3, newInt(1), newInt(2), newInt(3));
	set3 = newSetVar(3, newInt(2), newInt(3), newInt(4));
	set4 = vdmSetInter(set1, set2);
	set5 = vdmSetInter(set1, set3);

	res = vdmSetEquals(set1, set4);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);
	res = vdmSetEquals(set1, set5);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(res);
	vdmFree(set2);
	vdmFree(set3);
	vdmFree(set4);
	vdmFree(set5);

	//A negative check by modifying one of the original sets.
	set2 = newSetVar(3, newInt(1), newInt(6), newInt(3));
	set3 = vdmSetInter(set1, set2);

	res = vdmSetEquals(set1, set3);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(res);

	//Wrap up.
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(set3);
}



TEST(Expression_Set, setPower)
{
	TVP set1;
	TVP set2;
	TVP res;

	set1 = newSetVar(4, newInt(1), newInt(2), newInt(3), newInt(4));
	set2 = vdmSetPower(set1);

	UNWRAP_COLLECTION(col, set2);

	//Set should have the right size.
	EXPECT_EQ(true, col->size == pow(2, 4));
	EXPECT_EQ(false, col->size == pow(2, 5));

	//Power set should contain the subsets.  Only testing a few here.
	vdmFree(set1);

	//Should definitely contain the empty set and the original.
	set1 = newSetVar(0, NULL);
	res = vdmSetMemberOf(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(set1);
	vdmFree(res);

	set1 = newSetVar(4, newInt(1), newInt(2), newInt(3), newInt(4));
	res = vdmSetMemberOf(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(set1);
	vdmFree(res);

	//And some others.
	set1 = newSetVar(1, newInt(3));
	res = vdmSetMemberOf(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(set1);
	vdmFree(res);

	set1 = newSetVar(2, newInt(2), newInt(3));
	res = vdmSetMemberOf(set2, set1);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(set1);
	vdmFree(res);

	set1 = newSetVar(2, newInt(2), newInt(5));
	res = vdmSetMemberOf(set2, set1);
	EXPECT_EQ(false, res->value.boolVal);

	//Wrap up.
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(res);
}



/*
//A crude way of testing how long it takes to generate a power set.
TEST(Expression_SetRandom, setPowerSpeedTest)
{
	const int numelems1 = 11;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP set3;
	TVP res;

	//Initialize main set of sets.
	set3 = newSetVar(0, NULL);

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(rand());

		//A set containing one element.
		set1 = newSetVar(1, randelems1[i]);
		set2 = vdmSetUnion(set3, set1);
		vdmFree(set3);
		set3 = set2;
		vdmFree(set1);
	}
	//Wrap up.
	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}

	set2 = vdmSetPower(set3);

	//Wrap up.
	vdmFree(set2);
	vdmFree(set3);
}



//A crude way to look for memory leaks using the OS resource monitor.
TEST(Expression_SetRandom, setMemTest)
{
	const int numelems1 = 1000;
	const int numelems2 = 1000;
	TVP randelems1[numelems1];
	TVP randelems2[numelems2];
	TVP set1;
	TVP set2;
	TVP interset;

	//Generate the random test value collections.
	srand(time(0));

	for(int numiter = 0; numiter < 5000; numiter++)
	{
		for(int i = 0; i < numelems1; i++)
		{
			randelems1[i] = newInt(rand());
		}

		for(int i = 0; i < numelems2; i++)
		{
			randelems2[i] = newInt(rand());
		}

		//Create the random test sets.
		set1 = newSetWithValues(numelems1, randelems1);
		set2 = newSetWithValues(numelems2, randelems2);
		interset = vdmSetInter(set1, set2);

		vdmFree(set1);
		vdmFree(set2);
		vdmFree(interset);
		for(int i = 0; i < numelems1; i++)
		{
			vdmFree(randelems1[i]);
		}
		for(int i = 0; i < numelems2; i++)
		{
			vdmFree(randelems2[i]);
		}
	}
}
 */
