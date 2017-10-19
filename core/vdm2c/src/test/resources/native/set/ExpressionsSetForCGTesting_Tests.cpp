/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */

#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionSetForCGTesting.h"

}

#define CHECK(methodId) TVP c=_Z25ExpressionSetForCGTestingEV(NULL);\
TVP res=CALL_FUNC(ExpressionSetForCGTesting,ExpressionSetForCGTesting,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, setRange)
{
CHECK(CLASS_ExpressionSetForCGTesting__Z8setRangeEV);
}

TEST_F(TestFlowFunctions, inset)
{
CHECK(CLASS_ExpressionSetForCGTesting__Z8setInsetEV);
}

TEST_F(TestFlowFunctions, notinset)
{
    CHECK(CLASS_ExpressionSetForCGTesting__Z11setNotInSetEV);
}

TEST_F(TestFlowFunctions, setunion)
{
    CHECK(CLASS_ExpressionSetForCGTesting__Z8setUnionEV);
}

TEST_F(TestFlowFunctions, setintersection)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z15setIntersectionEV);
}

TEST_F(TestFlowFunctions, setdifference)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z13setDifferenceEV);
}

TEST_F(TestFlowFunctions, subset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setSubsetEV);
}

TEST_F(TestFlowFunctions,propersubset )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z15setProperSubsetEV);
}

TEST_F(TestFlowFunctions, setequality)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z11setEqualityEV);
}

TEST_F(TestFlowFunctions,setinequality )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z13setInequalityEV);
}

TEST_F(TestFlowFunctions,card )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z7setCardEV);
}

TEST_F(TestFlowFunctions,dunion )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setDunionEV);
}

TEST_F(TestFlowFunctions,dinter )
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z9setDinterEV);
}

TEST_F(TestFlowFunctions, powerset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z8setPowerEV);
}

TEST_F(TestFlowFunctions, iotaset)
{
	CHECK(CLASS_ExpressionSetForCGTesting__Z7setIotaEV);
}
