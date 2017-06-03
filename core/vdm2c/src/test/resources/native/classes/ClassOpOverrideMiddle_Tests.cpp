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
#include "ClassOpOverrideMiddle3.h"
}

#define CHECK(methodId) TVP c= _Z22ClassOpOverrideMiddle3EV(NULL);\
TVP res=CALL_FUNC(ClassOpOverrideMiddle3, ClassOpOverrideMiddle3, c, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, op)
{
	CHECK(CLASS_ClassOpOverrideMiddle3__Z2opEV);
}

TEST_F(TestFlowFunctions, op2)
{
	CHECK(CLASS_ClassOpOverrideMiddle3__Z3op2EV);
}