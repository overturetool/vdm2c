#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassCollectionUpdate.h"
}

#define CHECK_TRUE(methodId) TVP c=_Z21ClassCollectionUpdateEV(NULL);\
TVP res=CALL_FUNC(ClassCollectionUpdate, ClassCollectionUpdate, c, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)



TEST(ClassScratchWork, op)
{	
	CHECK_TRUE(CLASS_ClassCollectionUpdate__Z20updateSeqInstanceVarEV);
}