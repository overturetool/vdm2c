/*
 * ExpressionsIs_Tests.cpp
 *
 *  Created on: Oct, 2016
 *      Author: Vicgtor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "IsTest.h"

}

#define CHECK(methodId) TVP c=_Z6IsTestEV(NULL);\
TVP res=CALL_FUNC(IsTest,IsTest,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, isInt)
{
	CHECK(CLASS_IsTest__Z9TestIsIntEV);
}

TEST_F(TestFlowFunctions, isBool)
{
	CHECK(CLASS_IsTest__Z10TestIsBoolEV);
}

TEST_F(TestFlowFunctions, isReal)
{
	CHECK(CLASS_IsTest__Z10TestIsRealEV);
}

TEST_F(TestFlowFunctions, isNotInt)
{
	CHECK(CLASS_IsTest__Z12TestIsNotIntEV);
}