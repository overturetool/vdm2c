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
#include "ExpressionCases.h"

}

#define CHECK(methodId) TVP c=_Z15ExpressionCasesEV(NULL);\
TVP res=CALL_FUNC(ExpressionCases,ExpressionCases,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, casesLitMatch)
{
	CHECK(CLASS_ExpressionCases__Z13casesLitMatchEV);
}


