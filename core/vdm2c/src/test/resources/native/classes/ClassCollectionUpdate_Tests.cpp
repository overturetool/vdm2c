

#include "gtest/gtest.h"

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


TEST(ClassCollectionUpdate, updateSeqLocal)
{
 CHECK(CLASS_ClassCollectionUpdate__Z14updateSeqLocalEV);
}

TEST(ClassCollectionUpdate, updateMapLocal)
{
 CHECK(CLASS_ClassCollectionUpdate__Z14updateMapLocalEV);
}

TEST(ClassCollectionUpdate, updateSeqInstanceVar)
{
 CHECK(CLASS_ClassCollectionUpdate__Z20updateSeqInstanceVarEV);
}

TEST(ClassCollectionUpdate, updateMapInstanceVar)
{
 CHECK(CLASS_ClassCollectionUpdate__Z20updateMapInstanceVarEV);
}

