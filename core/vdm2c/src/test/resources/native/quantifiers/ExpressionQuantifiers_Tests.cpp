
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C" {
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionQuantifiers.h"

}

#define CHECK(methodId) TVP c=_Z21ExpressionQuantifiersEV(NULL);\
TVP res=CALL_FUNC(ExpressionQuantifiers, ExpressionQuantifiers,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, forAllSimple)
{
	CHECK(CLASS_ExpressionQuantifiers__Z12forAllSimpleEV);
}

TEST_F(TestFlowFunctions, existsSimple)
{
	CHECK(CLASS_ExpressionQuantifiers__Z12existsSimpleEV);
}


