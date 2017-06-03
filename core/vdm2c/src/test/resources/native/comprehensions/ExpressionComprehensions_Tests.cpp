
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C" {
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionComprehensions.h"

}

#define CHECK(methodId) TVP c=_Z24ExpressionComprehensionsEV(NULL);\
TVP res=CALL_FUNC( ExpressionComprehensions, ExpressionComprehensions,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, retSetComp)
{
	CHECK(CLASS_ExpressionComprehensions__Z10retSetCompEV);
}

TEST_F(TestFlowFunctions, retMapComp)
{
	CHECK(CLASS_ExpressionComprehensions__Z10retMapCompEV);
}

TEST_F(TestFlowFunctions, letVarSeqComp)
{
	CHECK(CLASS_ExpressionComprehensions__Z13letVarSeqCompEV);
}