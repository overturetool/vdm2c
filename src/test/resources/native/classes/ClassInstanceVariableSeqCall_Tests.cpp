/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassInstanceVariableSeqCaller.h"
}

#define CHECK(methodId) TVP c=_Z30ClassInstanceVariableSeqCallerEV(NULL);\
TVP res=CALL_FUNC(ClassInstanceVariableSeqCaller,ClassInstanceVariableSeqCaller,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassInstanceVariableSeqCaller, call)
{
	CHECK(CLASS_ClassInstanceVariableSeqCaller__Z4callEV);
}


