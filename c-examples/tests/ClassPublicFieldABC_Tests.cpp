#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"
#include <stdio.h>
}

namespace
{
/**
 * Utility methods
 */
void checkFreeInt(const char* name, int expected, TVP value)
{
	printf("%s is %d\n",name,value->value.intVal);
	EXPECT_EQ (expected,value->value.intVal);
	vdmFree(value);
}

void checkFreeDouble(const char* name, double expected, TVP value)
{
	printf("%s is %f\n",name,value->value.doubleVal);
	EXPECT_EQ (expected,value->value.doubleVal);
	vdmFree(value);
}

/*
 * Private stuff
 */
TEST(A, A_sum)
{
	TVP c=A._new();

	TVP res = NULL;
	//in class

	res = GET_FIELD(A,A,c,field1);

	//out class

	EXPECT_EQ (4,res->value.intVal);

	vdmFree(res);
	vdmFree(c);
}

TEST(B, B_sum)
{
	TVP c=B._new();

	TVP res = NULL;
	//in class

	TVP tmp1 = GET_FIELD(B,A,c,field1);
	EXPECT_EQ (4,tmp1->value.intVal);

	TVP tmp2 = GET_FIELD(B,B,c,field2);
	EXPECT_EQ (5,tmp2->value.intVal);

	res = vdmSum(tmp1,tmp2);

	vdmFree(tmp1);
	vdmFree(tmp2);

	//return newInt(base->m_A_field1->value.intVal+this->m_B_field2->value.intVal);

	//out class

	EXPECT_EQ (9,res->value.doubleVal);

	vdmFree(res);
	vdmFree(c);
}

/**
 * Tests
 */
TEST(B, fieldTestAsA)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeInt("check field1 of B as A",4, GET_FIELD(B,A,c,field1));

	checkFreeDouble("check field1c of B as C",12.34, GET_FIELD(B,C,c,field1c));

	checkFreeInt("check field2 of B as B",5, GET_FIELD(B,B,c,field2));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

TEST(B, testBFieldSet)
{
	TVP c=B._new();

	TVP a = newInt(9);
	SET_FIELD(B,A,c,field1,a);
	vdmFree(a);

	checkFreeInt("check field1 of B as A",9, GET_FIELD(B,A,c,field1));

	a = newInt(0);
	SET_FIELD(B,A,c,field1,a);
	vdmFree(a);

	checkFreeInt("check field1 of B as A",0, GET_FIELD(B,A,c,field1));

	a = newInt(99);
	SET_FIELD(B,B,c,field2,a);
	vdmFree(a);

	checkFreeInt("check field2 of B as B",99, GET_FIELD(B,B,c,field2));

	a = newReal(99.99);
	SET_FIELD(B,C,c,field1c,a);
	vdmFree(a);

	checkFreeDouble("check field1c of B as C",99.99, GET_FIELD(B,C,c,field1c));

	vdmFree(c);

}

}
