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
#include "ClassInstanceVariableSeqAssign.h"
}

#define CHECK(methodId) TVP c=_Z30ClassInstanceVariableSeqAssignEV(NULL);\
TVP res=CALL_FUNC(ClassInstanceVariableSeqAssign,ClassInstanceVariableSeqAssign,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassInstanceVariableSeqAssign, call)
{
	CHECK(CLASS_ClassInstanceVariableSeqAssign__Z9getField0EV);
}


