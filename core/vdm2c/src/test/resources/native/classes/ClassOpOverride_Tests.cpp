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
#include "ClassOverrideCaller.h"
}

#define CHECK_TRUE(methodId) TVP c=_Z19ClassOverrideCallerEV(NULL);\
TVP res=CALL_FUNC(ClassOverrideCaller, ClassOverrideCaller, c, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_FALSE(methodId) TVP c=_Z19ClassOverrideCallerEV(NULL);\
TVP res=CALL_FUNC(ClassOverrideCaller, ClassOverrideCaller, c, methodId);\
EXPECT_EQ (false, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassOpOverride, op1base)
{
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z12checkBaseOp1EV);
}

TEST(ClassOpOverride, op2base)
{
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z12checkBaseOp2EV);
}

TEST(ClassOpOverride, op3base)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z12checkBaseOp3EV);
}

TEST(ClassOpOverride, op4base)
{
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z12checkBaseOp4EV);
}

TEST(ClassOpOverride, op5base)
{
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z12checkBaseOp5EV);
}

TEST(ClassOpOverride, op1override)
{	
	CHECK_TRUE(CLASS_ClassOverrideCaller__Z16checkOverrideOp1EV);
}


TEST(ClassOpOverride, op2override)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z16checkOverrideOp2EV);
}

TEST(ClassOpOverride, op3override)
{	
	CHECK_TRUE(CLASS_ClassOverrideCaller__Z16checkOverrideOp3EV);
}

/*

These two fail with segmentation faults.  This reproduces the error in the ClearSy model.


TEST(ClassOpOverride, op4override)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z16checkOverrideOp4EV);
}

TEST(ClassOpOverride, op5override)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z16checkOverrideOp5EV);
}

*/
