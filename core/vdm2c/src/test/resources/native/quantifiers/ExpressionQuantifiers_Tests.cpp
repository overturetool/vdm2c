
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

TEST_F(TestFlowFunctions, forAllFirst)
{
	CHECK(CLASS_ExpressionQuantifiers__Z11forAllFirstEV);
}

TEST_F(TestFlowFunctions, forAllSecond)
{
	CHECK(CLASS_ExpressionQuantifiers__Z12forAllSecondEV);
}


TEST_F(TestFlowFunctions, existsFirst)
{
	CHECK(CLASS_ExpressionQuantifiers__Z11existsFirstEV);
}

TEST_F(TestFlowFunctions, existsSecond)
{
	CHECK(CLASS_ExpressionQuantifiers__Z12existsSecondEV);
}

TEST_F(TestFlowFunctions, exists1First)
{
	CHECK(CLASS_ExpressionQuantifiers__Z12exists1FirstEV);
}

TEST_F(TestFlowFunctions, exists1Second)
{
	CHECK(CLASS_ExpressionQuantifiers__Z13exists1SecondEV);
}
