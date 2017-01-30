
#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "A.h"
}

#define CHECK(methodId) TVP a=_Z1AEV(NULL);\
TVP res=CALL_FUNC(A,A,a,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(a)

TEST(Tuples, tupValCreation)
{
	CHECK(CLASS_A__Z14tupValCreationEV);
}

TEST(Tuples, fieldRead)
{
	CHECK(CLASS_A__Z9fieldReadEV);
}
