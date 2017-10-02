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

TEST_F(TestFlowFunctions, isNatPos)
{
	CHECK(CLASS_IsTest__Z8isNatPosEV);
}

TEST_F(TestFlowFunctions, isNatNeg)
{
	CHECK(CLASS_IsTest__Z8isNatNegEV);
}
