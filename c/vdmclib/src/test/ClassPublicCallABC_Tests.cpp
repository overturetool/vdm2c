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
#include "Vdm.h"
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

TEST(A, _new)
{
	TVP c=A._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeInt("calculation calc",5,CALL_FUNC(A,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",4,CALL_FUNC(A,A,c,CLASS_A_sum));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}

TEST(B, _new)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeInt("calculation B->A CLASS_A_calc",5,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation B->A CLASS_A_sum",9, CALL_FUNC( B,A,c,CLASS_A_sum));

	checkFreeInt("calculation B->B CLASS_B_sum2",9, CALL_FUNC(B, B,c,CLASS_B_sum2));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

TEST(B, _newAsA)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeInt("calculation B->A CLASS_A_calc",5,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation B->A CLASS_A_sum",9,CALL_FUNC(B,A,c,CLASS_A_sum));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}


TEST(B, _newAsC)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

//	checkFreeDouble("calculation sum",17.34,CALL_FUNC(B,C,c,CLASS_C_calc,a,b));

	checkFreeDouble("calculation field1c",12.34, CALL_FUNC( B,C,c,CLASS_C_getField1));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

TEST(C, _new)
{
	TVP c=C._new();

	checkFreeDouble("getfield1c",12.34, CALL_FUNC( C,C,c,CLASS_C_getField1));

	TVP f1 = CALL_FUNC(C,C,c,CLASS_C_getField1);
//	printf("field one with macro is: %f\n",f1->value.doubleVal);

//	UNWRAP_CLASS_C(l,c);
	EXPECT_EQ (12.34,f1->value.doubleVal);

	vdmFree(c);

}

}
