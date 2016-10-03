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
#include "ClassOpOverride2.h"
}

#define CHECK_TRUE(methodId) TVP c=_Z16ClassOpOverride1EV(NULL);\
TVP res=CALL_FUNC(ClassOpOverride1, ClassOpOverride1, c, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_FALSE(methodId) TVP c=_Z16ClassOpOverride2EV(NULL);\
TVP res=CALL_FUNC(ClassOpOverride2, ClassOpOverride2, c, methodId);\
EXPECT_EQ (false, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassOpOverride, op)
{
	CHECK_TRUE(CLASS_ClassOpOverride1__Z2opEV);
}

TEST(ClassOpOverride, second_op)
{
	CHECK_FALSE(CLASS_ClassOpOverride2__Z2opEV);
}