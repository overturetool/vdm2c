/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionSetForCGTesting.h"

}

#define CHECK(methodId) TVP c=ExpressionSetForCGTesting._new();\
TVP res=CALL_FUNC(ExpressionSetForCGTesting,ExpressionSetForCGTesting,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Expression_Set, setInSet)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z8setInsetEV);
}

TEST(Expression_Set, setNotInSet)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z11setNotInSetEV);
}

TEST(Expression_Set, setUnion)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z8setUnionEV);
}

TEST(Expression_Set, setInterection)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z15setIntersectionEV);
}

TEST(Expression_Set, setDifference)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z13setDifferenceEV);
}

TEST(Expression_Set, setSubset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setSubsetEV);
}

TEST(Expression_Set, setProperSubset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z15setProperSubsetEV);
}

TEST(Expression_Set, setEquality)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z11setEqualityEV);
}

TEST(Expression_Set, setInequality)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z13setInequalityEV);
}

TEST(Expression_Set, setCardinality)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z7setCardEV);
}

TEST(Expression_Set, setDunion)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setDunionEV);
}

TEST(Expression_Set, setDinter)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setDinterEV);
}

TEST(Expression_Set, setPower)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z8setPowerEV);
}
