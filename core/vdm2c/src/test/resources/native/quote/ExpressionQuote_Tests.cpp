/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: pvj
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionQuote.h"
}

#define CHECK(methodId,b,args...) TVP c= _Z15ExpressionQuoteEV(NULL);\
TVP res=CALL_FUNC(ExpressionQuote,ExpressionQuote,c,methodId,args);\
EXPECT_EQ (b,equals(newQuote(QUOTE_Q2),res));\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, quoteEq)
{
	CHECK(CLASS_ExpressionQuote__Z2opE2YQ1,true,newQuote(QUOTE_Q2));
}

TEST_F(TestFlowFunctions, quoteNotEq)
{
	CHECK(CLASS_ExpressionQuote__Z2opE2YQ1,false,newQuote(QUOTE_Q1));
}
