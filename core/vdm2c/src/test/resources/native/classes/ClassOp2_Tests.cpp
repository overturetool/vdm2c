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
#include "ClassOp2.h"
}

#define CHECK(methodId, args...) TVP c=_Z8ClassOp2EV(NULL);\
TVP res = CALL_FUNC(ClassOp2,ClassOp2,c,methodId, args);\
EXPECT_EQ(true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, affect)
{
	CHECK(CLASS_ClassOp2__Z6affectEIB, newInt(1), newBool(true));
}
