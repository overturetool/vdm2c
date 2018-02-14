
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "NonDet.h"

}

#define CHECK(methodId) TVP c = _Z6NonDetEV(NULL);\
TVP res=CALL_FUNC(NonDet,NonDet,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c);

TEST_F(TestFlowFunctions, nonDet)
{
  CHECK(CLASS_NonDet__Z9nonDetStmEV);
}
