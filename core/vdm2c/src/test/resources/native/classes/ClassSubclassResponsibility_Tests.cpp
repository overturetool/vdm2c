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
#include "AAAAA.h"
#include "BBBBB.h"
}

#define CHECK(methodId) TVP b=_Z5BBBBBEV(NULL);\
TVP res=CALL_FUNC(BBBBB,AAAAA,b,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(b)


TEST_F(TestFlowFunctions, operation)
{
	CHECK(CLASS_BBBBB__Z2opEV);
}


TEST_F(TestFlowFunctions, function)
{
	CHECK(CLASS_BBBBB__Z1fEV);
}
