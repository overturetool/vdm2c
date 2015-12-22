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
	UNWRAP_CLASS_A(l, c);
	ACLASS this_ptr = l;

	TVP res = NULL;
	//in class

	res = GET_FIELD_PTR(A,A,this_ptr,field1);

	//out class

	EXPECT_EQ (4,res->value.intVal);

	vdmFree(res);
	vdmFree(c);
}

TEST(B, B_sum)
{
	TVP c=B._new();
	UNWRAP_CLASS_B(l, c);
	BCLASS this_ptr = l;

	TVP res = NULL;
	//in class

	TVP tmp1 = GET_FIELD_PTR(B,A,this_ptr,field1);
	EXPECT_EQ (4,tmp1->value.intVal);

	TVP tmp2 = GET_FIELD_PTR(B,B,this_ptr,field2);
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

TEST(A, _new)
{
	TVP c=A._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation calc",5,CALL_FUNC(A,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",4,CALL_FUNC(A,A,c,CLASS_A_sum));

	UNWRAP_CLASS_A(l, c);
	EXPECT_EQ (4,l->m_A_field1->value.intVal);

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

	UNWRAP_CLASS_B(l,c);
	EXPECT_EQ (4,l->m_A_field1->value.intVal);

	checkFreeInt("calculation sum2",5, CALL_FUNC(B, B,c,CLASS_B_sum2));

	EXPECT_EQ (5,l->m_B_field2->value.intVal);

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

TEST(B, fieldTestAsA)
{
	TVP c=B._new();

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeInt("check field1 of B as A",4, GET_FIELD(B,A,c,field1));

	checkFreeDouble("check field1c of B as C",12.34, GET_FIELD(B,C,c,field1c));

	checkFreeInt("check field2 of B as B",5, GET_FIELD(B,B,c,field2));

//	checkFreeDouble("calculation sum",4,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

//	checkFreeInt("calculation sum",9,CALL_FUNC(B,A,c,CLASS_A_sum));

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
