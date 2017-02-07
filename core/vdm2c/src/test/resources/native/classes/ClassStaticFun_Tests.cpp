/*
 * ClassStaticMethodAccess_Tests.cpp
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
#include "ClassFunCall.h"
}

#define CHECK(methodId) TVP c=_Z12ClassFunCallEV(NULL);\
TVP res=CALL_FUNC(ClassFunCall,ClassFunCall,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_FALSE(methodId) TVP c=_Z12ClassFunCallEV(NULL);\
TVP res=CALL_FUNC(ClassFunCall,ClassFunCall,c,methodId);\
EXPECT_EQ (false,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, op)
{
	CHECK(CLASS_ClassFunCall__Z2opEV);
}

TEST_F(TestFlowFunctions, op2)
{
	CHECK(CLASS_ClassFunCall__Z3op2EV);
}

TEST_F(TestFlowFunctions, externalfn)
{
	CHECK(CLASS_ClassFunCall__Z10externalfnEV);
}

TEST_F(TestFlowFunctions, localfn)
{
	CHECK_FALSE(CLASS_ClassFunCall__Z7localfnEV);
}

TEST_F(TestFlowFunctions, funcInstantiation)
{
	CHECK(CLASS_ClassFunCall__Z17funcInstantiationEV);
}

TEST_F(TestFlowFunctions, twoParamFun)
{
	CHECK(CLASS_ClassFunCall__Z11twoParamFunEV);
}
