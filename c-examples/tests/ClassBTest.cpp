/*
 * ClassATest.cpp
 *
 *  Created on: Jan 7, 2016
 *      Author: kel
 */

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

/**
 * Tests
 */

TEST(ClassBTest, _new)
{
	TVP c=B._new();

	vdmFree(c);
}

TEST(ClassBTest, calc)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation calc",5,CALL_FUNC(B, A, c, CLASS_A_calc, a, b)
	);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}

TEST(ClassBTest, sum)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

//	checkFreeDouble("calculation sum",9,CALL_FUNC(B, A, c, CLASS_A_sum)	);//TODO overload test. This test is working but not supported by generator yet

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}

TEST(ClassBTest, field1)
{
	TVP c=B._new();

	TVP a = newInt(9);
	SET_FIELD(B,A,c,field1,a);
	vdmFree(a);

	checkFreeInt("check field1 of A",9, GET_FIELD(B,A,c,field1));

	vdmFree(c);
}




TEST(ClassBTest, field1c)
{
	TVP c=B._new();

	TVP a = newReal(99.99);
	SET_FIELD(B,C,c,field1c,a);
	vdmFree(a);

	checkFreeDouble("check field1c of C",99.99, GET_FIELD(B,C,c,field1c));

	vdmFree(c);
}

}
