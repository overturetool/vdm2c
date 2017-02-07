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
#include "ExpressionLetBeSt.h"

}

#define CHECK(methodId) TVP c=_Z17ExpressionLetBeStEV(NULL);\
TVP res=CALL_FUNC(ExpressionLetBeSt,ExpressionLetBeSt,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, expPred)
{
	CHECK(CLASS_ExpressionLetBeSt__Z7expPredEV);
}

TEST_F(TestFlowFunctions, expNoPred)
{
	CHECK(CLASS_ExpressionLetBeSt__Z9expNoPredEV);
}

TEST_F(TestFlowFunctions, stmPredReturn)
{
	CHECK(CLASS_ExpressionLetBeSt__Z13stmPredReturnEV);
}

TEST_F(TestFlowFunctions, tmNoPredNestedLet)
{
	CHECK(CLASS_ExpressionLetBeSt__Z18stmNoPredNestedLetEV);
}


