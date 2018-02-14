
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "PreConditions.h"

}

#define CHECK(methodId) PreConditions_static_init();\
TVP c = _Z13PreConditionsEV(NULL);\
TVP res=CALL_FUNC(PreConditions,PreConditions,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c);\
PreConditions_static_init();

TEST_F(TestFlowFunctions, opPreCondTrue)
{
  CHECK(CLASS_PreConditions__Z13opPreCondTrueEV);
}

TEST_F(TestFlowFunctions, opPreCondFalse)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z14opPreCondFalseEV), "Assertion.*");
}

TEST_F(TestFlowFunctions, opNoPreSuccess)
{
  CHECK(CLASS_PreConditions__Z14opNoPreSuccessEV);
}

TEST_F(TestFlowFunctions, opFailPreCondSecondFame)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z23opFailPreCondSecondFameEV), "Assertion.*");
}

TEST_F(TestFlowFunctions, opStatePreSuccess)
{
  CHECK(CLASS_PreConditions__Z17opStatePreSuccessEV);
}

TEST_F(TestFlowFunctions, opStatePreFail)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z14opStatePreFailEV), "Assertion.*");
}

TEST_F(TestFlowFunctions, opStaticStatePreSuccess)
{
  CHECK(CLASS_PreConditions__Z23opStaticStatePreSuccessEV);
}

TEST_F(TestFlowFunctions, opStaticStatePreFail)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z20opStaticStatePreFailEV), "Assertion.*");
}

TEST_F(TestFlowFunctions, opArgsSuccess)
{
  CHECK(CLASS_PreConditions__Z23opCallOpWithArgsSuccessEV);
}

TEST_F(TestFlowFunctions, opCallOpWithArgsFail)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z20opCallOpWithArgsFailEV), "Assertion.*");
}

TEST_F(TestFlowFunctions, funcPreCondSuccess)
{
  CHECK(CLASS_PreConditions__Z18funcPreCondSuccessEV);
}

TEST_F(TestFlowFunctions, funcPreCondFail)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z15funcPreCondFailEV), "Assertion.*");
}

TEST_F(TestFlowFunctions, funcPassPreCondSecondFame)
{
  CHECK(CLASS_PreConditions__Z25funcPassPreCondSecondFameEV);
}

TEST_F(TestFlowFunctions, funcFailPreCondSecondFame)
{
  ASSERT_DEATH (CHECK(CLASS_PreConditions__Z25funcFailPreCondSecondFameEV), "Assertion.*");
}