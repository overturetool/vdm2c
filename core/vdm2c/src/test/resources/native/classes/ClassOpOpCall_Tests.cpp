/*
 * ClassInstanceVariable_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassOpOpCall.h"
}

#define CHECK(methodId) TVP c= _Z13ClassOpOpCallEV(NULL);\
TVP res = CALL_FUNC(ClassOpOpCall, ClassOpOpCall, c ,methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, op)
{
	CHECK(CLASS_ClassOpOpCall__Z2opEV);
}

TEST_F(TestFlowFunctions, callcall)
{
	CHECK(CLASS_ClassOpOpCall__Z8callcallEV);
}