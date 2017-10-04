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


/*
 * ClassATest.cpp
 *
 *  Created on: Jan 7, 2016
 *      Author: kel
 */

#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include "classes/A.h"


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

	checkFreeInt("calculation calc",5,CALL_FUNC(A, A, c, CLASS_A_calc, a, b)
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

TEST(ClassATest, isA)
{
	TVP c = A._new();
	char ot[] = {'W', CLASS_ID_A_ID};

	TVP res = isOfClass(c, CLASS_ID_A_ID);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = isOfClass(c, CLASS_ID_A_ID + 1);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	res = is(c, ot);
	EXPECT_TRUE(res->value.boolVal);

	vdmFree(c);
}

TEST(ClassATest, sameClass)
{
	TVP c = A._new();
	TVP res;

	res = sameClass(c, c);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

}
