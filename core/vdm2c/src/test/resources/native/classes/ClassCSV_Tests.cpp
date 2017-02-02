/*
 * ClassCSV_Tests.cpp
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
#include "CSVLib.h"
}

#define CHECK(methodId) TVP c=_Z6CSVLibEV(NULL);\
TVP res=CALL_FUNC(CSVLib,CSVLib,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, lineCount)
{
	CHECK(CLASS_CSVLib__Z9lineCountEV);
}

TEST_F(TestFlowFunctions, readLine)
{
	CHECK(CLASS_CSVLib__Z8readLineEV);
}