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
#include "ClassDepth0.h"
}

#define CHECK(methodId) TVP c=_Z11ClassDepth0EV(NULL);\
TVP res=CALL_FUNC(ClassDepth0,ClassDepth0,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)



TEST(ClassDepth0, testSumFields)
{
	CHECK(CLASS_ClassDepth0__Z8checkSumEV);
}

TEST(ClassDepth0, testSetInheritedField)
{
	CHECK(CLASS_ClassDepth0__Z17checkSetInheritedEV);
}
