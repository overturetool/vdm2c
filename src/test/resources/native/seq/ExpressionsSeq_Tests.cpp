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
#include "ExpressionSeq.h"
#ifdef VDM_CG
#include "MethodNameMap.h"
#endif
}

#define CHECK(methodId) TVP c=ExpressionSeq._new();\
TVP res=CALL_FUNC(ExpressionSeq,ExpressionSeq,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Expression_Seq, seqHd)
{
	CHECK(CLASS_ExpressionSeq_seqHd);
}

TEST(Expression_Seq, seqTl)
{
	CHECK(CLASS_ExpressionSeq_seqTl);
}

TEST(Expression_Seq, seqLen)
{
	CHECK(CLASS_ExpressionSeq_seqLen);
}

TEST(Expression_Seq, seqElems)
{
	CHECK(CLASS_ExpressionSeq_seqElems);
}

TEST(Expression_Seq, seqInds)
{
	CHECK(CLASS_ExpressionSeq_seqInds);
}

TEST(Expression_Seq, seqConc)
{
	CHECK(CLASS_ExpressionSeq_seqConc);
}

TEST(Expression_Seq, seqReverse)
{
	CHECK(CLASS_ExpressionSeq_seqReverse);
}

//TEST(Expression_Seq, seqMod)
//{
//}

TEST(Expression_Seq, seqIndex)
{
	CHECK(CLASS_ExpressionSeq_seqIndex);
}

TEST(Expression_Seq, seqEqual)
{
	CHECK(CLASS_ExpressionSeq_seqEqual);
}

TEST(Expression_Seq, seqInEqual)
{
	CHECK(CLASS_ExpressionSeq_seqInEqual);
}
