

#include "gtest/gtest.h"

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


TEST(ClassIfNotEq, checkEq)
{
 CHECK(CLASS_ClassIf__Z7checkEqEV);
}

TEST(ClassIfNotEq, checkNe)
{
 CHECK(CLASS_ClassIf__Z7checkNeEV);
}



