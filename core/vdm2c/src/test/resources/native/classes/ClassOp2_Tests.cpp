/*
 * ClassInstanceVariable_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassOp2.h"
}

#define CHECK(methodId) TVP c=_Z8ClassOp2EV(NULL);\
TVP res=CALL_FUNC(ClassOp2,ClassOp2,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassOp2, op)
{
	CHECK(CLASS_ClassOp2__Z2opEV);
}