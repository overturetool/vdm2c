/*
 * ExpressionsIs_Tests.cpp
 *
 *  Created on: Oct, 2016
 *      Author: Vicgtor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


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

TEST_F(TestFlowFunctions, isNatPos)
{
	CHECK(CLASS_IsTest__Z8isNatPosEV);
}

TEST_F(TestFlowFunctions, isNatNeg)
{
	CHECK(CLASS_IsTest__Z8isNatNegEV);
}

TEST_F(TestFlowFunctions, isNat1Pos)
{
	CHECK(CLASS_IsTest__Z9isNat1PosEV);
}

TEST_F(TestFlowFunctions, isNat1Neg)
{
	CHECK(CLASS_IsTest__Z9isNat1NegEV);
}

TEST_F(TestFlowFunctions, isIntPos)
{
	CHECK(CLASS_IsTest__Z8isIntPosEV);
}

TEST_F(TestFlowFunctions, isIntNeg)
{
	CHECK(CLASS_IsTest__Z8isIntNegEV);
}

TEST_F(TestFlowFunctions, isRealPos)
{
	CHECK(CLASS_IsTest__Z9isRealPosEV);
}

TEST_F(TestFlowFunctions, isRealNeg)
{
	CHECK(CLASS_IsTest__Z9isRealNegEV);
}

TEST_F(TestFlowFunctions, isBoolPos)
{
	CHECK(CLASS_IsTest__Z9isBoolPosEV);
}

TEST_F(TestFlowFunctions, isBoolNeg)
{
	CHECK(CLASS_IsTest__Z9isBoolNegEV);
}

TEST_F(TestFlowFunctions, isRatPos)
{
	CHECK(CLASS_IsTest__Z8isRatPosEV);
}

TEST_F(TestFlowFunctions, isRatNeg)
{
	CHECK(CLASS_IsTest__Z8isRatNegEV);
}

TEST_F(TestFlowFunctions, isCharPos)
{
	CHECK(CLASS_IsTest__Z9isCharPosEV);
}

TEST_F(TestFlowFunctions, isCharNeg)
{
	CHECK(CLASS_IsTest__Z9isCharNegEV);
}

TEST_F(TestFlowFunctions, isTokenPos)
{
	CHECK(CLASS_IsTest__Z10isTokenPosEV);
}

TEST_F(TestFlowFunctions, isTokenNeg)
{
	CHECK(CLASS_IsTest__Z10isTokenNegEV);
}

TEST_F(TestFlowFunctions, isNatCharTuplePos)
{
	CHECK(CLASS_IsTest__Z17isNatCharTuplePosEV);
}

TEST_F(TestFlowFunctions, isNatCharTupleNeg)
{
	CHECK(CLASS_IsTest__Z17isNatCharTupleNegEV);
}

TEST_F(TestFlowFunctions, isNatOrCharPos)
{
	CHECK(CLASS_IsTest__Z14isNatOrCharPosEV);
}

TEST_F(TestFlowFunctions, isNatOrCharNeg)
{
	CHECK(CLASS_IsTest__Z14isNatOrCharNegEV);
}

TEST_F(TestFlowFunctions, isTupPos)
{
	CHECK(CLASS_IsTest__Z8isTupPosEV);
}

TEST_F(TestFlowFunctions, isTupNeg)
{
	CHECK(CLASS_IsTest__Z8isTupNegEV);
}
