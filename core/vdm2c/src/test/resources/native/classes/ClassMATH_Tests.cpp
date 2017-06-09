
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include "MATHLibTest.h"
}

#define CHECK(methodId) TVP c=_Z11MATHLibTestEV(NULL);\
TVP res=CALL_FUNC(MATHLibTest,MATHLibTest,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST_F(TestFlowFunctions, useAll)
{
	CHECK(CLASS_MATHLibTest__Z6useAllEV);
}

