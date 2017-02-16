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
#include "ClassOp.h"
}

#define CHECK(methodId) TVP c=_Z7ClassOpEV(NULL);\
TVP res=CALL_FUNC(ClassOp,ClassOp,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, op)
{
	CHECK(CLASS_ClassOp__Z2opEV);
}