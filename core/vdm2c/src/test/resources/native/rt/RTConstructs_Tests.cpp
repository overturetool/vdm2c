/*
 * RTConstructs_Test.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "RTConstructs.h"

}

#define CHECK(methodId) TVP c=_Z12RTConstructsEV(NULL);\
TVP res=CALL_FUNC(RTConstructs,RTConstructs,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, rtDurationRemove)
{
	CHECK(CLASS_RTConstructs__Z12testdurationEV);
}

TEST_F(TestFlowFunctions, rtCyclesRemove)
{
	CHECK(CLASS_RTConstructs__Z10testcyclesEV);
}

TEST_F(TestFlowFunctions, startStm)
{
	CHECK(CLASS_RTConstructs__Z8startStmEV);
}