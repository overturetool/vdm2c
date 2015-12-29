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
TEST(ClassPrivateCallABC, A_sum)
{
	TVP c=A._new();
	UNWRAP_CLASS_A(l, c);
	ACLASS this_ptr = l;

	//in class

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation calc",5,CALL_FUNC_PTR(A,A,this_ptr,CLASS_A_calc,a,b));
	checkFreeInt("calculation sum",4,CALL_FUNC_PTR(A,A,this_ptr,CLASS_A_sum));

	vdmFree(a);
	vdmFree(b);

	//out class

	vdmFree(c);
}

TEST(ClassPrivateCallABC, B)
{
	TVP c=B._new();
	UNWRAP_CLASS_B(l, c);
	BCLASS this_ptr = l;

	//in class
	TVP a = newInt(1);
	TVP b = newInt(4);

	//on A
	checkFreeDouble("calculation B->A CLASS_A_calc",5,CALL_FUNC_PTR(B,A,this_ptr,CLASS_A_calc,a,b));
	checkFreeDouble("calculation B->A CLASS_A_sum",9, CALL_FUNC_PTR( B,A,this_ptr,CLASS_A_sum));

	//on C
//	checkFreeDouble("calculation calc",4,CALL_FUNC_PTR(B,A,this_ptr,CLASS_C_calc,a,b));
	checkFreeDouble("calculation B->C CLASS_C_getField1",12.34,CALL_FUNC_PTR(B,C,this_ptr,CLASS_C_getField1));

	//on B
	checkFreeDouble("calculation B->B CLASS_B_sum2",9, CALL_FUNC_PTR(B, B,this_ptr,CLASS_B_sum2));

	vdmFree(a);
	vdmFree(b);
	//out class

	vdmFree(c);
}
}
