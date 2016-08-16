/*
 * ClassInstanceVariableSeqAssign_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassInstanceVariable.h"
}

#define CHECK(methodId) TVP c=_Z21ClassInstanceVariableEV(NULL);\
TVP res=CALL_FUNC(ClassInstanceVariable,ClassInstanceVariable,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassInstanceVariableSeqAssign, call)
{
	CHECK(CLASS_ClassInstanceVariable__Z13getField1TestEV);
}


