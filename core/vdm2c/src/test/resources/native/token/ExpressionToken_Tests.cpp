
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionToken.h"
}

#define CHECK(methodId) TVP c=_Z15ExpressionTokenEV(NULL);\
TVP res=CALL_FUNC(ExpressionToken, ExpressionToken,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, tokenEq)
{
	CHECK(CLASS_ExpressionToken__Z7tokenEqEV);
}

TEST_F(TestFlowFunctions, tokenNotEq)
{
	CHECK(CLASS_ExpressionToken__Z10tokenNotEqEV);
}
