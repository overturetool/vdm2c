/*
 * ClassStaticMethodAccess_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "B.h"
}

#define CHECK(methodId)\
TVP b=_Z1BEV(NULL);\
TVP res=CALL_FUNC(B,B,b,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(b)


TEST(ClassStaticInstanceVariableComplex, testInitialisedField)
{
	A_static_init();
	CHECK(CLASS_B__Z20testInitialisedFieldEV);
}

TEST(ClassStaticInstanceVariableComplex, testUpdatedField)
{
	A_static_init();
	CHECK(CLASS_B__Z16testUpdatedFieldEV);
}

TEST(ClassStaticInstanceVariableComplex, testUpdateStaticField)
{
	A_static_init();
	CHECK(CLASS_B__Z21testUpdateStaticFieldEV);
}

TEST(ClassStaticInstanceVariableComplex, testUpdateStaticEnclosingField)
{
	B_static_init();
	CHECK(CLASS_B__Z30testUpdateStaticEnclosingFieldEV);
}


