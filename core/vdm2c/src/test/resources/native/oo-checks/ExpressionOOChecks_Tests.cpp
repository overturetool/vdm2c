
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ExpressionOOChecks.h"
}

#define CHECK(methodId) ExpressionOOChecks_const_init();\
TVP c = _Z18ExpressionOOChecksEV(NULL);\
TVP res=CALL_FUNC(ExpressionOOChecks,ExpressionOOChecks,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c);\
ExpressionOOChecks_const_init();

// isofbaseclass tests

/*TEST_F(TestFlowFunctions, isofbaseclass_First)
{
  CHECK(CLASS_ExpressionOOChecks__Z19isofbaseclass_FirstEV);
}*/

TEST_F(TestFlowFunctions, isofbaseclass_Second)
{
  CHECK(CLASS_ExpressionOOChecks__Z20isofbaseclass_SecondEV);
}

/*TEST_F(TestFlowFunctions, isofbaseclass_Third)
{
  CHECK(CLASS_ExpressionOOChecks__Z19isofbaseclass_ThirdEV);
}*/

TEST_F(TestFlowFunctions, isofbaseclass_Fourth)
{
  CHECK(CLASS_ExpressionOOChecks__Z20isofbaseclass_FourthEV);
}

TEST_F(TestFlowFunctions, isofbaseclass_Fifth)
{
  CHECK(CLASS_ExpressionOOChecks__Z19isofbaseclass_FifthEV);
}

// isofclass tests

/*TEST_F(TestFlowFunctions, isofclass_First)
{
  CHECK(CLASS_ExpressionOOChecks__Z15isofclass_FirstEV);
}

TEST_F(TestFlowFunctions, isofclass_Second)
{
  CHECK(CLASS_ExpressionOOChecks__Z16isofclass_SecondEV);
}

TEST_F(TestFlowFunctions, isofclass_Third)
{
  CHECK(CLASS_ExpressionOOChecks__Z15isofclass_ThirdEV);
}

TEST_F(TestFlowFunctions, isofclass_Fourth)
{
  CHECK(CLASS_ExpressionOOChecks__Z16isofclass_FourthEV);
}

TEST_F(TestFlowFunctions, isofclass_Fifth)
{
  CHECK(CLASS_ExpressionOOChecks__Z15isofclass_FifthEV);
}

TEST_F(TestFlowFunctions, isofclass_Sixth)
{
  CHECK(CLASS_ExpressionOOChecks__Z15isofclass_SixthEV);
}*/

// samebaseclass

/*TEST_F(TestFlowFunctions, samebaseclass_First)
{
  CHECK(CLASS_ExpressionOOChecks__Z19samebaseclass_FirstEV);
}

TEST_F(TestFlowFunctions, samebaseclass_Second)
{
  CHECK(CLASS_ExpressionOOChecks__Z20samebaseclass_SecondEV);
}

TEST_F(TestFlowFunctions, samebaseclass_Third)
{
  CHECK(CLASS_ExpressionOOChecks__Z19samebaseclass_ThirdEV);
}

TEST_F(TestFlowFunctions, samebaseclass_Fourth)
{
  CHECK(CLASS_ExpressionOOChecks__Z20samebaseclass_FourthEV);
}*/

TEST_F(TestFlowFunctions, samebaseclass_Fifth)
{
  CHECK(CLASS_ExpressionOOChecks__Z19samebaseclass_FifthEV);
}