/*
 * ClassCSV_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

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

TEST(Other_CSVLib, lineCount)
{
	CHECK(CLASS_CSVLib__Z9lineCountEV);
}

TEST(Other_CSVLib, readLine)
{
	CHECK(CLASS_CSVLib__Z8readLineEV);
}