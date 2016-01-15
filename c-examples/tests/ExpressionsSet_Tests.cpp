#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmSet.h"
#include "lib/VdmBasicTypes.h"
#include <stdio.h>

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

	vdmFree(S);
	vdmFree(t);
}



TEST(Expression_Set, setInSet)
{
	const int numelems = 10;
	TVP elem = newInt(INT_MAX);
	TVP set = newSetVar(1, elem);
	struct TypedValue *randelems[100], *randelemscpy[100];

	//Make sure that we're checking for the value itself and not the newInt structure.
	vdmFree(elem);
	elem = newInt(INT_MAX);

	TVP res = vdmSetMemberOf(set, elem);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(elem);

	for(int i = 0; i < numelems; i++)
	{
		randelems[i] = newInt(rand());
	}

	vdmFree(set);
	set = newSetWithValues(numelems, randelems);

	for(int i = 0; i < numelems; i++)
	{
		randelemscpy[i] = vdmClone(randelems[i]);
	}

	for(int i = 0; i < numelems; i++)
	{
		free(res);
		res = vdmSetMemberOf(set, randelemscpy[i]);
		EXPECT_EQ(true, res->value.boolVal);
	}

	vdmFree(res);
	vdmFree(set);

	for(int i = 0; i < numelems; i++)
	{
		free(randelems[i]);
		free(randelemscpy[i]);
	}
}



TEST(Expression_Set, setNotInSet)
{
	const int numelems = 10;
	TVP elem = newInt(INT_MAX);
	TVP set = newSetVar(1, elem);
	struct TypedValue *randelems[100], *randelemscpy[100];

	//Make sure that we're checking for the value itself and not the newInt structure.
	vdmFree(elem);
	elem = newInt(INT_MAX);

	TVP res = vdmSetNotMemberOf(set, elem);
	EXPECT_EQ(false, res->value.boolVal);
	vdmFree(elem);
	vdmFree(res);

	elem = newInt(INT_MAX - 1);
	res = vdmSetNotMemberOf(set, elem);
	EXPECT_EQ(true, res->value.boolVal);
	vdmFree(elem);

	for(int i = 0; i < numelems; i++)
	{
		randelems[i] = newInt(rand());
	}

	vdmFree(set);
	set = newSetWithValues(numelems, randelems);

	for(int i = 0; i < numelems; i++)
	{
		randelemscpy[i] = vdmClone(randelems[i]);
	}

	for(int i = 0; i < numelems; i++)
	{
		free(res);
		res = vdmSetNotMemberOf(set, randelemscpy[i]);
		EXPECT_EQ(false, res->value.boolVal);
	}

	vdmFree(res);
	vdmFree(set);

	for(int i = 0; i < numelems; i++)
	{
		free(randelems[i]);
		free(randelemscpy[i]);
	}
}



TEST(Expression_Set, setCard)
{
	const int numelems = 5;
	TVP elems[numelems] = {newInt(1), newInt(2), newInt(2), newInt(3), newInt(INT_MAX)};
	TVP theset;

	//Cardinality of empty set.
	theset = newSetWithValues(0, NULL);
	EXPECT_EQ(0, (vdmSetCard(theset))->value.intVal);
	vdmFree(theset);

	//Cardinality of non-empty set.
	theset = newSetWithValues(numelems, elems);
	EXPECT_EQ(numelems - 1, (vdmSetCard(theset))->value.intVal);
	for(int i = 0; i < numelems; i++)
	{
		free(elems[i]);
	}
	vdmFree(theset);
}



TEST(Expression_set, setUnion)
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
		randelems1[i] = newInt(rand());
	}

	for(int i = 0; i < numelems2; i++)
	{
		randelems2[i] = newInt(rand());
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
		free(res);
	}
	for(int i = 0; i < numelems2; i++)
	{
		res = vdmSetMemberOf(unionset, randelems2[i]);
		EXPECT_EQ(true, res->value.boolVal);
		free(res);
	}

	//Ensure that there are no other elements.
	EXPECT_EQ(true, ((struct Collection*)(unionset->value.ptr))->size == \
				((struct Collection*)(set1->value.ptr))->size + \
				((struct Collection*)(set2->value.ptr))->size);

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


TEST(Expression_set, setSubset)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(rand());
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

	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
}



TEST(Expression_set, setProperSubset)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(rand());
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

	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(res);
}



TEST(Expression_set, setEquality)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(rand());
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

	for(int i = 0; i < numelems1; i++)
	{
		vdmFree(randelems1[i]);
	}
	vdmFree(set1);
	vdmFree(set2);
	vdmFree(res);
}



TEST(Expression_set, setInequality)
{
	const int numelems1 = 101;
	TVP randelems1[numelems1];
	TVP set1;
	TVP set2;
	TVP res;

	//Generate the random test value collections.
	for(int i = 0; i < numelems1; i++)
	{
		randelems1[i] = newInt(rand());
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
