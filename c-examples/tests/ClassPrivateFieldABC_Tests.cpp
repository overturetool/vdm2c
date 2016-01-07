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

/*
 * Private stuff
 */
TEST(ClassPrivateFieldABC, A_sum)
{
	TVP c=A._new();
	ACLASS this_ptr = TO_CLASS_PTR(c,A);

	TVP res = NULL;
	//in class

	res = GET_FIELD_PTR(A,A,this_ptr,field1);

	//out class

	EXPECT_EQ (4,res->value.intVal);

	vdmFree(res);
	vdmFree(c);
}

TEST(ClassPrivateFieldABC, B_sum)
{
	TVP c=B._new();
	BCLASS this_ptr = TO_CLASS_PTR(c,B);;

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

}
