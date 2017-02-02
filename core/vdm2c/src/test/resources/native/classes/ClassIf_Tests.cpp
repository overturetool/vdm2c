

#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassIf.h"
}

#define CHECK(methodId) TVP c=_Z7ClassIfEV(NULL);\
TVP res=CALL_FUNC(ClassIf,ClassIf,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, checkEq)
{
 CHECK(CLASS_ClassIf__Z7checkEqEV);
}

TEST_F(TestFlowFunctions, checkNe)
{
 CHECK(CLASS_ClassIf__Z7checkNeEV);
}

TEST_F(TestFlowFunctions, opCallInIfTestTrue)
{
 CHECK(CLASS_ClassIf__Z18opCallInIfTestTrueEV);
}

TEST_F(TestFlowFunctions, opCallInIfTestFalse)
{
 CHECK(CLASS_ClassIf__Z19opCallInIfTestFalseEV);
}



