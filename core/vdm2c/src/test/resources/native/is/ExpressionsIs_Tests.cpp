/*
 * ExpressionsIs_Tests.cpp
 *
 *  Created on: Oct, 2016
 *      Author: Vicgtor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "IsTest.h"

}

#define CHECK(methodId) TVP c=_Z6IsTestEV(NULL);\
TVP res=CALL_FUNC(IsTest,IsTest,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Expression_Is, isInt)
{
	CHECK(CLASS_IsTest__Z9TestIsIntEV);
}

TEST(Expression_Is, isBool)
{
	CHECK(CLASS_IsTest__Z10TestIsBoolEV);
}

TEST(Expression_Is, isReal)
{
	CHECK(CLASS_IsTest__Z10TestIsRealEV);
}

TEST(Expression_Is, isNotInt)
{
	CHECK(CLASS_IsTest__Z12TestIsNotIntEV);
}