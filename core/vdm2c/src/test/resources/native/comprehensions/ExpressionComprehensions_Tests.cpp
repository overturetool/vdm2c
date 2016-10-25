
#include "gtest/gtest.h"

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

TEST(ExpressionComprehensions, retSetComp)
{
	CHECK(CLASS_ExpressionComprehensions__Z10retSetCompEV);
}

TEST(ExpressionComprehensions, letVarSeqComp)
{
	CHECK(CLASS_ExpressionComprehensions__Z13letVarSeqCompEV);
}

