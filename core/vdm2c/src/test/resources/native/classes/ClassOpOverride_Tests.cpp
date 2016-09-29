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
#include "ClassOpOverride1.h"
}

#define CHECK(methodId) TVP c=_Z16ClassOpOverride1EV(NULL);\
TVP res=CALL_FUNC(ClassOpOverride1, ClassOpOverride1, c, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)



TEST(ClassOpOverride, op)
{
	CHECK(CLASS_ClassOpOverride1__Z2opEV);
}

