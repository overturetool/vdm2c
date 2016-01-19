/*
 * ClassATest.cpp
 *
 *  Created on: Jan 7, 2016
 *      Author: kel
 */

#include "gtest/gtest.h"

extern "C"
{
#include "lib/Vdm.h"
#include "A.h"


#include <stdio.h>

#ifdef VDM_CG
#include "MethodNameMap.h"
#endif
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

TEST(ClassATest, _new)
{
	TVP c=A._new();

	vdmFree(c);
}

TEST(ClassATest, calc)
{
	TVP c=A._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation calc",5,CALL_FUNC(A, A, c, CLASS_A_calc, a, b)
	);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}

TEST(ClassATest, sum)
{
	TVP c=A._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeInt("calculation sum",4,CALL_FUNC(A, A, c, CLASS_A_sum)
	);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}

TEST(ClassATest, field1)
{
	TVP c=A._new();

	TVP a = newInt(9);
	SET_FIELD(A,A,c,field1,a);
	vdmFree(a);

	checkFreeInt("check field1 of A",9, GET_FIELD(A,A,c,field1));

	vdmFree(c);
}

}
