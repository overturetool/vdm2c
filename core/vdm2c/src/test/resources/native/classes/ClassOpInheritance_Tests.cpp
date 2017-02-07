/*
 * ExpressionsSeq_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: kel
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassOpInheritanceDepth0.h"
}

#define CHECK(methodId) TVP c=_Z24ClassOpInheritanceDepth0EV(NULL);\
TVP res=CALL_FUNC(ClassOpInheritanceDepth0,ClassOpInheritanceDepth2,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, op)
{
	/* not really sure what to do here. We cast to a base class which has the op and then call it */
	CHECK(CLASS_ClassOpInheritanceDepth2__Z2opEV);
}


