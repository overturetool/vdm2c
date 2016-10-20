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
#include "AAAAA.h"
#include "BBBBB.h"
}

#define CHECK(methodId) TVP b=_Z5BBBBBEV(NULL);\
TVP res=CALL_FUNC(BBBBB,AAAAA,b,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(b)


TEST(SubclassResponsibility, operation)
{
	CHECK(CLASS_BBBBB__Z2opEV);
}


TEST(SubclassResponsibility, function)
{
	CHECK(CLASS_BBBBB__Z1fEV);
}
