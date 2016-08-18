/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: pvj
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionQuote.h"
}

#define CHECK(methodId,args...) TVP c= _Z15ExpressionQuoteEV(NULL);\
TVP res=CALL_FUNC(ExpressionQuote,ExpressionQuote,c,methodId,args);\
EXPECT_EQ (true,equals(newQuote(QUOTE_Q2),res));\
vdmFree(res);\
vdmFree(c)

TEST(ExpressionQuote, op)
{
	CHECK(CLASS_ExpressionQuote__Z2opE2YQ1,newQuote(QUOTE_Q2));
}
