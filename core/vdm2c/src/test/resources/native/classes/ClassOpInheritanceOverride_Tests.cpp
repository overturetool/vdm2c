/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassOpInheritanceOverrideDepth0.h"
}

#define CHECK(methodId) TVP c=_Z32ClassOpInheritanceOverrideDepth0EV(NULL);\
TVP res=CALL_FUNC(ClassOpInheritanceOverrideDepth0,ClassOpInheritanceOverrideDepth0,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_AS_BASE(methodId) TVP c=_Z32ClassOpInheritanceOverrideDepth0EV(NULL);\
TVP res=CALL_FUNC(ClassOpInheritanceOverrideDepth0,ClassOpInheritanceOverrideDepth1,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassOpInheritanceOverrideDepth0, op)
{
	/* ok this has the method, so do the super but it should not matter. Do not cast and call with our id */
	CHECK(CLASS_ClassOpInheritanceOverrideDepth0__Z2opEV);
}


TEST(ClassOpInheritanceOverrideDepth0, opAsBase)
{
	/* ok in case this was cast to the base and called with the base method id it should still work since we redirect and downcast */
	CHECK_AS_BASE(CLASS_ClassOpInheritanceOverrideDepth1__Z2opEV);
}

