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
#include "ClassValue.h"
#include "ClassValue0.h"
}

#define CHECK(methodId) TVP c=_Z10ClassValueEV(NULL);\
TVP res=CALL_FUNC(ClassValue,ClassValue,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassValue, op1)
{
	ClassValue_const_init();
	ClassValue0_const_init();
	CHECK(CLASS_ClassValue__Z3op1EV);

	ClassValue0_const_shutdown();
	ClassValue_const_shutdown();
}

TEST(ClassValue, opOtherValue)
{
	ClassValue_const_init();
	ClassValue0_const_init();
	CHECK(CLASS_ClassValue__Z12opOtherValueEV);

	ClassValue0_const_shutdown();
	ClassValue_const_shutdown();
}

