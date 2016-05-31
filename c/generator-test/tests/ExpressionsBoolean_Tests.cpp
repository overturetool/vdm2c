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
#include "ExpressionBoolean.h"

}

#define CHECK(methodId) TVP c=ExpressionBoolean._new();\
TVP res=CALL_FUNC(ExpressionBoolean,ExpressionBoolean,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Expression_Boolean, notExp)
{
	CHECK(CLASS_ExpressionBoolean__Z6notExpEV);
}

TEST(Expression_Boolean, andExp)
{
	CHECK(CLASS_ExpressionBoolean__Z6andExpEV);
}

TEST(Expression_Boolean, orExp)
{
	CHECK(CLASS_ExpressionBoolean__Z5orExpEV);
}

TEST(Expression_Boolean, implicationExp)
{
	CHECK(CLASS_ExpressionBoolean__Z14implicationExpEV);
}

TEST(Expression_Boolean, biimplicationExp)
{
	CHECK(CLASS_ExpressionBoolean__Z16biimplicationExpEV);
}

TEST(Expression_Boolean, equalityExp)
{
	CHECK(CLASS_ExpressionBoolean__Z11equalityExpEV);
}
