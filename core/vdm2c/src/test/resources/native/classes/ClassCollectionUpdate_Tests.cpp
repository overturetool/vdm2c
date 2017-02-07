

#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassCollectionUpdate.h"
}

#define CHECK(methodId) TVP c=_Z21ClassCollectionUpdateEV(NULL);\
TVP res=CALL_FUNC(ClassCollectionUpdate,ClassCollectionUpdate,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, updateSeqLocal)
{
 CHECK(CLASS_ClassCollectionUpdate__Z14updateSeqLocalEV);
}

TEST_F(TestFlowFunctions, updateMapLocal)
{
 CHECK(CLASS_ClassCollectionUpdate__Z14updateMapLocalEV);
}

TEST_F(TestFlowFunctions, updateSeqInstanceVar)
{
 CHECK(CLASS_ClassCollectionUpdate__Z20updateSeqInstanceVarEV);
}

TEST_F(TestFlowFunctions, updateMapInstanceVar)
{
 CHECK(CLASS_ClassCollectionUpdate__Z20updateMapInstanceVarEV);
}

