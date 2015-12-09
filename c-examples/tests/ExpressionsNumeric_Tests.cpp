#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmBasicTypes.h"
#include <stdio.h>
}


TEST(Expression_Numeric, minusExp)
{
	TVP t = newInt(5);

	assert(t->type == VDM_INT && "Value is not a integer");
	TVP res = vdmMinus(t);
	EXPECT_EQ(-5,res->value.intVal);

	vdmFree(t);
	vdmFree(res);
}

TEST(Expression_Numeric, absExp)
{
	TVP t = newInt(-5);

	assert(t->type == VDM_INT && "Value is not a integer");
	TVP res = vdmMinus(t);
	EXPECT_EQ(5,res->value.intVal);

	vdmFree(t);
	vdmFree(res);
}

TEST(Expression_Numeric, sumExp)
{
	TVP t1 = newInt(5);
	TVP t2 = newInt(4);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmSum(t1,t2);
	EXPECT_EQ(9,res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, differenceExp)
{
	TVP t1 = newInt(5);
	TVP t2 = newInt(4);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmDifference(t1,t2);
	EXPECT_EQ(1,res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}
