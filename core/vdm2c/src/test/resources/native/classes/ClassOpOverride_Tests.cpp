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

//Call operations in base class.
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



//Call first set of overriding operations.
TEST(ClassOpOverride, op1override1)
{	
	CHECK_TRUE(CLASS_ClassOverrideCaller__Z16checkOverrideOp1EV);
}

TEST(ClassOpOverride, op2override1)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z16checkOverrideOp2EV);
}

TEST(ClassOpOverride, op3override1)
{	
	CHECK_TRUE(CLASS_ClassOverrideCaller__Z16checkOverrideOp3EV);
}

TEST(ClassOpOverride, op4override1)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z16checkOverrideOp4EV);
}

TEST(ClassOpOverride, op5override1)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z16checkOverrideOp5EV);
}



//Call second set of overriding operations.
TEST(ClassOpOverride, op1override2)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z17check2OverrideOp1EV);
}

TEST(ClassOpOverride, op2override2)
{	
	CHECK_TRUE(CLASS_ClassOverrideCaller__Z17check2OverrideOp2EV);
}

TEST(ClassOpOverride, op3override2)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z17check2OverrideOp3EV);
}

TEST(ClassOpOverride, op4override2)
{	
	CHECK_TRUE(CLASS_ClassOverrideCaller__Z17check2OverrideOp4EV);
}

TEST(ClassOpOverride, op5override2)
{	
	CHECK_FALSE(CLASS_ClassOverrideCaller__Z17check2OverrideOp5EV);
}