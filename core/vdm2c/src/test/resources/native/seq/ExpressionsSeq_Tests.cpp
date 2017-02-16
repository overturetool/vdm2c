/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"

#include <stdio.h>
#include "ExpressionSeq.h"
#ifdef VDM_CG
#include "MethodNameMap.h"
#endif
}

#define CHECK(methodId) TVP c=_Z13ExpressionSeqEV(NULL);\
TVP res=CALL_FUNC(ExpressionSeq,ExpressionSeq,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, seqHd)
{
	CHECK(CLASS_ExpressionSeq_seqHd);
}

TEST_F(TestFlowFunctions, seqTl)
{
	CHECK(CLASS_ExpressionSeq_seqTl);
}

TEST_F(TestFlowFunctions, seqLen)
{
	CHECK(CLASS_ExpressionSeq_seqLen);
}

TEST_F(TestFlowFunctions, seqElems)
{
	CHECK(CLASS_ExpressionSeq_seqElems);
}

TEST_F(TestFlowFunctions, seqInds)
{
	CHECK(CLASS_ExpressionSeq_seqInds);
}

TEST_F(TestFlowFunctions, seqConc)
{
	CHECK(CLASS_ExpressionSeq_seqConc);
}

TEST_F(TestFlowFunctions, seqReverse)
{
	CHECK(CLASS_ExpressionSeq_seqReverse);
}

//TEST_F(TestFlowFunctions, seqMod)
//{
//}

TEST_F(TestFlowFunctions, seqIndex)
{
	CHECK(CLASS_ExpressionSeq_seqIndex);
}

TEST_F(TestFlowFunctions, seqEqual)
{
	CHECK(CLASS_ExpressionSeq_seqEqual);
}

TEST_F(TestFlowFunctions, seqInEqual)
{
	CHECK(CLASS_ExpressionSeq_seqInEqual);
}
