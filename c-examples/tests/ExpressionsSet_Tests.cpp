#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmSet.h"
#include "lib/VdmBasicTypes.h"
#include <stdio.h>
}

//TODO use correct set construction
TVP newSet(int size, int* arr)
{
	TVP seq = newSet(size);

	struct Collection* col =(struct Collection*) seq->value.ptr;

	for (int i = 0; i < size; i++)
	{
		col->value[i] = newInt(arr[i]);
	}

	return seq;
}

TEST(Expression_Set, setInset)
{
	TVP el = newInt(1);
	TVP t = newSetVar(1,el);

	TVP res = vdmSetMemberOf(t,el);

	EXPECT_EQ(true, res->value.boolVal);

	vdmFree(res);
//
	vdmFree(t);
	vdmFree(el);
}

//#define COMPREHENSION(exp,var,S,P)

#define DEFAULT_SET_COMP_BUFFER 1
#define DEFAULT_SET_COMP_BUFFER_STEPSIZE 10

TEST(Expression_Set, setComprehension)
{
	int arr[] =
	{ 1, 1, 2, 8, 15 };
	TVP S = newSet(5,arr); //Technically this isn't a set but we need this for testing
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
		EXPECT_EQ(1, buf[1]->value.intVal);
		EXPECT_EQ(2, buf[2]->value.intVal);
		EXPECT_EQ(8, buf[3]->value.intVal);

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

TEST(Expression_Set, setAdd)
{
	TVP e1 = newInt(2);
	TVP t = newSet(1);
	TVP res;
	int index = 1;

	vdmSetAdd(&t, &index, e1);

	//We are looking for the same integer value.
	vdmFree(e1);
	e1 = newInt(2);
	res = vdmSetMemberOf(t, e1);
	//EXPECT_EQ(true, res->value.boolVal);

	vdmFree(res);
	vdmFree(t);
	vdmFree(e1);
}
