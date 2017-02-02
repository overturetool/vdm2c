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
#include "ClassVarOpOpCall.h"
}

#define CHECK(methodId) TVP c= _Z16ClassVarOpOpCallEV(NULL);\
TVP res = CALL_FUNC(ClassVarOpOpCall, ClassVarOpOpCall, c ,methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, callcall)
{
	CHECK(CLASS_ClassVarOpOpCall__Z11varcallcallEV);
}