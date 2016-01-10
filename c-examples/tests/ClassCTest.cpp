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

TEST(ClassCTest, _new)
{
	TVP c=C._new();

	vdmFree(c);
}



TEST(ClassCTest, field1c)
{
	TVP c=A._new();

	TVP a = newReal(99.99);
	SET_FIELD(C,C,c,field1c,a);
	vdmFree(a);

	checkFreeDouble("check field1c of C",99.99, GET_FIELD(C,C,c,field1c));

	vdmFree(c);
}

}
