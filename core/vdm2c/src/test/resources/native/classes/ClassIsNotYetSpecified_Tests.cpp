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
#include "ClassIsNotYetSpecified.h"
}

#define CHECK(methodId) TVP c=_Z22ClassIsNotYetSpecifiedEV(NULL);\
TVP res=CALL_FUNC(ClassIsNotYetSpecified,ClassIsNotYetSpecified,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECKVOID(methodId) TVP c=_Z22ClassIsNotYetSpecifiedEV(NULL);\
CALL_FUNC(ClassIsNotYetSpecified,ClassIsNotYetSpecified,c,methodId);\
vdmFree(c)

TEST_F(TestFlowFunctions, opNotSpecified)
{
	CHECKVOID(CLASS_ClassIsNotYetSpecified__Z14opNotSpecifiedEV);
}

TEST_F(TestFlowFunctions, funNotSpecified)
{
	CHECK(CLASS_ClassIsNotYetSpecified__Z15funNotSpecifiedEV);
}

