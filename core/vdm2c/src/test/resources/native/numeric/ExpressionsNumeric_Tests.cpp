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
#include "ExpressionNumeric.h"

}

#define CHECK(methodId) TVP c=_Z17ExpressionNumericEV(NULL);\
TVP res=CALL_FUNC(ExpressionNumeric,ExpressionNumeric,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(ExpressionNumeric, abs)
{
	CHECK(CLASS_ExpressionNumeric__Z6absExpEV);
}

TEST(ExpressionNumeric, floor)
{
	CHECK(CLASS_ExpressionNumeric__Z8floorExpEV);
}

TEST(ExpressionNumeric, sum)
{
	CHECK(CLASS_ExpressionNumeric__Z6sumExpEV);
}

TEST(ExpressionNumeric, difference)
{
	CHECK(CLASS_ExpressionNumeric__Z13differenceExpEV);
}

TEST(ExpressionNumeric, product)
{
	CHECK(CLASS_ExpressionNumeric__Z10productExpEV);
}

TEST(ExpressionNumeric, division)
{
	CHECK(CLASS_ExpressionNumeric__Z11divisionExpEV);
}

TEST(ExpressionNumeric,intDivision )
{
	CHECK(CLASS_ExpressionNumeric__Z14intDivisionExpEV);
}
TEST(ExpressionNumeric, remainder)
{
	CHECK(CLASS_ExpressionNumeric__Z12remainderExpEV);
}
TEST(ExpressionNumeric, mod)
{
	CHECK(CLASS_ExpressionNumeric__Z6modExpEV);
}
TEST(ExpressionNumeric, power)
{
	CHECK(CLASS_ExpressionNumeric__Z8powerExpEV);
}
TEST(ExpressionNumeric,lessthan )
{
	CHECK(CLASS_ExpressionNumeric__Z11lessthanExpEV);
}
TEST(ExpressionNumeric,greaterThan )
{
	CHECK(CLASS_ExpressionNumeric__Z14greaterThanExpEV);
}
TEST(ExpressionNumeric, lessEqual)
{
	CHECK(CLASS_ExpressionNumeric__Z13lessEqualExp1EV);
}
TEST(ExpressionNumeric,greaterEqual )
{
	CHECK(CLASS_ExpressionNumeric__Z16greaterEqualExp1EV);
}
TEST(ExpressionNumeric, equal)
{
	CHECK(CLASS_ExpressionNumeric__Z8equalExpEV);
}
TEST(ExpressionNumeric,notEqual )
{
	CHECK(CLASS_ExpressionNumeric__Z11notEqualExpEV);
}

TEST(ExpressionNumeric,unaryPlus )
{
	CHECK(CLASS_ExpressionNumeric__Z9unaryPlusEV);
}

TEST(ExpressionNumeric,unaryMinus )
{
	CHECK(CLASS_ExpressionNumeric__Z10unaryMinusEV);
}
