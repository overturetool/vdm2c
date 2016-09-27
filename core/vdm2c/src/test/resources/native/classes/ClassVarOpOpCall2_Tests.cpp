/*
 * ClassInstanceVariable_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassVarOpOpCall2.h"
}

#define CHECK(methodId) TVP c = _Z17ClassVarOpOpCall2EV(NULL);\
TVP res = CALL_FUNC(ClassVarOpOpCall2, ClassVarOpOpCall2, c ,methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(ClassVarOpOpCall2, check)
{
	CHECK(CLASS_ClassVarOpOpCall2__Z5checkEV);
}