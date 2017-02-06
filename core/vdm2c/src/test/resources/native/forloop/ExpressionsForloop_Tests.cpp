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
#include "ExpressionForLoop.h"

}

#define CHECK(methodId) TVP c=_Z17ExpressionForLoopEV(NULL);\
TVP res=CALL_FUNC(ExpressionForLoop,ExpressionForLoop,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, forindex)
{
	CHECK(CLASS_ExpressionForLoop__Z8forindexEV);
}

TEST_F(TestFlowFunctions, forset)
{
	CHECK(CLASS_ExpressionForLoop__Z6forsetEV);
}

TEST_F(TestFlowFunctions, forseq)
{
	CHECK(CLASS_ExpressionForLoop__Z6forseqEV);
}

TEST_F(TestFlowFunctions, testSumOneToFive)
{
	CHECK(CLASS_ExpressionForLoop__Z16testSumOneToFiveEV);
}