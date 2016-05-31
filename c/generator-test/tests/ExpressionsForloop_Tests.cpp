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
#include "ExpressionForLoop.h"
#ifdef VDM_CG
#include "MethodNameMap.h"
#endif
}

#define CHECK(methodId) TVP c=ExpressionForLoop._new();\
TVP res=CALL_FUNC(ExpressionForLoop,ExpressionForLoop,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Expression_Seq, forindex)
{
	CHECK(CLASS_ExpressionForLoop__Z8forindexEV);
}

TEST(Expression_Seq, forset)
{
	CHECK(CLASS_ExpressionForLoop__Z6forsetEV);
}

TEST(Expression_Seq, forseq)
{
	CHECK(CLASS_ExpressionForLoop__Z6forseqEV);
}
