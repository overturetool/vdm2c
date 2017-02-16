/*
 * ClassInstanceVariableSeqAssign_Tests.cpp
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
#include "ClassInstanceVariableSeqAssign.h"
}

#define CHECK(methodId,expected) TVP c=_Z30ClassInstanceVariableSeqAssignEV(NULL);\
TVP res=CALL_FUNC(ClassInstanceVariableSeqAssign,ClassInstanceVariableSeqAssign,c,methodId);\
EXPECT_EQ (true,vdmEquals(res, expected)->value.boolVal);\
vdmFree(res);\
vdmFree(expected);\
vdmFree(c)


TEST_F(TestFlowFunctions, call)
{
	TVP expected = newSeqVar(1, newInt(9));
	CHECK(CLASS_ClassInstanceVariableSeqAssign__Z9getField0EV,expected);
}


