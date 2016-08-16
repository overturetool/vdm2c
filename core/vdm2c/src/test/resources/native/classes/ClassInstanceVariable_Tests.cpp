/*
 * ClassInstanceVariable_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

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


TEST(ClassInstanceVariable, getField1)
{
	CHECK(CLASS_ClassInstanceVariable__Z13getField1TestEV);
}


TEST(ClassInstanceVariable, getFieldSum)
{
	CHECK(CLASS_ClassInstanceVariable__Z15getFieldSumTestEV);
}

TEST(ClassInstanceVariable, setField1)
{
	CHECK(CLASS_ClassInstanceVariable__Z13setField1TestEV);
}

TEST(ClassInstanceVariable, setField1UsingField2AsParam)
{
	CHECK(CLASS_ClassInstanceVariable__Z31setField1UsingField2AsParamTestEV);
}

TEST(ClassInstanceVariable, assignField2ToField1)
{
	CHECK(CLASS_ClassInstanceVariable__Z24assignField2ToField1TestEV);
}
