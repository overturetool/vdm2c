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
#include "ClassValueInheritance.h"

}

#define CHECK(methodId) TVP c = _Z21ClassValueInheritanceEV(NULL);\
TVP res = CALL_FUNC(ClassValueInheritance, ClassValueInheritance, c, methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassValueInheritance, check)
{
	CHECK(CLASS_ClassValueInheritance__Z5checkEV);
}

