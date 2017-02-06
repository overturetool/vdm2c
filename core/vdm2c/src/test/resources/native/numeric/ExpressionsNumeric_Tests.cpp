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
#include "ExpressionNumeric.h"

}

#define CHECK(methodId) TVP c=_Z17ExpressionNumericEV(NULL);\
TVP res=CALL_FUNC(ExpressionNumeric,ExpressionNumeric,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, abs)
{
	CHECK(CLASS_ExpressionNumeric__Z6absExpEV);
}

TEST_F(TestFlowFunctions, floor)
{
	CHECK(CLASS_ExpressionNumeric__Z8floorExpEV);
}

TEST_F(TestFlowFunctions, sum)
{
	CHECK(CLASS_ExpressionNumeric__Z6sumExpEV);
}

TEST_F(TestFlowFunctions, difference)
{
	CHECK(CLASS_ExpressionNumeric__Z13differenceExpEV);
}

TEST_F(TestFlowFunctions, product)
{
	CHECK(CLASS_ExpressionNumeric__Z10productExpEV);
}

TEST_F(TestFlowFunctions, division)
{
	CHECK(CLASS_ExpressionNumeric__Z11divisionExpEV);
}

TEST_F(TestFlowFunctions,intDivision )
{
	CHECK(CLASS_ExpressionNumeric__Z14intDivisionExpEV);
}
TEST_F(TestFlowFunctions, remainder)
{
	CHECK(CLASS_ExpressionNumeric__Z12remainderExpEV);
}
TEST_F(TestFlowFunctions, mod)
{
	CHECK(CLASS_ExpressionNumeric__Z6modExpEV);
}
TEST_F(TestFlowFunctions, power)
{
	CHECK(CLASS_ExpressionNumeric__Z8powerExpEV);
}
TEST_F(TestFlowFunctions,lessthan )
{
	CHECK(CLASS_ExpressionNumeric__Z11lessthanExpEV);
}
TEST_F(TestFlowFunctions,greaterThan )
{
	CHECK(CLASS_ExpressionNumeric__Z14greaterThanExpEV);
}
TEST_F(TestFlowFunctions, lessEqual)
{
	CHECK(CLASS_ExpressionNumeric__Z13lessEqualExp1EV);
}
TEST_F(TestFlowFunctions,greaterEqual )
{
	CHECK(CLASS_ExpressionNumeric__Z16greaterEqualExp1EV);
}
TEST_F(TestFlowFunctions, equal)
{
	CHECK(CLASS_ExpressionNumeric__Z8equalExpEV);
}
TEST_F(TestFlowFunctions,notEqual )
{
	CHECK(CLASS_ExpressionNumeric__Z11notEqualExpEV);
}

TEST_F(TestFlowFunctions,unaryPlus )
{
	CHECK(CLASS_ExpressionNumeric__Z9unaryPlusEV);
}

TEST_F(TestFlowFunctions,unaryMinus )
{
	CHECK(CLASS_ExpressionNumeric__Z10unaryMinusEV);
}
