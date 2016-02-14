/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionLet.h"

}

#define CHECK(methodId) TVP c=ExpressionLet._new();\
TVP res=CALL_FUNC(ExpressionLet,ExpressionLet,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ExpressionLet, letBlockScope)
{
	CHECK(CLASS_ExpressionLet__Z13letBlockScopeEV);
}

TEST(ExpressionLet, letPatternIdentifier)
{
	CHECK(CLASS_ExpressionLet__Z20letPatternIdentifierEV);
}

TEST(ExpressionLet, letPatternDontCare)
{
	CHECK(CLASS_ExpressionLet__Z18letPatternDontCareEV);
}

