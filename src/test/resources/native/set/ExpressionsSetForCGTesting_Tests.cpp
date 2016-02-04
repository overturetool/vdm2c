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

//#define CLASS_ExpressionSetForCGTesting__Z13setDifferenceEV 4
//#define CLASS_ExpressionSetForCGTesting__Z9setSubsetEV 5
//#define CLASS_ExpressionSetForCGTesting__Z15setProperSubsetEV 6
//#define CLASS_ExpressionSetForCGTesting__Z11setEqualityEV 7
//#define CLASS_ExpressionSetForCGTesting__Z13setInequalityEV 8
//#define CLASS_ExpressionSetForCGTesting__Z7setCardEV 9
//#define CLASS_ExpressionSetForCGTesting__Z9setDunionEV 10
//#define CLASS_ExpressionSetForCGTesting__Z9setDinterEV 11
//#define CLASS_ExpressionSetForCGTesting__Z8setPowerEV 12

TEST(ExpressionSetForCGTesting, inset)
{
CHECK(CLASS_ExpressionSetForCGTesting__Z8setInsetEV);
}

TEST(ExpressionSetForCGTesting, notinset)
{
    CHECK(CLASS_ExpressionSetForCGTesting__Z11setNotInSetEV);
}

TEST(ExpressionSetForCGTesting, setunion)
{
    CHECK(CLASS_ExpressionSetForCGTesting__Z8setUnionEV);
}

TEST(ExpressionSetForCGTesting, setintersection)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z15setIntersectionEV);
}

TEST(ExpressionSetForCGTesting, setdifference)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z13setDifferenceEV);
}

TEST(ExpressionSetForCGTesting, subset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setSubsetEV);
}

TEST(ExpressionSetForCGTesting,propersubset )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z15setProperSubsetEV);
}

TEST(ExpressionSetForCGTesting, setequality)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z11setEqualityEV);
}

TEST(ExpressionSetForCGTesting,setinequality )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z13setInequalityEV);
}

TEST(ExpressionSetForCGTesting,card )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z7setCardEV);
}

TEST(ExpressionSetForCGTesting,dunion )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setDunionEV);
}

TEST(ExpressionSetForCGTesting,dinter )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setDinterEV);
}

TEST(ExpressionSetForCGTesting, powerset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z8setPowerEV);
}
