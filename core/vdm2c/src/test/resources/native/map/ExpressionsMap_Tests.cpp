
#include "gtest/gtest.h"

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


TEST(ExpressionMap, mapDom)
{
	CHECK(CLASS_ExpressionMap__Z6mapDomEV);
}

TEST(ExpressionMap, mapRng)
{
	CHECK(CLASS_ExpressionMap__Z6mapRngEV);
}

TEST(ExpressionMap, mapAccessSimple)
{
	CHECK(CLASS_ExpressionMap__Z15mapAccessSimpleEV);
}

TEST(ExpressionMap, mapEq)
{
	CHECK(CLASS_ExpressionMap__Z5mapEqEV);
}

TEST(ExpressionMap, mapNeq)
{
	CHECK(CLASS_ExpressionMap__Z6mapNeqEV);
}
