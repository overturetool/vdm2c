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
#include "ExpressionBoolean.h"

}

#define CHECK(methodId) TVP c=_Z17ExpressionBooleanEV(NULL);\
TVP res=CALL_FUNC(ExpressionBoolean,ExpressionBoolean,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, notExp)
{
	CHECK(CLASS_ExpressionBoolean__Z6notExpEV);
}

TEST_F(TestFlowFunctions, andExp)
{
	CHECK(CLASS_ExpressionBoolean__Z6andExpEV);
}

TEST_F(TestFlowFunctions, orExp)
{
	CHECK(CLASS_ExpressionBoolean__Z5orExpEV);
}

TEST_F(TestFlowFunctions, implicationExp)
{
	CHECK(CLASS_ExpressionBoolean__Z14implicationExpEV);
}

TEST_F(TestFlowFunctions, biimplicationExp)
{
	CHECK(CLASS_ExpressionBoolean__Z16biimplicationExpEV);
}

TEST_F(TestFlowFunctions, equalityExp)
{
	CHECK(CLASS_ExpressionBoolean__Z11equalityExpEV);
}
