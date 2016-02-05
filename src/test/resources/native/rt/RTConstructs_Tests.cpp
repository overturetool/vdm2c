/*
 * RTConstructs_Test.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "RTConstructs.h"

}

#define CHECK(methodId) TVP c=RTConstructs._new();\
TVP res=CALL_FUNC(RTConstructs,RTConstructs,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(RTConstructs, rtDurationRemove)
{
	CHECK(CLASS_RTConstructs__Z12testdurationEV);
}

TEST(RTConstructs, rtCyclesRemove)
{
	CHECK(CLASS_RTConstructs__Z10testcyclesEV);
}
