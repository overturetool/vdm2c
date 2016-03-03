/*
 * ClassIO_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "IOLib.h"
}

#define CHECK(methodId) TVP c=_Z5IOLibEV(NULL);\
TVP res=CALL_FUNC(IOLib,IOLib,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Other_IOLib, usePrint)
{
	CHECK(CLASS_IOLib__Z8usePrintEV);
}

