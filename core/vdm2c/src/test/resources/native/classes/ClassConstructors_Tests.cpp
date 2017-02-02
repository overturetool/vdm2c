/*
 * ClassConstructors_Tests.cpp
 *
 *  Created on: Oct, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "Constructors.h"
}

#define CHECK(methodId) TVP c=_Z12ConstructorsEV(NULL);\
TVP res=CALL_FUNC(Constructors,Constructors,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_CUSTOM2(methodId, val) TVP c=_Z12ConstructorsEI(NULL, val);\
TVP res=CALL_FUNC(Constructors,Constructors,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_CUSTOM3(methodId, val1, val2) TVP c=_Z12ConstructorsEII(NULL, val1, val2);\
TVP res=CALL_FUNC(Constructors,Constructors,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)



TEST_F(TestFlowFunctions, defaultConstruction)
{
	CHECK(CLASS_Constructors__Z23TestDefaultConstructionEV);
}

TEST_F(TestFlowFunctions, custom1Construction)
{
	CHECK(CLASS_Constructors__Z23TestCustom1ConstructionEV);
}

TEST_F(TestFlowFunctions, custom2Construction)
{
	CHECK_CUSTOM2(CLASS_Constructors__Z23TestCustom2ConstructionEV, newInt(12));
}

TEST_F(TestFlowFunctions, custom3Construction)
{
	CHECK_CUSTOM3(CLASS_Constructors__Z23TestCustom3ConstructionEV, newInt(13), newInt(14));
}