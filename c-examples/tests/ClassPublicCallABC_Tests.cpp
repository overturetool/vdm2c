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

TEST(A, _new)
{
	TVP c=A._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation calc",5,CALL_FUNC(A,A,c,CLASS_A_calc,a,b));

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

	checkFreeDouble("calculation sum",4,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",9, CALL_FUNC( B,A,c,CLASS_A_sum));

	checkFreeInt("calculation sum2",5, CALL_FUNC(B, B,c,CLASS_B_sum2));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

TEST(B, _newAsA)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation sum",4,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",9,CALL_FUNC(B,A,c,CLASS_A_sum));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}


TEST(B, _newAsC)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation sum",17.34,CALL_FUNC(B,C,c,CLASS_C_calc,a,b));

	checkFreeDouble("calculation field1c",12.34, CALL_FUNC( B,C,c,CLASS_C_getField1));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

TEST(C, _new)
{
	TVP c=C._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calc c as C",17.34, CALL_FUNC( C,C,c,CLASS_C_calc,a,b));

	checkFreeDouble("getfield1c",12.34, CALL_FUNC( C,C,c,CLASS_C_getField1));

	TVP f1 = CALL_FUNC(C,C,c,CLASS_C_getField1);
	printf("field one with macro is: %f\n",f1->value.doubleVal);

	UNWRAP_CLASS_C(l,c);
	EXPECT_EQ (12.34,l->m_C_field1c->value.doubleVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

}
