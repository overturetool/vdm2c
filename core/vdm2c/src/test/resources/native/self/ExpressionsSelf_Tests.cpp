/*
 * ExpressionsSelf_Tests.cpp
 *
 *  Created on: Oct, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "SelfTest.h"

}

#define CHECK(methodId) TVP c=_Z8SelfTestEV(NULL);\
TVP res=CALL_FUNC(SelfTest,SelfTest,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

TEST(Expression_Self, self)
{
	CHECK(CLASS_SelfTest__Z2opEV);
}