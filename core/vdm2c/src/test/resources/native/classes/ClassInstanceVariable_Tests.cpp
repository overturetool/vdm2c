/*
 * ClassInstanceVariable_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassInstanceVariable.h"
}

#define CHECK(methodId) TVP c=_Z21ClassInstanceVariableEV(NULL);\
TVP res=CALL_FUNC(ClassInstanceVariable,ClassInstanceVariable,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, getField1)
{
	CHECK(CLASS_ClassInstanceVariable__Z13getField1TestEV);
}


TEST_F(TestFlowFunctions, getFieldSum)
{
	CHECK(CLASS_ClassInstanceVariable__Z15getFieldSumTestEV);
}

TEST_F(TestFlowFunctions, setField1)
{
	CHECK(CLASS_ClassInstanceVariable__Z13setField1TestEV);
}

TEST_F(TestFlowFunctions, setField1UsingField2AsParam)
{
	CHECK(CLASS_ClassInstanceVariable__Z31setField1UsingField2AsParamTestEV);
}

TEST_F(TestFlowFunctions, assignField2ToField1)
{
	CHECK(CLASS_ClassInstanceVariable__Z24assignField2ToField1TestEV);
}
