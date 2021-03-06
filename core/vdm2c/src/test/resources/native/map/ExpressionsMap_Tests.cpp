
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionMap.h"

}

#define CHECK(methodId) TVP c=_Z13ExpressionMapEV(NULL);\
TVP res=CALL_FUNC(ExpressionMap,ExpressionMap,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, mapDom)
{
	CHECK(CLASS_ExpressionMap__Z6mapDomEV);
}

TEST_F(TestFlowFunctions, mapRng)
{
	CHECK(CLASS_ExpressionMap__Z6mapRngEV);
}

TEST_F(TestFlowFunctions, mapAccessSimple)
{
	CHECK(CLASS_ExpressionMap__Z15mapAccessSimpleEV);
}

TEST_F(TestFlowFunctions, mapEq)
{
	CHECK(CLASS_ExpressionMap__Z5mapEqEV);
}

TEST_F(TestFlowFunctions, mapNeq)
{
	CHECK(CLASS_ExpressionMap__Z6mapNeqEV);
}

TEST_F(TestFlowFunctions, mapInv)
{
	CHECK(CLASS_ExpressionMap__Z6mapInvEV);
}

TEST_F(TestFlowFunctions, mapUnion)
{
	CHECK(CLASS_ExpressionMap__Z8mapUnionEV);
}

TEST_F(TestFlowFunctions, mapOverride)
{
	CHECK(CLASS_ExpressionMap__Z11mapOverrideEV);
}

TEST_F(TestFlowFunctions, mapDistMerge)
{
	CHECK(CLASS_ExpressionMap__Z12mapDistMergeEV);
}

TEST_F(TestFlowFunctions, mapDomResTo)
{
	CHECK(CLASS_ExpressionMap__Z13mapRestrictToEV);
}

TEST_F(TestFlowFunctions, mapDomResBy)
{
	CHECK(CLASS_ExpressionMap__Z13mapRestrictByEV);
}

TEST_F(TestFlowFunctions, mapRngResTo)
{
	CHECK(CLASS_ExpressionMap__Z16mapRestrictRngToEV);
}

TEST_F(TestFlowFunctions, mapRngResBy)
{
	CHECK(CLASS_ExpressionMap__Z16mapRestrictRngByEV);
}

TEST_F(TestFlowFunctions, mapIterate)
{
	CHECK(CLASS_ExpressionMap__Z12mapIterationEV);
}

TEST_F(TestFlowFunctions, mapComp)
{
	CHECK(CLASS_ExpressionMap__Z7mapCompEV);
}
