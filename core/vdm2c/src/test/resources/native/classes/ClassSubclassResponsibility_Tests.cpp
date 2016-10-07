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
#include "A.h"
#include "B.h"
}

#define CHECK(methodId) TVP b=_Z1BEV(NULL);\
TVP res=CALL_FUNC(B,A,b,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(b)


TEST(SubclassResponsibility, operation)
{
	CHECK(CLASS_B__Z2opEV);
}


TEST(SubclassResponsibility, function)
{
	CHECK(CLASS_B__Z1fEV);
}
