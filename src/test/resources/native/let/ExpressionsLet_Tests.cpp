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

#define CHECK(methodId) TVP c=_Z13ExpressionLetEV(NULL);\
TVP res=CALL_FUNC(ExpressionLet,ExpressionLet,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ExpressionLet, notExp)
{
	CHECK(CLASS_ExpressionLet__Z13letBlockScopeEV);
}

TEST(ExpressionLet, andExp)
{
	CHECK(CLASS_ExpressionLet__Z20letPatternIdentifierEV);
}

TEST(ExpressionLet, orExp)
{
	CHECK(CLASS_ExpressionLet__Z18letPatternDontCareEV);
}

